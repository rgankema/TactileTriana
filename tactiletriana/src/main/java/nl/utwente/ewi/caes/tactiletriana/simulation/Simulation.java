/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Richard
 */
public class Simulation extends LoggingEntity {
    public static final int NUMBER_OF_HOUSES = 6;   // number of houses
    public static final int SYSTEM_TICK_TIME = 200;        // time between ticks in ms
    public static final int SIMULATION_TICK_TIME = 5;   // time in minutes that passes in the simulation with each tick
    
    public static final double LONGITUDE = 6.897;
    public static final double LATITUDE = 52.237;

    
    private final Transformer transformer;
    private final Map<Node, Double> lastVoltageByNode;
    private final Map<LocalDateTime, Double> temperatureByTime;
    private final Map<LocalDateTime, Double> radianceByTime;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private IController controller;
    
    protected static Simulation instance;
    
    public static Simulation getInstance(){
        if (instance == null){
            instance = new Simulation();
        }
        return instance;
    }
    
    protected Simulation() {
        super(LoggedValueType.POWER, "Network");
        
        setAbsoluteMaximum(250 * 500);
        
        // keep an array of nodes for later reference
        this.lastVoltageByNode = new HashMap<>();
        
        // de tree maken
        transformer = new Transformer();
        
        Node[] internalNodes = new Node[NUMBER_OF_HOUSES];
        Node[] houseNodes = new Node[NUMBER_OF_HOUSES];
        Cable[] cables = new Cable[NUMBER_OF_HOUSES];
        House[] houses = new House[NUMBER_OF_HOUSES];
        
        // maak huizen aan met cables en dat soort grappen
        for(int i = 0; i <= NUMBER_OF_HOUSES-1; i ++){
            houses[i] = new House();
            
            houseNodes[i] = new Node(houses[i]);
            internalNodes[i] = new Node(null);
            Cable houseCable = new Cable(houseNodes[i], 110);
            internalNodes[i].getCables().add(houseCable);
            
            cables[i] = new Cable(internalNodes[i], 110 + (NUMBER_OF_HOUSES - i) * 60);
            if (i == 0) {
                transformer.getCables().add(cables[i]);
            }
            else {
                internalNodes[i-1].getCables().add(cables[i]);
            }
            
            lastVoltageByNode.put(internalNodes[i], 230d);
            lastVoltageByNode.put(houseNodes[i], 230d);
        }
        
        // initialise time
        setCurrentTime(LocalDateTime.of(2014, 7, 1, 0, 0));
        
        // load KNMI data
        temperatureByTime = new HashMap<>();
        radianceByTime = new HashMap<>();
        
        try {
            Stream<String> dataset = Files.lines(Paths.get("src/main/resources/datasets/KNMI_dataset.txt"));
            dataset.filter(line -> !line.startsWith("#"))
                   .forEachOrdered(line -> { 
                       String[] tokens = line.split(",");
                       // tokens[1] = YYYYMMDD, tokens[2] = hour, tokens[3] = temperature, tokens[4] = radiance
                       int year = Integer.valueOf(tokens[1].trim().substring(0, 4));
                       int month = Integer.valueOf(tokens[1].trim().substring(4, 6));
                       int day = Integer.valueOf(tokens[1].trim().substring(6));
                       int hour = Integer.valueOf(tokens[2].trim());
                       double temperature = Double.valueOf(tokens[3].trim());
                       double radiance = Double.valueOf(tokens[4].trim());
                       
                       LocalDateTime date = LocalDateTime.of(year, month, day, hour - 1, 0, 0);
                       
                       temperatureByTime.put(date, temperature);
                       radianceByTime.put(date, radiance);
                   });
        } catch (Exception e) {
            throw new RuntimeException("Could not load KNMI dataset", e);
        }
        
    }
    
    // PROPERTIES
    
    /**
     * @return whether the simulation has been initialized
     */
    public static boolean isInitialized(){
        return (instance != null);
    }
    
    /**
     * The current time in the simulation.
     */
    private final ObjectProperty<LocalDateTime> currentTime = new SimpleObjectProperty<>();
    
    public ObjectProperty<LocalDateTime> currentTimeProperty() {
        return currentTime;
    }
    
    public LocalDateTime getCurrentTime() {
        return currentTimeProperty().get();
    }
    
    private void setCurrentTime(LocalDateTime time) {
        currentTimeProperty().set(time);
    }
    
    /**
     * 
     * @return the temperature right now, in degrees Celsius
     */
    public double getTemperature() {
        LocalDateTime currentTime = getCurrentTime();
        LocalDateTime prevHour, nextHour;
        int minutes = currentTime.getMinute();
        prevHour = currentTime.minusMinutes(minutes);
        nextHour = currentTime.plusMinutes(60 - minutes);
        double prevHourWeight = ((double)(60 - minutes)) / 60d;
        double nextHourWeight = ((double)minutes) / 60d;
        return prevHourWeight*(temperatureByTime.get(prevHour) / 10d) + nextHourWeight*(temperatureByTime.get(nextHour) / 10d);
    }
    
    /**
     * 
     * @return the radiance right now, in J/cm^2
     */
    public double getRadiance() {
        LocalDateTime currentTime = getCurrentTime();
        LocalDateTime prevHour, nextHour;
        int minutes = currentTime.getMinute();
        prevHour = currentTime.minusMinutes(minutes);
        nextHour = currentTime.plusMinutes(60 - minutes);
        double prevHourWeight = ((double)(60 - minutes)) / 60d;
        double nextHourWeight = ((double)minutes) / 60d;
        return prevHourWeight*radianceByTime.get(prevHour) + nextHourWeight*radianceByTime.get(nextHour);
    }
    
    /**
     * The Controller that controls the devices in this simulation. May be null.
     * @return 
     */
    public IController getController() {
        return controller;
    }
    
    public void setController(IController controller) {
        this.controller = controller;
    }
    
    /**
     * The root of the network.
     * @return 
     */
    public Transformer getTransformer() {
        return transformer;
    }
    
    // PUBLIC METHODS
    
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            // Todo: optimize dit, dit is slechts een hotfix
            // Uiteraard nogal idioot om de hele meuk op de JavaFX thread te draaien
            Platform.runLater(() -> { 
                getTransformer().tick(this, true);
                initiateForwardBackwardSweep();
                
                // Log total power consumption in network
                log(getCurrentTime(), transformer.getCables().get(0).getCurrent() * 230d);
                
                // Increment time
                setCurrentTime((getCurrentTime().plusMinutes(SIMULATION_TICK_TIME)));
            });
            
            
        }, SYSTEM_TICK_TIME, SYSTEM_TICK_TIME, TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        scheduler.shutdown();
    }
    
    // FORWARD BACKWARD SWEEP METHODS

    // Start the forward backward sweep algorithm
    private void initiateForwardBackwardSweep() {
        // First reset the nodes.
        transformer.reset();
        // Run the ForwardBackwardSweep Load-flow calculation until converged or the iteration limit is reached
        for(int i = 0; i < 20; i++) {
            transformer.doForwardBackwardSweep(230); // this runs recursivly down the tree
            
            if (hasFBSConverged(0.0001)) break;
            
            // Store last voltage to check for convergence
            for (Node node : this.lastVoltageByNode.keySet()) {
                lastVoltageByNode.put(node, node.getVoltage());
            }
        }
    }
    
    // Calculate if the FBS algorithm has converged. 
    private boolean hasFBSConverged(double error) {
        boolean result = true;
        
        //Loop through the network-tree and compare the previous voltage from each with the current voltage.
        //If the difference between the previous and current voltage is smaller than the given error, the result is true
        for (Node node : this.lastVoltageByNode.keySet()) {
            if (!result) break;
            result = (Math.abs(lastVoltageByNode.get(node) - node.getVoltage()) < error);
        }
        return result;
    }
}

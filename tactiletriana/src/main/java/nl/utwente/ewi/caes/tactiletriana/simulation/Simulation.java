/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.UncontrollableLoad;

/**
 *
 * @author Richard
 */
public class Simulation extends LoggingEntityBase {

    public static enum SimulationState { RUNNING, PAUSED, STOPPED };
    public static final int NUMBER_OF_HOUSES = 6;
    
    private static final TimeScenario DEFAULT_SCENARIO = new TimeScenario();
    static {
        DEFAULT_SCENARIO.add(new TimeScenario.TimeSpan(LocalDateTime.of(LocalDate.of(2014, 1, 1), LocalTime.MIN), 
                LocalDateTime.of(LocalDate.of(2014, 12, 31), LocalTime.MAX)));
    }

    private final Transformer transformer;
    private final Map<Node, Double> lastVoltageByNode;
    private final Map<LocalDateTime, Double> temperatureByTime;
    private final Map<LocalDateTime, Double> radianceByTime;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private IController controller;

    private Node[] internalNodes;
    private Node[] houseNodes;
    private Cable[] internalCables;
    private House[] houses;
    
    public Simulation() {
        super(null, "Network", QuantityType.POWER);
        this.setSimulation(this);   // LoggingEntityBase needs reference to Simulation for time
        
        this.setState(SimulationState.STOPPED);

        // keep an array of nodes for later reference
        this.lastVoltageByNode = new HashMap<>();

        // de tree maken
        this.transformer = new Transformer(this);

        this.internalNodes = new Node[NUMBER_OF_HOUSES];
        this.internalCables = new Cable[NUMBER_OF_HOUSES];
        this.houseNodes = new Node[NUMBER_OF_HOUSES];
        this.houses = new House[NUMBER_OF_HOUSES];

        // maak huizen aan met cables en dat soort grappen
        for (int i = 0; i <= NUMBER_OF_HOUSES - 1; i++) {
            this.houses[i] = new House(this);

            if (SimulationConfig.SIMULATION_UNCONTROLABLE_LOAD_ENABLED) {
                houses[i].getDevices().add(new UncontrollableLoad(i, this.simulation));
            }

            this.houseNodes[i] = new Node(houses[i], this);
            this.internalNodes[i] = new Node(null, this);
            Cable houseCable = new Cable(houseNodes[i], 110, 5, this);
            this.internalNodes[i].getCables().add(houseCable);

            this.internalCables[i] = new Cable(internalNodes[i], 110 + (NUMBER_OF_HOUSES - i) * 60, 20, simulation);
            if (i == 0) {
                transformer.getCables().add(internalCables[i]);
            } else {
                internalNodes[i - 1].getCables().add(internalCables[i]);
            }

            lastVoltageByNode.put(internalNodes[i], 230d);
            lastVoltageByNode.put(houseNodes[i], 230d);
        }

        // initialise time
        setCurrentTime(getTimeScenario().getStart());
        
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
     * The state of the simulation.
     */
    private final ReadOnlyObjectWrapper<SimulationState> state = new ReadOnlyObjectWrapper<>();
    
    public ReadOnlyObjectProperty<SimulationState> stateProperty() {
        return state.getReadOnlyProperty();
    }

    private void setState(SimulationState state){
        this.state.set(state);
    }
    
    public SimulationState getState(){
        return this.stateProperty().get();
    }
    
    /**
     * The time scenario that this simulation follows.
     */
    private final ObjectProperty<TimeScenario> timeScenario = new SimpleObjectProperty<TimeScenario>(DEFAULT_SCENARIO) {
        @Override
        public void set(TimeScenario value) {
            
            value.addNewTimeSpanStartedCallback(t -> {
                clearAllLogs();
            });
            
            super.set(value);
        }
    };
    
    public ObjectProperty<TimeScenario> timeScenarioProperty() {
        return timeScenario;
    }
    
    public final TimeScenario getTimeScenario() {
        return timeScenarioProperty().get();
    }
    
    public final void setTimeScenario(TimeScenario timeScenario) {
        timeScenarioProperty().set(timeScenario);
    }

    /**
     * The current time in the simulation.
     */
    private final ReadOnlyObjectWrapper<LocalDateTime> currentTime = new ReadOnlyObjectWrapper<>();

    public ReadOnlyObjectProperty<LocalDateTime> currentTimeProperty() {
        return currentTime.getReadOnlyProperty();
    }

    public final LocalDateTime getCurrentTime() {
        return currentTimeProperty().get();
    }

    protected final void setCurrentTime(LocalDateTime time) {
        currentTime.set(time);
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
        double prevHourWeight = ((double) (60 - minutes)) / 60d;
        double nextHourWeight = ((double) minutes) / 60d;
        return prevHourWeight * (temperatureByTime.get(prevHour) / 10d) + nextHourWeight * (temperatureByTime.get(nextHour) / 10d);
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
        double prevHourWeight = ((double) (60 - minutes)) / 60d;
        double nextHourWeight = ((double) minutes) / 60d;
        return prevHourWeight * radianceByTime.get(prevHour) + nextHourWeight * radianceByTime.get(nextHour);
    }

    /**
     * The Controller that controls the devices in this simulation. May be null.
     *
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
     *
     * @return
     */
    public Transformer getTransformer() {
        return transformer;
    }
    
    /**
     * Get the houses in the network. 
     * 
     * @return array of House object used in the Simulation
     */
    public House[] getHouses() {
        return houses;
    }
    
    // PUBLIC METHODS
    
    /**
     * Starts the simulation if it is stopped, or resumes it if it is paused.
     * If the simulation is stopped, it will start a new thread to run the simulation
     * on.
     */
    public void start() {
        if (getState() == SimulationState.STOPPED){            
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                if (this.getState() == SimulationState.RUNNING){
                    tick();
                }
            }, SimulationConfig.SYSTEM_TICK_TIME, SimulationConfig.SYSTEM_TICK_TIME, TimeUnit.MILLISECONDS);
        }
        setState(SimulationState.RUNNING);
    }

    /**
     * Called at the start of each tick
     */
    protected final void tick() {
        // Run anything that involves the UI on the JavaFX thread
        runOnJavaFXThreadSynchronously(() -> {
            getTransformer().tick(SimulationConfig.SIMULATION_TICK_TIME, true);
        });
        
        // Reset the nodes.
        transformer.prepareForwardBackwardSweep();
        // Run the ForwardBackwardSweep Load-flow calculation until converged or the iteration limit is reached
        for (int i = 0; i < 20; i++) {
            transformer.doForwardBackwardSweep(230);

            if (hasFBSConverged(0.0001)) {
                break;
            }

            // Store last voltage to check for convergence
            for (Node node : this.lastVoltageByNode.keySet()) {
                lastVoltageByNode.put(node, node.getVoltage());
            }
        }
        
        // Run anything that involves the UI on the JavaFX thread
        runOnJavaFXThreadSynchronously(() -> {
            // Finish forward backward sweep
            transformer.finishForwardBackwardSweep();

            // Log total power consumption in network
            log(transformer.getCables().get(0).getCurrent() * 230d);

            // Increment time
            setCurrentTime((getTimeScenario().getNext(getCurrentTime(), SimulationConfig.SIMULATION_TICK_TIME)));
        });
    }

    /**
     * Pauses the simulation if it is running. Unlike stop, this method does not
     * shut down the simulation thread.
     */
    public void pause() {
        if (getState() != SimulationState.STOPPED) {
            setState(SimulationState.PAUSED);
        }
    }

    /**
     * Stops the simulation if it is running or paused. Shuts down the background thread
     * that is used for the simulation.
     */
    public void stop() {
        if (getState() != SimulationState.STOPPED) {
            scheduler.shutdownNow();
            setState(SimulationState.STOPPED);
        }
    }

    /**
     * Stops the simulation, shuts down background thread, and resets all values to default.
     */
    public void reset() {
        stop();

        for (House house : houses) {
            house.getDevices().clear();
        }
        clearAllLogs();

        setCurrentTime(getTimeScenario().getStart());
    }

    // FORWARD BACKWARD SWEEP METHODS
    
    // Calculate if the FBS algorithm has converged. 
    private boolean hasFBSConverged(double error) {
        boolean result = true;

        //Loop through the network-tree and compare the previous voltage from each with the current voltage.
        //If the difference between the previous and current voltage is smaller than the given error, the result is true
        for (Node node : this.lastVoltageByNode.keySet()) {
            result = (Math.abs(lastVoltageByNode.get(node) - node.getVoltage()) < error);
            if (!result) {
                break;
            }
        }
        return result;
    }
    
    /**
     * Return the timestep, the time covered in each step of the simulation, currently used in the simulation.
     * 
     * @return the timestep of the simulation 
     */
    public int getTimeStep() {
        return SimulationConfig.SIMULATION_TICK_TIME;
    }

    // HELP METHODS
    
    /**
     * Runs a given task on the JavaFX thread, and blocks until the task
     * is done.
     * 
     * @param task that needs to be run on the JavaFX thread
     */
    private void runOnJavaFXThreadSynchronously(Runnable task) {
        
        if (Platform.isFxApplicationThread()) {
            task.run();
        } else {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.runLater(() -> {
                task.run();
                latch.countDown();
            });
            // Wait until the JavaFX thread is done to avoid synchronization
            // issues
            try {
                latch.await();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    private void clearAllLogs() {
        for (House house : houses) {
            house.getLog().clear();
        }
        for (Node node : internalNodes) {
            node.getLog().clear();
            for (Cable cable : node.getCables()) {
                cable.getLog().clear();
            }
        }
        for (Node node : houseNodes) {
            node.getLog().clear();
        }
        transformer.getLog().clear();
        for (Cable c : transformer.getCables()) {
            c.getLog().clear();
        }
        this.getLog().clear();
    }
}

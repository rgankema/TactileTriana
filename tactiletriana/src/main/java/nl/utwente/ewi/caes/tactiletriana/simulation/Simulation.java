/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
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

    
    // de verschillende stati van de simulation
    // INITIALIZED: alles is aangemaakt maar nog niks runt
    // RUNNING: de simulation is running -> hij voert tick() uit
    // PAUSED: de simulatie is gepauzeerd
    // STOPPED: de simulatie is gestopt
    
    public static enum SimulationState { INITIALIZED, RUNNING, PAUSED, STOPPED };

    

    private final Transformer transformer;
    private final Map<Node, Double> lastVoltageByNode;
    private final Map<LocalDateTime, Double> temperatureByTime;
    private final Map<LocalDateTime, Double> radianceByTime;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private IController controller;

    private Node[] internalNodes;
    private Node[] houseNodes;
    private Cable[] cables;
    private House[] houses;

    public Simulation() {
        super(LoggedValueType.POWER, "Network", null);
        this.setSimulation(this);
        
        this.setState(SimulationState.INITIALIZED);

        
        // wat doet dit?
        setAbsoluteMaximum(250 * 500);

        // keep an array of nodes for later reference
        this.lastVoltageByNode = new HashMap<>();

        // de tree maken
        this.transformer = new Transformer(this);

        this.internalNodes = new Node[SimulationConfig.SIMULATION_NUMBER_OF_HOUSES];
        this.houseNodes = new Node[SimulationConfig.SIMULATION_NUMBER_OF_HOUSES];
        this.cables = new Cable[SimulationConfig.SIMULATION_NUMBER_OF_HOUSES];
        this.houses = new House[SimulationConfig.SIMULATION_NUMBER_OF_HOUSES];

        // maak huizen aan met cables en dat soort grappen
        for (int i = 0; i <= SimulationConfig.SIMULATION_NUMBER_OF_HOUSES - 1; i++) {
            this.houses[i] = new House(this);

            if (SimulationConfig.SIMULATION_UNCONTROLABLE_LOAD_ENABLED) {
                houses[i].getDevices().add(new UncontrollableLoad(i, this.simulation));
            }

            this.houseNodes[i] = new Node(houses[i], this);
            this.internalNodes[i] = new Node(null, this);
            Cable houseCable = new Cable(houseNodes[i], 110, this);
            this.internalNodes[i].getCables().add(houseCable);

            this.cables[i] = new Cable(internalNodes[i], 110 + (SimulationConfig.SIMULATION_NUMBER_OF_HOUSES - i) * 60, simulation);
            if (i == 0) {
                transformer.getCables().add(cables[i]);
            } else {
                internalNodes[i - 1].getCables().add(cables[i]);
            }

            lastVoltageByNode.put(internalNodes[i], 230d);
            lastVoltageByNode.put(houseNodes[i], 230d);
        }

        // initialise time
        setCurrentTime(SimulationConfig.SIMULATION_START_TIME);

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

    protected void setCurrentTime(LocalDateTime time) {
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
     *
     * @return a copy of the array of houses in this simulation
     */
    public House[] getHouses() {
        return Arrays.copyOf(houses, houses.length);
    }

    // PUBLIC METHODS
    // start, pause en reset kan ongetwijfeld allemaal veel mooier.
    public void start() {
        this.setState(SimulationState.RUNNING);
    }

    protected final void tick() {
        // Run anything that involves the UI on the JavaFX thread
        runOnJavaFXThreadSynchronously(() -> {
            getTransformer().tick(this, true);
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
            setCurrentTime((getCurrentTime().plusMinutes(SimulationConfig.SIMULATION_TICK_TIME)));
        });
    }

    public void pause() {
        this.setState(SimulationState.PAUSED);
    }

    public void stop() {
        this.setState(SimulationState.STOPPED);
        
    }

    public void reset() {
        this.setState(SimulationState.INITIALIZED);

        scheduler.shutdownNow();
        scheduler = Executors.newScheduledThreadPool(1);

        for (House house : houses) {
            house.getDevices().clear();
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

        setCurrentTime(SimulationConfig.SIMULATION_START_TIME);
        getLog().clear();

        
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
            Platform.runLater( () -> {
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

    /**
     * Bestuurt of de simulation tick()s doet. Ook zet hij de waarden van started en running
     * @param state de state waarin de simulation gezet moet worden.
     */
    public void setState(SimulationState state){
        switch (state){
            case STOPPED:
                scheduler.shutdown();
                this.started.set(true);
                this.running.set(false);
                break;
            case PAUSED:
                this.running.set(false);
                this.started.set(true);
                break;
            case INITIALIZED:
                this.running.set(false);
                this.started.set(false);
                break;
            case RUNNING:
                // hij word op running gezet maar heeft nog niet gerunt
                this.running.set(true);
                this.started.set(true);
                if (this.getState() == SimulationState.INITIALIZED){
                    scheduler.scheduleAtFixedRate(() -> {

                        // zo lang hij runt doe een tick. Dus als hij PAUSED staat niet.
                        if (this.getState() == SimulationState.RUNNING){
                            tick();
                        }

                    }, SimulationConfig.SYSTEM_TICK_TIME, SimulationConfig.SYSTEM_TICK_TIME, TimeUnit.MILLISECONDS);
                }
                break;                    
        }
        this.state.set(state);
    }
    public SimulationState getState(){
        return this.stateProperty().get();
    }
    
    // PROPERTIES
    /**
     * Whether the Simulation has been started. This can be true even when the
     * Simulation is not running. Resetting the Simulation will revert it to
     * false.
     */
    private final ReadOnlyObjectWrapper<SimulationState> state = new ReadOnlyObjectWrapper<>();

    
    public ReadOnlyObjectProperty<SimulationState> stateProperty() {
        return state.getReadOnlyProperty();
    }
    
    // PROPERTIES
    /**
     * Whether the Simulation has been started. This can be true even when the
     * Simulation is not running. Resetting the Simulation will revert it to
     * false.
     */
    private final ReadOnlyBooleanWrapper started = new ReadOnlyBooleanWrapper(false);

    /**
     * Checkt of de simulation ooit gestart is geweest
     * @return true als de simulation ooit gestart is geweest. Dit is zo als hij niet meer op INITIALIZED staat.
     */
    public boolean isStarted() {
        return startedProperty().get();
    }

    private void setStarted(boolean started) {
        if (started){
            this.setState(SimulationState.RUNNING);
        }
        else{
            this.setState(SimulationState.STOPPED);
        }
    }

    public ReadOnlyBooleanProperty startedProperty() {
        return started.getReadOnlyProperty();
    }

    /**
     * Whether the Simulation is currently running
     */
    private final ReadOnlyBooleanWrapper running = new ReadOnlyBooleanWrapper(false);

    public boolean isRunning() {
        return runningProperty().get();
    }
    private void setRunning(boolean running) {
        if (running){
            this.setState(SimulationState.RUNNING);
        }
        else{
            this.setState(SimulationState.PAUSED);
        }
    }

    public ReadOnlyBooleanProperty runningProperty() {
        return running.getReadOnlyProperty();
    }

}

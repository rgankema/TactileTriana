/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import static nl.utwente.ewi.caes.tactiletriana.Concurrent.runOnJavaFXThreadSynchronously;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import static nl.utwente.ewi.caes.tactiletriana.Util.toTimeStep;
import static nl.utwente.ewi.caes.tactiletriana.simulation.Simulation.NUMBER_OF_HOUSES;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.WeatherData;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.UncontrollableLoad;

/**
 *
 * @author Richard
 */
public abstract class SimulationBase extends LoggingEntityBase {
    // CONSTANTS
    public static final int NUMBER_OF_HOUSES = 6;

    // FIELDS
    // API
    private IController controller;
    // Network
    protected final Transformer transformer;
    protected final Node[] internalNodes;
    protected final Node[] houseNodes;
    protected final Cable[] internalCables;
    protected final Cable[] houseCables;
    protected final House[] houses;
    // FWBWS
    private final Map<Node, Double> lastVoltageByNode;
    
    // CONSTRUCTOR
    
    public SimulationBase() {
        super(null, "Network", QuantityType.POWER);
        this.setSimulation(this);   // LoggingEntityBase needs reference to Simulation for time
        
        // keep an array of nodes for later reference
        this.lastVoltageByNode = new HashMap<>();

        // de tree maken
        this.transformer = new Transformer(this);

        this.internalNodes = new Node[NUMBER_OF_HOUSES];
        this.internalCables = new Cable[NUMBER_OF_HOUSES];
        this.houseNodes = new Node[NUMBER_OF_HOUSES];
        this.houseCables = new Cable[NUMBER_OF_HOUSES];
        this.houses = new House[NUMBER_OF_HOUSES];

        // maak huizen aan met cables en dat soort grappen
        for (int i = 0; i <= NUMBER_OF_HOUSES - 1; i++) {
            this.houses[i] = new House(this);

            if (SimulationConfig.SIMULATION_UNCONTROLABLE_LOAD_ENABLED) {
                houses[i].getDevices().add(new UncontrollableLoad(i, this));
            }

            this.houseNodes[i] = new Node(houses[i], this);
            this.internalNodes[i] = new Node(null, this);
            this.houseCables[i] = new Cable(houseNodes[i], 110, 5, this);
            this.internalNodes[i].getCables().add(houseCables[i]);

            this.internalCables[i] = new Cable(internalNodes[i], 110 + (NUMBER_OF_HOUSES - i) * 60, 20, simulation);
            if (i == 0) {
                transformer.getCables().add(internalCables[i]);
            } else {
                internalNodes[i - 1].getCables().add(internalCables[i]);
            }

            lastVoltageByNode.put(internalNodes[i], 230d);
            lastVoltageByNode.put(houseNodes[i], 230d);
        }
    }
    
    // PROPERTIES
    
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
     * @return the current radiance
     */
    public double getRadiance() {
        return WeatherData.getInstance().getRadianceProfile()[toTimeStep(getCurrentTime())];
    }
    
    /**
     * 
     * @return the current temperature
     */
    public double getTemperature() {
        return WeatherData.getInstance().getTemperatureProfile()[toTimeStep(getCurrentTime())];
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
    
    /**
     * Get the Devices in the network
     * 
     * @return ArrayList of Devices in the simulation 
     */
    public ArrayList<DeviceBase> getDevices() {
        ArrayList<DeviceBase> result = new ArrayList<DeviceBase>();
        for (House house : houses) {
            result.addAll(house.getDevices());
        }
        
        return result;
    }
    
    
    /**
     * Get a device by its ID
     * 
     * @param id the ID of a device
     * @return The device with the given ID or null when it does not exist
     */
    public DeviceBase getDeviceByID(int id) {
        DeviceBase result = null;
        ArrayList<DeviceBase> devices = getDevices();
        boolean found = false;
        for(int i = 0; i < devices.size() && !found; i++) {
            DeviceBase device = devices.get(i);
            if(device.getId() == id) {
                result = device;
                found = true;
            }
        }
        
        return result;
    }
    
    // METHODS
    
    /**
     * Called at the start of each tick
     */
    protected final void tick() {
        // Run anything that involves the UI on the JavaFX thread
        runOnJavaFXThreadSynchronously(() -> {
            getTransformer().tick(true);
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
            incrementTime();
        });
        
        if (getController() != null) {
            System.out.println("as");
            getController().retrievePlanning(50, getCurrentTime());
        }
    }
    
    /**
     * Increments the time.
     */
    protected abstract void incrementTime();
    
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
    
    
}

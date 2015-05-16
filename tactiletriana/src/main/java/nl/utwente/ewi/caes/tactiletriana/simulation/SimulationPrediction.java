/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.collections.ListChangeListener;
import javafx.scene.chart.XYChart.Data;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario.TimeSpan;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.*;

/**
 *
 * @author mickvdv
 */
public class SimulationPrediction extends Simulation {
    // Amount of hours that the prediction runs ahead
    private final static int RUN_AHEAD = 6;
    // The prediction's tick method shouldn't be affected by a scenario, so use one that runs infinitely
    private final static TimeScenario PREDICTION_SCENARIO = new TimeScenario();
    static {
        PREDICTION_SCENARIO.add(new TimeSpan(LocalDateTime.MIN, LocalDateTime.MAX));
    }
    
    private final Simulation mainSimulation;
    private final Map<LoggingEntityBase, LoggingEntityBase> futureByActual = new HashMap<>();
    
    private boolean mainSimulationChanged = false;
    private boolean timeSpanChanged = false;

    /**
     * Creates a new SimulationPrediction.
     * 
     * @param mainSimulation The real Simulation that this object will predict
     */
    public SimulationPrediction(Simulation mainSimulation) {
        super();
        setTimeScenario(PREDICTION_SCENARIO);
        
        this.mainSimulation = mainSimulation;
        setCurrentTime(mainSimulation.getCurrentTime());

        // Link this (future) simulation to acual simulation
        futureByActual.put(mainSimulation, this);
        linkNetwork(mainSimulation.getTransformer(), this.getTransformer());
        
        final Consumer<TimeSpan> timeSpanCallback = (TimeSpan t) -> {
            timeSpanChanged = true;
            setCurrentTime(t.getStart());
        };
        
        mainSimulation.getTimeScenario().addNewTimeSpanStartedCallback(timeSpanCallback);
        mainSimulation.timeScenarioProperty().addListener((observable, oldValue, newValue) -> {
            oldValue.removeNewTimeSpanStartedCallback(timeSpanCallback);
            newValue.addNewTimeSpanStartedCallback(timeSpanCallback);
        });
        
        // Zorg dat de simulatie 12 uur vooruit loopt
        this.mainSimulation.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            // Main Simulation jumped to new timespan, set time to start of new timespan
            if (timeSpanChanged) {
                timeSpanChanged = false;
                mainSimulationChanged = false;
                
                setCurrentTime(newValue);
                
                // Clear the log
                for (LoggingEntityBase logger : futureByActual.keySet()) {
                    logger.getLog().clear();
                    // Reset state of charges of all buffers
                    if (logger instanceof BufferBase) {
                        ((BufferBase)logger).setStateOfCharge(((BufferBase)getActual(logger)).getStateOfCharge());
                    }
                }
            }
            // Er is iets veranderd. Run de simulation vanaf het huidige punt vooruit
            else if (mainSimulationChanged) {
                mainSimulationChanged = false;
                setCurrentTime(oldValue);
                
                // Clear the invalid log values
                int minuteOfYear = toMinuteOfYear(oldValue);
                for (LoggingEntityBase logger : futureByActual.values()) {
                    List<Data<Integer, Double>> toRemove = new ArrayList<>();
                    for (Data<Integer, Double> data : logger.getLog()) {
                        if (data.getXValue() >= minuteOfYear) {
                            toRemove.add(data);
                        }
                    }
                    for (Data<Integer, Double> data : toRemove) {
                        logger.getLog().remove(data);
                    }
                    
                    // Reset state of charges of all buffers
                    if (logger instanceof BufferBase) {
                        ((BufferBase)logger).setStateOfCharge(((BufferBase)getActual(logger)).getStateOfCharge());
                    }
                }
            }

            // Zo lang hij achterloopt -> doe een tick()
            while (getCurrentTime().isBefore(newValue.plusHours(RUN_AHEAD))) {
                super.tick();
            }
        });
    }
    
    // HELPER METHODS
    
    // Walks through the network tree and synchronizes equivalent LoggingEntityBases
    private void linkNetwork(Node actual, Node future) {
        futureByActual.put(actual, future);
        if (actual.getHouse() != null) {
            linkHouse(actual.getHouse(), future.getHouse());
        }
        for (int i = 0; i < actual.getCables().size(); i++) {
            Cable actualCable = actual.getCables().get(i);
            Cable futureCable = future.getCables().get(i);
            futureCable.broken.bind(actualCable.broken);
            // Bind length
            actualCable.lengthProperty().addListener(obs -> { 
                futureCable.setLength(actualCable.getLength());
                mainSimulationChanged = true;
            });
            futureByActual.put(actualCable, futureCable);
            linkNetwork(actualCable.getChildNode(), futureCable.getChildNode());
        }
    }
    
    // Synchronizes two houses by synchronizing its device list
    private void linkHouse(House actualHouse, House futureHouse) {
        futureByActual.put(actualHouse, futureHouse);
        //futureDevice.getProperties().get(property).bind(actualDevice.getProperties().get(property));
        
        // bind the fuse property from actualHouse to futureHouse
        futureHouse.fuseBlown.bind(actualHouse.fuseBlown);
        
        actualHouse.getDevices().addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            while (c.next()) {
                for (DeviceBase item : c.getRemoved()) {
                    mainSimulationChanged = true;
                    futureHouse.getDevices().remove((DeviceBase)futureByActual.get(item));
                }
                for (DeviceBase actualDevice : c.getAddedSubList()) {
                    mainSimulationChanged = true;
                    
                    // maak een kopie van dit device in de map
                    DeviceBase futureDevice = (DeviceBase) futureByActual.get(actualDevice);
                    if (futureDevice == null) {
                        if (actualDevice instanceof Buffer) {
                            futureDevice = new Buffer(this);
                        } else if (actualDevice instanceof ElectricVehicle) {
                            futureDevice = new ElectricVehicle(this, ((ElectricVehicle)actualDevice).getModel());
                        } else if (actualDevice instanceof DishWasher) {
                            futureDevice = new DishWasher(this);
                        } else if (actualDevice instanceof SolarPanel) {
                            futureDevice = new SolarPanel(this);
                        } else if (actualDevice instanceof WashingMachine) {
                            futureDevice = new WashingMachine(this);
                        } else {
                            throw new UnsupportedOperationException("Copying instances of type " + 
                                    actualDevice.getClass().getName() + " not supported.");
                        }
                    }
                    
                    // sla het nieuwe device op in de map
                    futureByActual.put(actualDevice, futureDevice);
                    
                    // bind alle parameters
                    for (String property : actualDevice.getProperties().keySet()) {
                        // SOC and profile shouldn't be bound to
                        if (property.equals("SOC") || property.equals("profile")) {
                            continue;
                        }
                        futureDevice.getProperties().get(property).bind(actualDevice.getProperties().get(property));
                        
                        // als er iets aan de parameters veranderd moet de simulation.setMainSimulationChanged() aangeroepen worden
                        // dit zorgt ervoor dat bij de eerst volgende tick() van de main simulation de prediction opnieuw begint
                        actualDevice.getProperties().get(property).addListener(observable -> {
                            mainSimulationChanged = true;
                        });
                    }
                    
                    // voeg het toe aan dit huis
                    futureHouse.getDevices().add(futureDevice);
                }
            }
        });
    }
    
    // PUBLIC METHODS
    
    /**
     * Returns a LoggingEntityBase that represents the future version of the
     * specified LoggingEntityBase.
     * 
     * @param actual the real LoggingEntityBase
     * @return the future representation
     */
    public final LoggingEntityBase getFuture(LoggingEntityBase actual) {
        return futureByActual.get(actual);
    }
    
    /**
     * Returns the LoggingEntityBase that the specified LoggingEntityBase is the
     * the future version of.
     * 
     * @param future the future representation of a LoggingEntityBase
     * @return the real LoggingEntityBase
     */
    public final LoggingEntityBase getActual(LoggingEntityBase future) {
        for (LoggingEntityBase actual : futureByActual.keySet()) {
            if (futureByActual.get(actual).equals(future)) {
                return actual;
            }
        }
        return null;
    }
}

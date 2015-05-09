/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.prediction;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javafx.collections.ListChangeListener;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.*;

/**
 *
 * @author mickvdv
 */
public class SimulationPrediction extends Simulation {
    private final static int RUN_AHEAD = 6; // aantal uren dat de prediction voorloopt
    
    private final Simulation mainSimulation;
    private boolean mainSimulationChanged = false;
    public Map<LoggingEntityBase, LoggingEntityBase> futureByActual = new HashMap<>();

    public SimulationPrediction(Simulation mainSimulation) {
        this.mainSimulation = mainSimulation;
        setCurrentTime(mainSimulation.getCurrentTime());

        // Link this (future) simulation to acual simulation
        futureByActual.put(mainSimulation, this);
        linkNetwork(mainSimulation.getTransformer(), this.getTransformer());

        // Zorg dat de simulatie 12 uur vooruit loopt
        this.mainSimulation.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            // Er is iets veranderd. Run de simulation vanaf het huidige punt vooruit
            if (mainSimulationChanged) {
                mainSimulationChanged = false;
                setCurrentTime(oldValue);
                
                // Clear the invalid log values
                for (LoggingEntityBase logger : futureByActual.values()) {
                    Set<LocalDateTime> toRemove = new TreeSet<>();
                    for (LocalDateTime time : logger.getLog().keySet()) {
                        if (!time.isBefore(oldValue)) {
                            toRemove.add(time);
                        }
                    }
                    for (LocalDateTime time : toRemove) {
                        logger.getLog().remove(time);
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
    
    private void linkNetwork(Node actual, Node future) {
        futureByActual.put(actual, future);
        if (actual.getHouse() != null) {
            linkHouse(actual.getHouse(), future.getHouse());
        }
        for (int i = 0; i < actual.getCables().size(); i++) {
            Cable actualCable = actual.getCables().get(i);
            Cable futureCable = future.getCables().get(i);
            // Bind length
            actualCable.lengthProperty().addListener(obs -> { 
                futureCable.setLength(actualCable.getLength());
                mainSimulationChanged = true;
            });
            futureByActual.put(actualCable, futureCable);
            linkNetwork(actualCable.getChildNode(), futureCable.getChildNode());
        }
    }
    
    private void linkHouse(House actual, House future) {
        futureByActual.put(actual, future);
        actual.getDevices().addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            while (c.next()) {
                for (DeviceBase item : c.getRemoved()) {
                    mainSimulationChanged = true;
                    future.getDevices().remove((DeviceBase)futureByActual.get(item));
                }
                for (DeviceBase actualDevice : c.getAddedSubList()) {
                    mainSimulationChanged = true;
                    
                    // maak een kopie van dit device in de map
                    DeviceBase futureDevice = (DeviceBase) futureByActual.get(actualDevice);
                    if (futureDevice == null) {
                        if (actualDevice instanceof Buffer) {
                            futureDevice = new Buffer(this);
                        } else if (actualDevice instanceof BufferTimeShiftable) {
                            futureDevice = new BufferTimeShiftable(this, ((BufferTimeShiftable)actualDevice).getModel());
                        } else if (actualDevice instanceof DishWasher) {
                            futureDevice = new DishWasher(this);
                        } else if (actualDevice instanceof MockDevice) {
                            futureDevice = new MockDevice(this);
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
                    for (int i = 0; i < actualDevice.getParameters().size(); i++) {
                        futureDevice.getParameters().get(i).getProperty().bind(actualDevice.getParameters().get(i).getProperty());
                        
                        // als er iets aan de parameters veranderd moet de simulation.setMainSimulationChanged() aangeroepen worden
                        // dit zorgt ervoor dat bij de eerst volgende tick() van de main simulation de prediction opnieuw begint
                        actualDevice.getParameters().get(i).getProperty().addListener(observable -> {
                            mainSimulationChanged = true;
                        });
                    }
                    
                    // voeg het toe aan dit huis
                    future.getDevices().add(futureDevice);
                }
            }
        });
    }
    
    public final LoggingEntityBase getFuture(LoggingEntityBase actual) {
        return futureByActual.get(actual);
    }
    
    public final LoggingEntityBase getActual(LoggingEntityBase future) {
        for (LoggingEntityBase actual : futureByActual.keySet()) {
            if (futureByActual.get(actual).equals(future)) {
                return actual;
            }
        }
        return null;
    }
}

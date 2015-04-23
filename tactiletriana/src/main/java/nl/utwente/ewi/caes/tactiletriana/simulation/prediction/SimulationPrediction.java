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
                    for (Map<LocalDateTime, Double> log : logger.getLogsByEntityType().values()) {
                        for (LocalDateTime time : log.keySet()) {
                            if (!time.isBefore(oldValue)) {
                                toRemove.add(time);
                            }
                        }
                        for (LocalDateTime time : toRemove) {
                            log.remove(time);
                        }
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
                    try {
                        if (futureDevice == null)
                            futureDevice = (DeviceBase) actualDevice.getClass().getConstructors()[0].newInstance(simulation);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    
                    // sla het nieuwe device op in de map
                    futureByActual.put(actualDevice, futureDevice);
                    
                    // bind alle parameters
                    for (int i = 0; i < actualDevice.getParameters().size(); i++) {
                        futureDevice.getParameters().get(i).property.bind(actualDevice.getParameters().get(i).property);
                        
                        // als er iets aan de parameters veranderd moet de simulation.setMainSimulationChanged() aangeroepen worden
                        // dit zorgt ervoor dat bij de eerst volgende tick() van de main simulation de prediction opnieuw begint
                        actualDevice.getParameters().get(i).property.addListener(observable -> {
                            mainSimulationChanged = true;
                        });
                    }
                    
                    // voeg het toe aan dit huis
                    future.getDevices().add(futureDevice);
                }
            }
        });
    }
    
    public LoggingEntityBase getFuture(LoggingEntityBase actual) {
        return futureByActual.get(actual);
    }
}

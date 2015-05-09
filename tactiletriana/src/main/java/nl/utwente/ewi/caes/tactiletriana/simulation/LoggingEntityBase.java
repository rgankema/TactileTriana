/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Superclass of any class that needs to log a certain value at a certain time.
 * Needs an instance of Simulation to attain the current time.
 */
public abstract class LoggingEntityBase {
    private final String displayName;
    private final QuantityType qType;
    private final ObservableMap<LocalDateTime, Double> log;
    
    protected Simulation simulation;

    public LoggingEntityBase(Simulation simulation, String displayName, QuantityType qType) {
        this.displayName = displayName;
        this.qType = qType;
        this.simulation = simulation;
        this.log = FXCollections.observableMap(new TreeMap<>());
    }

    protected void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    // PROPERTIES
    
    public final String getDisplayName() {
        return this.displayName;
    }

    public final QuantityType getQuantityType() {
        return this.qType;
    }

    public final Simulation getSimulation() {
        return this.simulation;
    }
    
    public final ObservableMap<LocalDateTime, Double> getLog() {
        return this.log;
    }

    // METHODS
    
    protected final void log(double value) {
        // Log can be called when Simulation is still initializing, and thus currentTime can be null
        if (this.simulation.getCurrentTime() != null) {
            log.put(this.simulation.getCurrentTime(), value);
        }
    }

    // ENUMS
    
    /**
     * Describes a physical quantity that may be logged.
     */
    public static enum QuantityType {
        CURRENT, POWER, VOLTAGE
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 *
 * @author mickvdv
 */
public abstract class LoggingEntityBase {
    private final QuantityType qType;
    private final Map<Class<? extends LoggingEntityBase>, ObservableMap<LocalDateTime, Double>> logsByEntityType;
    private double absoluteMaximum = Double.POSITIVE_INFINITY;
    protected Simulation simulation;

    public LoggingEntityBase(QuantityType qType, Simulation simulation) {
        this.qType = qType;
        this.simulation = simulation;
        this.logsByEntityType = new HashMap<>();
    }

    protected void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    // PROPERTIES
    
    public abstract String getDisplayName();

    public final QuantityType getQuantityType() {
        return this.qType;
    }

    public final double getAbsoluteMaximum() {
        return this.absoluteMaximum;
    }

    protected final void setAbsoluteMaximum(double maximum) {
        absoluteMaximum = Math.abs(maximum);
    }

    public final Simulation getSimulation() {
        return this.simulation;
    }
    
    public final Map<Class<? extends LoggingEntityBase>, ObservableMap<LocalDateTime, Double>> getLogsByEntityType() {
        return Collections.unmodifiableMap(this.logsByEntityType);
    }

    // METHODS
    
    protected final void log(Class<? extends LoggingEntityBase> type, double value) {
        // Log can be called when Simulation is still initializing, and thus currentTime can be null
        if (this.simulation.getCurrentTime() != null) {
            if (logsByEntityType.get(type) == null) {
                logsByEntityType.put(type, FXCollections.observableMap(new TreeMap<>()));
            }
            logsByEntityType.get(type).put(this.simulation.getCurrentTime(), value);
        }
    }
    
    public final void clearLogs() {
        for (Map<LocalDateTime, Double> log : logsByEntityType.values()) {
            log.clear();
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

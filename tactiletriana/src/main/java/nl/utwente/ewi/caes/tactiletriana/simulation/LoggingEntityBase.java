/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
    private final EntityType eType;
    private final ObservableMap<LocalDateTime, Double> log;
    private final Map<EntityType, ObservableMap<LocalDateTime, Double>> childLogs;
    private double absoluteMaximum = Double.POSITIVE_INFINITY;
    protected Simulation simulation;

    public LoggingEntityBase(QuantityType qType, Simulation simulation, 
            EntityType eType, EntityType... childTypes) {
        this.qType = qType;
        this.eType = eType;
        this.simulation = simulation;
        this.log = FXCollections.observableMap(new TreeMap<>());
        this.childLogs = new HashMap<>();
        for (EntityType childType : childTypes) {
            childLogs.put(childType, FXCollections.observableMap(new TreeMap<>()));
        }
    }

    protected void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    // PROPERTIES
    
    public final String getDisplayName() {
        String name = null;
        switch (eType) {
            case BUFFER_TIME_SHIFTABLE:
                name = "Buffer Time Shiftable";
                break;
            case CABLE:
                name = "Cable";
                break;
            case HOUSE:
                name = "House";
                break;
            case MOCK_DEVICE:
                name = "Mock Device";
                break;
            case NETWORK:
                name = "Network";
                break;
            case NODE:
                name = "Node";
                break;
            case SOLAR_PANEL:
                name = "Solar Panel";
                break;
            case UNCONTROLLABLE:
                name = "Uncontrollable Load";
                break;
        }
        return name;
    }

    public final QuantityType getQuantityType() {
        return this.qType;
    }
    
    public final EntityType getEntityType() {
        return this.eType;
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
    
    public final ObservableMap<LocalDateTime, Double> getLog() {
        return this.log;
    }
    
    public final Map<EntityType, ObservableMap<LocalDateTime, Double>> getChildLogs() {
        return Collections.unmodifiableMap(this.childLogs);
    }

    // METHODS
    
    protected final void log(double value) {
        // Log can be called when Simulation is still initializing, and thus currentTime can be null
        if (this.simulation.getCurrentTime() != null) {
            log.put(this.simulation.getCurrentTime(), value);
        }
    }
    
    protected final void log(EntityType childType, double value) {
        // Log can be called when Simulation is still initializing, and thus currentTime can be null
        if (this.simulation.getCurrentTime() != null) {
            childLogs.get(childType).put(this.simulation.getCurrentTime(), value);
        }
    }

    // ENUMS
    
    /**
     * Describes a physical quantity that may be logged.
     */
    public static enum QuantityType {
        CURRENT, POWER, VOLTAGE
    }
    
    /**
     * Describes types of entities.
     */
    public static enum EntityType {
        NETWORK, HOUSE, NODE, CABLE, MOCK_DEVICE,
        BUFFER_TIME_SHIFTABLE, SOLAR_PANEL, UNCONTROLLABLE
    }
}

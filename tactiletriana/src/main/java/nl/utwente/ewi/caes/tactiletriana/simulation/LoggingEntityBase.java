/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.SolarPanel;
import nl.utwente.ewi.caes.tactiletriana.util.Util;

/**
 *
 * @author mickvdv
 */
public abstract class LoggingEntityBase {
    private final QuantityType qType;
    private final Map<String, ObservableList<Data<Number, Number>>> logsByName;
    private final String defaultLog;
    private double absoluteMaximum = Double.POSITIVE_INFINITY;
    protected Simulation simulation;

    public LoggingEntityBase(QuantityType qType, Simulation simulation, String... logNames) {
        this.qType = qType;
        this.simulation = simulation;
        this.logsByName = new HashMap<>();
        defaultLog = logNames[0];
        for (String logName : logNames) {
            
            logsByName.put(logName, FXCollections.observableArrayList());
        }
    }

    protected void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    // PROPERTIES
    
    public abstract String getDisplayName();

    public String getDefault() {
        return this.defaultLog;
    }
    
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
    
    public final Map<String, ObservableList<Data<Number, Number>>> getLogsByName() {
        return this.logsByName;
    }

    // METHODS
    
    protected final void log(String logName, double value) {
        // Log can be called when Simulation is still initializing, and thus currentTime might be null
        if (this.simulation.getCurrentTime() != null) {
            LocalDateTime time = simulation.getCurrentTime();
            List<Data<Number, Number>> log = logsByName.get(logName);
            
            long minutes = Util.toEpochMinutes(time);
            if (log.isEmpty()) {
        //        log.add(new Data<>(minutes, 0));
            } else {
        //        log.add(new Data<>(minutes, log.get(log.size() - 1).getYValue()));
            }
            log.add(new Data<>(minutes, value));
        }
    }
    
    public final void clearLogs() {
        for (List log : logsByName.values()) {
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

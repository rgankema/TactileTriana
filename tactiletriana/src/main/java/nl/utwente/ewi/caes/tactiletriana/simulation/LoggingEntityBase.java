/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Data;

/**
 * Superclass of any class that needs to log a certain value at a certain time.
 * Needs an instance of Simulation to attain the current time.
 */
public abstract class LoggingEntityBase {
    private final String displayName;
    private final QuantityType qType;
    private final ObservableList<Data<Integer, Double>> log;
    
    protected SimulationBase simulation;

    public LoggingEntityBase(SimulationBase simulation, String displayName, QuantityType qType) {
        this.displayName = displayName;
        this.qType = qType;
        this.simulation = simulation;
        this.log = FXCollections.observableArrayList();
    }

    protected void setSimulation(SimulationBase simulation) {
        this.simulation = simulation;
    }

    // PROPERTIES
    
    public final String getDisplayName() {
        return this.displayName;
    }

    public final QuantityType getQuantityType() {
        return this.qType;
    }

    public final SimulationBase getSimulation() {
        return this.simulation;
    }
    
    public final ObservableList<Data<Integer, Double>> getLog() {
        return this.log;
    }

    // METHODS
    
    protected final void log(double value) {
        LocalDateTime time = this.simulation.getCurrentTime();
        // Log can be called when Simulation is still initializing, and thus currentTime can be null
        if (time != null) {
            if (log.size() > 0) {
                log.add(new Data<>(log.get(log.size() - 1).getXValue(), value));
            }
            log.add(new Data<>(toMinuteOfYear(time), value));
        }
    }
    
    protected final int toMinuteOfYear(LocalDateTime time) {
        return (time.getDayOfYear() - 1) * 24 * 60 + time.getHour() * 60 + time.getMinute();
    }

    // ENUMS
    
    /**
     * Describes a physical quantity that may be logged.
     */
    public static enum QuantityType {
        CURRENT, POWER, VOLTAGE
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 *
 * @author mickvdv
 */
public abstract class LoggingEntity {

    private final String displayName;
    private final LoggedValueType type;
    private final ObservableMap<LocalDateTime, Double> log;
    private double absoluteMaximum = Double.POSITIVE_INFINITY;
    protected Simulation simulation;

    public LoggingEntity(LoggedValueType type, String displayName, Simulation simulation) {
        this.displayName = displayName;
        this.type = type;
        this.simulation = simulation;
        this.log = FXCollections.observableMap(new HashMap<>());
    }

    protected void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    // PROPERTIES
    public final String getDisplayName() {
        return this.displayName;
    }

    public final LoggedValueType getLoggedValueType() {
        return this.type;
    }

    public final double getAbsoluteMaximum() {
        return this.absoluteMaximum;
    }

    protected final void setAbsoluteMaximum(double maximum) {
        absoluteMaximum = Math.abs(maximum);
    }

    public final ObservableMap<LocalDateTime, Double> getLog() {
        return this.log;
    }

    // METHODS
    protected final void log(double value) {
        log.put(this.simulation.getCurrentTime(), value);
    }

    // ENUMS
    public static enum LoggedValueType {

        CURRENT, POWER, VOLTAGE
    }
}

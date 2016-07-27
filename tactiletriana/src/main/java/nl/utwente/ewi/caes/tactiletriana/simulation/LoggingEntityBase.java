/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.chart.XYChart.Data;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import static nl.utwente.ewi.caes.tactiletriana.Util.toMinuteOfYear;

/**
 * Superclass of any class that needs to log a certain value at a certain time.
 * Needs an instance of Simulation to attain the current time.
 */
public abstract class LoggingEntityBase {

    private final String displayName;
    private final UnitOfMeasurement unitOfMeasurement;
    private final List<Data<Integer, Float>> log;

    /**
     * Flag indicating if any new values have been logged.
     */
    public boolean dirty = false;
    
    /**
     * 
     * @param displayName name of the entity as presented to the user
     * @param unitOfMeasurement the measurement unit of this entity (either Ampere / Volt or Watt)
     */
    public LoggingEntityBase(String displayName, UnitOfMeasurement unitOfMeasurement) {
        this.displayName = displayName;
        this.unitOfMeasurement = unitOfMeasurement;
        this.log = new ArrayList<>();
    }

    // PROPERTIES
    /**
     * 
     * @return the display name of this entity
     */
    public final String getDisplayName() {
        return this.displayName;
    }

    /**
     * 
     * @return the measurement unit of this entity
     */
    public final UnitOfMeasurement getUnitOfMeasurement() {
        return this.unitOfMeasurement;
    }

    /**
     * 
     * @return the logged values of this entity
     */
    public final List<Data<Integer, Float>> getLog() {
        return this.log;
    }

    // METHODS
    /**
     * Logs a value at a given time for this entity
     * @param time
     * @param value 
     */
    protected final void log(LocalDateTime time, double value) {
        synchronized (this) {
            if (log.size() > 0) {
                log.add(new Data<>(log.get(log.size() - 1).getXValue(), (float) value));
            }
            log.add(new Data<>(toMinuteOfYear(time), (float) value));
            dirty = true;

            // Discard values that won't be shown anymore.
            if (log.size() > 12 * 60 / GlobalSettings.TICK_MINUTES + 2) {
                log.remove(0);
                log.remove(0);
                dirty = true;
            }
        }
    }

    // ENUMS
    /**
     * Describes a physical quantity that may be logged.
     */
    public static enum UnitOfMeasurement {

        CURRENT, POWER, VOLTAGE
    }
}

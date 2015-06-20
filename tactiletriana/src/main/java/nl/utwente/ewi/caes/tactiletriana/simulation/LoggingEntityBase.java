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

/**
 * Superclass of any class that needs to log a certain value at a certain time.
 * Needs an instance of Simulation to attain the current time.
 */
public abstract class LoggingEntityBase {

    private final String displayName;
    private final QuantityType qType;
    private final List<Data<Integer, Float>> log;

    public boolean invalid = false;

    public LoggingEntityBase(String displayName, QuantityType qType) {
        this.displayName = displayName;
        this.qType = qType;
        this.log = new ArrayList<>();
    }

    // PROPERTIES
    public final String getDisplayName() {
        return this.displayName;
    }

    public final QuantityType getQuantityType() {
        return this.qType;
    }

    public final List<Data<Integer, Float>> getLog() {
        return this.log;
    }

    // METHODS
    protected final void log(LocalDateTime time, double value) {
        synchronized (this) {
            if (log.size() > 0) {
                log.add(new Data<>(log.get(log.size() - 1).getXValue(), (float) value));
            }
            log.add(new Data<>(toMinuteOfYear(time), (float) value));
            invalid = true;

            // Discard values that won't be shown anymore.
            if (log.size() > 12 * 60 / GlobalSettings.TICK_MINUTES + 2) {
                log.remove(0);
                log.remove(0);
                invalid = true;
            }
        }
    }

    public final static int toMinuteOfYear(LocalDateTime time) {
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

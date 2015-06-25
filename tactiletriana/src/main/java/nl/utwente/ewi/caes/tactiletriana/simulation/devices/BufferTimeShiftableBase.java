/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author niels
 */
public abstract class BufferTimeShiftableBase extends BufferBase {

    public static final String API_TIMES = "times";
    public static final String API_VEHICLE2GRID = "vehicle2grid";
    public static final String API_DESIREDCHARGE = "desired_charge";

    /**
     * Constructs a BufferTimeShiftable device.
     *
     * @param simulation The simulation object of the current simulation.
     * @param displayName The name of the device as shown to the user.
     */
    public BufferTimeShiftableBase(SimulationBase simulation, String displayName) {
        super(simulation, displayName, "BufferTimeShiftable");

        // register properties for API
        registerAPIParameter(API_TIMES);
        registerAPIParameter(API_VEHICLE2GRID);
        registerAPIParameter(API_DESIREDCHARGE);

        // register properties for prediction
        registerProperty(startTime);
        registerProperty(endTime);
        registerProperty(vehicle2Grid);
        registerProperty(desiredCharge);
    }

    // PROPERTIES
    /**
     * Whether the BufferTimeShiftable can provide energy back to the grid.
     */
    private final BooleanProperty vehicle2Grid = new SimpleBooleanProperty(false) {
        @Override
        public void set(boolean value) {
            super.set(value);
            setDirty(true);
        }
    };

    public BooleanProperty vehicle2GridProperty() {
        return vehicle2Grid;
    }

    public final void setVehicle2Grid(boolean vehicle2Grid) {
        vehicle2GridProperty().set(vehicle2Grid);
    }

    public final boolean isVehicle2Grid() {
        return vehicle2GridProperty().get();
    }

    /**
     * The time (in minutes from the start of the day) from which point the
     * device may start operating
     */
    private final DoubleProperty startTime = new SimpleDoubleProperty() {
        @Override
        public void set(double value) {
            super.set(value);
            setDirty(true);
        }
    };

    public DoubleProperty startTimeProperty() {
        return startTime;
    }

    public double getStartTime() {
        return startTimeProperty().get();
    }

    public void setStartTime(double start) {
        startTimeProperty().set(start);
    }

    /**
     * Last moment in minutes that the device may start its program
     */
    protected final DoubleProperty endTime = new SimpleDoubleProperty() {
        @Override
        public void set(double value) {
            super.set(value);
            setDirty(true);
        }
    };

    public DoubleProperty endTimeProperty() {
        return endTime;
    }

    public final double getEndTime() {
        return endTime.get();
    }

    public final void setEndTime(double endTime) {
        this.endTime.set(endTime);
    }

    /**
     *
     */
    protected final DoubleProperty desiredCharge = new SimpleDoubleProperty() {
        @Override
        public void set(double value) {
            if (get() == value) {
                return;
            }
            if (value < 0) {
                value = 0;
            }
            super.set(value);
            setDirty(true);
        }
    };

    public DoubleProperty desiredChargeProperty() {
        return desiredCharge;
    }

    public final double getDesiredCharge() {
        return desiredCharge.get();
    }

    public final void setDesiredCharge(double desiredCharge) {
        this.desiredCharge.set(desiredCharge);
    }

    // METHODS
    @Override
    protected JSONObject parametersToJSON() {
        JSONObject result = super.parametersToJSON();

        // Build times
        JSONObject interval = new JSONObject();
        interval.put("start_time", getStartTime());
        interval.put("end_time", getEndTime());
        JSONArray times = new JSONArray();
        times.add(interval);

        result.put(API_TIMES, times);
        result.put(API_VEHICLE2GRID, isVehicle2Grid());
        result.put(API_DESIREDCHARGE, getDesiredCharge());
        return result;
    }

    @Override
    public void updateParameter(String parameter, Object value) {
        if (parameter.equals(API_TIMES)) {
            JSONObject times = (JSONObject) value;
            setStartTime((double) times.get("start_time"));
            setEndTime((double) times.get("end_time"));
        } else if (parameter.equals(API_VEHICLE2GRID)) {
            setVehicle2Grid((boolean) value);
        } else if (parameter.equals(API_DESIREDCHARGE)) {
            setDesiredCharge((double) value);
        } else {
            super.updateParameter(parameter, value);
        }
    }
}

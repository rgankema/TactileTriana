/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import org.json.simple.JSONObject;

/**
 * Base class for any device that has a buffer.
 * 
 * Has the following properties as specified in the API:
 * <ul>
 *  <li>capacity</li>
 *  <li>max_power</li>
 *  <li>SOC</li>
 * </ul>
 * 
 * @author Richard
 */
public abstract class BufferBase extends DeviceBase {
    public static final String API_CAPACITY = "capacity";
    public static final String API_MAX_POWER = "max_power";
    public static final String API_STATE_OF_CHARGE = "SOC";
    
    /**
     * Constructs a new BufferBase. Registers the {@code capacity}, 
     * {@code max_power}, and {@code SOC} properties as specified in the API.
     * 
     * @param simulation    the Simulation that this device belongs to
     * @param displayName   the name of this device as shown to the user
     * @param apiDeviceType the name of this device as specified in the API
     */
    public BufferBase(SimulationBase simulation, String displayName, String apiDeviceType) {
        super(simulation, displayName, apiDeviceType);
        
        // register properties for API
        registerAPIProperty(API_CAPACITY);
        registerAPIProperty(API_MAX_POWER);
        registerAPIProperty(API_STATE_OF_CHARGE);
        
        // register properties for prediction
        registerProperty(capacity);
        registerProperty(maxPower);
    }
    
    // PROPERTIES
    
    /**
     * Capacitiy of Buffer in Wh.
     */
    private final DoubleProperty capacity = new SimpleDoubleProperty(3700d) {
        @Override
        public void set(double value) {
            if (value < 0) {
                value = 0;
            }
            super.set(value);
        }
    };
    
    public DoubleProperty capacityProperty() {
        return capacity;
    }

    public double getCapacity() {
        return capacity.get();
    }

    public void setCapacity(double capacity) {
        this.capacity.set(capacity);
    }

    /**
     * The maximum power the Buffer can produce or consume
     */
    private final DoubleProperty maxPower = new SimpleDoubleProperty(1000d) {
        @Override
        public void set(double value) {
            if (get() == value) {
                return;
            }
            if (value < 0) {
                value = 0;
            }
            super.set(value);
        }
    };

    public DoubleProperty maxPowerProperty() {
        return maxPower;
    }
    
    public double getMaxPower() {
        return maxPower.get();
    }

    public void setMaxPower(double power) {
        this.maxPower.set(power);
    }

    /**
     * The state of charge in Wh. Ensures that it's never below 0 and never higher
     * than {@link capacity}.
     */
    private final DoubleProperty stateOfCharge = new SimpleDoubleProperty(0) {
        @Override
        public void set(double value) {
            if (value < 0) {
                value = 0;
            } else if (value > getCapacity()) {
                value = getCapacity();
            }
            super.set(value);
        }
    };

    public DoubleProperty stateOfChargeProperty() {
        return stateOfCharge;
    }

    public final double getStateOfCharge() {
        return stateOfChargeProperty().get();
    }

    public final void setStateOfCharge(double soc) {
        this.stateOfCharge.set(soc);
    }
    
    /**
     * @return whether the battery is charged or not
     */
    public final boolean isCharged(){
        return getStateOfCharge() == getCapacity();
    }
    
    // METHODS
    
    @Override
    protected JSONObject parametersToJSON() {
        JSONObject result = new JSONObject();
        result.put(API_CAPACITY, getCapacity());
        result.put(API_MAX_POWER, getMaxPower());
        result.put(API_STATE_OF_CHARGE, getStateOfCharge());
        return result;
    }
}

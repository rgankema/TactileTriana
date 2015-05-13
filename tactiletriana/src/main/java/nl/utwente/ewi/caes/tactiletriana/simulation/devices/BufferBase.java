/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

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
    
    /**
     * Constructs a new BufferBase. Registers the {@code capacity}, 
     * {@code max_power}, and {@code SOC} properties as specified in the API.
     * 
     * @param simulation    the Simulation that this device belongs to
     * @param displayName   the name of this device as shown to the user
     * @param apiDeviceType the name of this device as specified in the API
     */
    public BufferBase(Simulation simulation, String displayName, String apiDeviceType) {
        super(simulation, displayName, apiDeviceType);
        
        // register properties for API
        addProperty("capacity", capacity);
        addProperty("max_power", maxPower);
        addProperty("SOC", stateOfCharge);
    }
    
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
    private final ReadOnlyDoubleWrapper stateOfCharge = new ReadOnlyDoubleWrapper(0) {
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

    public ReadOnlyDoubleProperty stateOfChargeProperty() {
        return stateOfCharge.getReadOnlyProperty();
    }

    public final double getStateOfCharge() {
        return stateOfChargeProperty().get();
    }

    // Temporary solution for buffers not working correctly after SimPrediction reset
    // Shouldn't be public in the end
    public void setStateOfCharge(double soc) {
        this.stateOfCharge.set(soc);
    }
    
    
    /**
     * @return whether the battery is charged or not
     */
    public boolean isCharged(){
        return getStateOfCharge() == getCapacity();
    }
}

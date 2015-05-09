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
 *
 * @author Richard
 */
public abstract class BufferBase extends DeviceBase {
    
    public BufferBase(Simulation simulation, String displayName) {
        super(simulation, displayName);
    }
    
    /**
     * Capacitiy of Buffer in Wh.
     */
    private final DoubleProperty capacity = new SimpleDoubleProperty(1000d) {
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
    private final ReadOnlyDoubleWrapper stateOfCharge = new ReadOnlyDoubleWrapper(230.0) {
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
}

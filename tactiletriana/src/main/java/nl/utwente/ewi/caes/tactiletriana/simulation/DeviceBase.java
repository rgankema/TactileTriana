/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 *
 * @author Richard
 */

public abstract class DeviceBase {
    /**
     * Describes the state of a device
     */
    public enum State {
        /**
         * The device is not connected to a house
         */
        DISCONNECTED,
        /**
         * The device is connected to a house
         */
        CONNECTED,
        /**
         * The device is connected to a house, but can't draw power
         */
        CONNECTED_NO_POWER,
    }
    
    /**
     * The describes the parameters that can be set for this device
     */
    public static class Parameter {
        /**
         * The display name of the parameter
         */
        public final String displayName;
        /**
         * The property that the parameter binds to
         */
        public final DoubleProperty property;
        /**
         * The minimum value of the property
         */
        public final double minValue;
        /**
         * The maximum value of the property
         */
        public final double maxValue;
        
        public Parameter(String displayName, DoubleProperty property, double minValue, double maxValue) {
            this.displayName = displayName;
            this.property = property;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
    }
    
    /**
     * 
     * @return the amount of power that the device currently consumes
     */
    public abstract ReadOnlyDoubleProperty currentConsumptionProperty();
    
    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }
    
    /**
     * 
     * @return the state of this device
     */
    public final ReadOnlyObjectProperty<State> stateProperty() {
        throw new UnsupportedOperationException("Not supported yet");
    }
    
    public final State getState() {
        return stateProperty().get();
    }
    
    /**
     * 
     * @return the parameters of this device
     */
    public abstract Parameter[] getParameters();
    
    /**
     * Called by the simulation for every tick. The Device calculates its consumption
     * for that tick, and updates it.
     * @param time  the amount of time that passed since the last tick
     */
    public abstract void tick(double time);
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Richard
 */
public abstract class DeviceBase {
    private final List<Parameter> parameters;
    private final List<Parameter> parametersUnmodifiable;
    
    public DeviceBase() {
        parameters = new ArrayList<>();
        parametersUnmodifiable = Collections.unmodifiableList(parameters);
    }
    
    // PROPERTIES
    
    /**
     * The amount of power that the device currently consumes
     */
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(10.0){
        @Override
        public void set(double value) {
            // als hij disconnected is is hij altijd 0
            if (getState() != DeviceBase.State.CONNECTED) {
                value = 0;
            }
            super.set(value);
        }
    };
    
    public ReadOnlyDoubleProperty currentConsumptionProperty(){
        return currentConsumption.getReadOnlyProperty();
    }
    
    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }
    
    protected final void setCurrentConsumption(double value){
        currentConsumption.set(value);
    }
    
    /**
     * 
     * @return the state of this device
     */
    private final ObjectProperty<State> state = new SimpleObjectProperty<State>(DeviceBase.State.DISCONNECTED){
        @Override
        public void set(State value){
            if (value != DeviceBase.State.CONNECTED){
                setCurrentConsumption(0);
            }
            super.set(value);
        }
    };
    
    public ObjectProperty<State> stateProperty() {
        return this.state;
    }
    
    public final State getState() {
        return stateProperty().get();
    }
    
    protected final void setState(State s){
        this.stateProperty().set(s);
    }
    
    /**
     * Returns the parameters of this device. This list is unmodifiable, never null,
     * and its elements are never null.
     * @return the parameters of this device
     */
    public List<Parameter> getParameters() {
        return parametersUnmodifiable;
    }
    
    /**
     * Adds a parameter to the list of parameters
     * @param parameter 
     */
    protected final void addParameter(Parameter parameter) {
        if (parameter != null) {
            parameters.add(parameter);
        }
    }
    
    /**
     * Called by the simulation for every tick. The Device calculates its consumption
     * for that tick, and updates it.
     * @param time  the amount of time that passed since the last tick
     * @param connected device is connected to the main network. (False in case of a broken cable)
     */
    public void tick(double time, boolean connected){
        if(!connected){
            setState(DeviceBase.State.DISCONNECTED);
        } else {
            setState(DeviceBase.State.CONNECTED);
        }
    }
    
    // ENUMS AND NESTED CLASSES
    
    /**
     * Describes the state of a device
     */
    public enum State {
        /**
         * The device is not connected to a house
         */
        NOT_IN_HOUSE,
        /**
         * The device is connected to a house
         */
        CONNECTED,
        /**
         * The device is connected to a house, but can't draw power
         */
        DISCONNECTED,
    }
    
    /**
     * The describes the parameters that can be set for this device
     */
    public static final class Parameter {
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
}

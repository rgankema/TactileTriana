/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author Richard
 */
public abstract class DeviceBase extends LoggingEntityBase {

    private final List<Configurable> parameters;
    private final List<Configurable> parametersUnmodifiable;

    public DeviceBase(Simulation simulation, String displayName) {
        super(simulation, displayName, QuantityType.POWER);

        parameters = new ArrayList<>();
        parametersUnmodifiable = Collections.unmodifiableList(parameters);
    }

    // PROPERTIES
    /**
     * The amount of power that the device currently consumes
     */
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(10.0) {
        @Override
        public void set(double value) {
            // consumption is always zero if not connected to the grid
            if (getState() != DeviceBase.State.CONNECTED) {
                value = 0;
            }
            log(value);
            super.set(value);
        }
    };

    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption.getReadOnlyProperty();
    }

    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }

    protected final void setCurrentConsumption(double value) {
        currentConsumption.set(value);
    }
    
    /**
     * The house that hosts this device
     */
    private final ReadOnlyObjectWrapper<House> parentHouse = new ReadOnlyObjectWrapper<>();
    
    public ReadOnlyObjectProperty<House> parentHouseProperty() {
        return parentHouse.getReadOnlyProperty();
    }
    
    public House getParentHouse() {
        return parentHouse.get();
    }
    
    void setParentHouse(House house) {
        parentHouse.set(house);
    }

    /**
     *
     * @return the state of this device
     */
    private final ObjectProperty<State> state = new SimpleObjectProperty<State>(DeviceBase.State.NOT_IN_HOUSE) {
        @Override
        public void set(State value) {
            if (value != DeviceBase.State.CONNECTED) {
                // when not connected, no consumption
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

    protected final void setState(State s) {
        this.stateProperty().set(s);
    }

    /**
     * Returns the parameters of this device. This list is unmodifiable, never
     * null, and its elements are never null.
     *
     * @return the parameters of this device
     */
    public List<Configurable> getParameters() {
        return parametersUnmodifiable;
    }

    /**
     * Adds a parameter to the list of parameters
     *
     * @param parameter
     */
    protected final void addParameter(Configurable parameter) {
        if (parameter != null) {
            parameters.add(parameter);
        }
    }

    public void tick(double timePassed, boolean connected) {
        if (!connected) {
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
     * Describes a parameter that can be configured for a device by a user. Used
     * by {@link SimulationPrediction} to synchronise two devices.
     */
    public static abstract class Configurable {
        private final String displayName;
        
        public Configurable(String displayName) {
            this.displayName = displayName;
        }
        
        /**
         * @return the property holding the parameter's value
         */
        public abstract Property getProperty();
        
        /**
         * @return the name of the parameter as it should appear to the user
         */
        public final String getDisplayName() {
            return this.displayName;
        }
    }
    
    /**
     * Describes a parameter for a device that have a numeric value
     */
    public static final class ConfigurableDouble extends Configurable {
        
        private final DoubleProperty property;
        private final double minValue;
        private final double maxValue;

        public ConfigurableDouble(String displayName, DoubleProperty property, double minValue, double maxValue) {
            super(displayName);
            this.property = property;
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        @Override
        public final DoubleProperty getProperty() {
            return property;
        }
        
        /**
         * @return the minimum value of the property
         */
        public final double getMin() {
            return minValue;
        }
        
        /**
         * @return the maximum value of the property
         */
        public final double getMax() {
            return maxValue;
        }
    }
    
    /**
     * Describes a parameter for a device that can only be true or false
     */
    public static final class ConfigurableBoolean extends Configurable {

        private final BooleanProperty property;
        
        public ConfigurableBoolean(String displayName, BooleanProperty property) {
            super(displayName);
            this.property = property;
        }
        
        @Override
        public BooleanProperty getProperty() {
            return this.property;
        }
        
    }
    
    /**
     * Describes a parameter for a device that cannot be represented by
     * a numeric value or a boolean
     * @param <T> The type of the category
     */
    public static final class ConfigurableCategory<T> extends Configurable {
        
        private final ObjectProperty<T> property;
        private final Function<T, String> categoryToString;
        private final T[] possibleValues;
        
        public ConfigurableCategory(String displayName, ObjectProperty<T> property, Function<T, String> categoryToString, T... possibleValues) {
            super(displayName);
            this.property = property;
            this.categoryToString = categoryToString;
            this.possibleValues = possibleValues;
        }

        @Override
        public ObjectProperty<T> getProperty() {
            return this.property;
        }
        
        /**
         * @return the display name of the currently selected value for this
         * parameter
         */
        public String getCurrentValueDisplayName() {
            return categoryToString.apply(getProperty().get());
        }
        
        /**
         * @return the set of values that this parameter may have
         */
        public T[] getPossibleValues() {
            return possibleValues;
        }
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
import org.json.simple.JSONObject;

/**
 *
 * @author Richard
 */
public abstract class DeviceBase extends LoggingEntityBase {
    private static int DEVICE_ID = 0;
    
    //TODO configurables eruit gooien
    private final List<Configurable> parameters;
    private final List<Configurable> parametersUnmodifiable;

    private final int id;
    private final String apiDeviceType;
    
    /*
    Map containing a Device's parameters.
    The key of the Map is the name of the parameter as specified in the API.
    The values are Property objects representing some Property of the device.
    */
    private final HashMap<String, Property> properties;
    
    /**
     * Constructs a new DeviceBase
     * 
     * @param simulation    the simulation that this device is part of
     * @param displayName   the name of the device as it should be shown to the user
     * @param apiDeviceType    the name of the device as specified in the API
     */
    public DeviceBase(Simulation simulation, String displayName, String apiDeviceType) {
        super(simulation, displayName, QuantityType.POWER);
        
        id = DEVICE_ID;
        DEVICE_ID++;
        
        this.apiDeviceType = apiDeviceType;
        
        this.properties = new HashMap<>();
        //TODO remove this
        parameters = new ArrayList<>();
        parametersUnmodifiable = Collections.unmodifiableList(parameters);
    }

    // PROPERTIES
    
    /**
     * @return a unique identifier for this device
     */
    public int getId() {
        return this.id;
    }
    
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
    
    // METHODS

    public void tick(double timePassed, boolean connected) {
        if (!connected) {
            setState(DeviceBase.State.DISCONNECTED);
        } else {
            setState(DeviceBase.State.CONNECTED);
        }
    }
    
    protected void addProperty(String apiName, Property property) {
        this.properties.put(apiName, property);
    }
    
    protected void getProperty(String apiName) {
        this.properties.get(apiName);
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
     * Convert this Device and its parameters to a JSON representation as specified in the API. 
     * Subclasses of DeviceBase should override this method and add any information not yet known (e.g. deviceType). 
     * 
     * @return 
     */
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("deviceID", this.id);
        result.put("deviceType", this.apiDeviceType);
        result.put("consumption", this.getCurrentConsumption());
        //Get the parameters of this Device
        //TODO handle other Property Types
        JSONObject parameters = new JSONObject();
        for(String param : this.properties.keySet()) {
            Property p = properties.get(param);
            if(p instanceof DoubleProperty) {
                parameters.put(param, p.getValue());
            } else if (p instanceof BooleanProperty) {
                parameters.put(param, p.getValue());
            }
            
        }
        result.put("parameters", parameters);
        
        return result;
        
    }
    
    
    //TODO remove all things related to Configurables
    
    /**
     * Describes a parameter that can be configured for a device by a user. Used
     * by {@link SimulationPrediction} to synchronise two devices.
     */
    public static abstract class Configurable {
        private final String displayName;
        private final String apiName;
        
        public Configurable(String displayName, String apiName) {
            this.displayName = displayName;
            this.apiName = apiName;
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
        
        /**
         * 
         * @return the name of the parameter as specified in the API 
         */
        public final String getApiName() {
            return this.apiName;
        }
    }
    
    /**
     * Describes a parameter for a device that have a numeric value
     */
    public static final class ConfigurableDouble extends Configurable {
        
        private final DoubleProperty property;
        private final double minValue;
        private final double maxValue;

        public ConfigurableDouble(String displayName, String apiName, DoubleProperty property, double minValue, double maxValue) {
            super(displayName, apiName);
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
        
        public ConfigurableBoolean(String displayName, String apiName, BooleanProperty property) {
            super(displayName, apiName);
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
        
        public ConfigurableCategory(String displayName, String apiName, ObjectProperty<T> property, Function<T, String> categoryToString, T... possibleValues) {
            super(displayName, apiName);
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

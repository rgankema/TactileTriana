/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.json.simple.JSONObject;

/**
 *
 * @author Richard
 */
public abstract class DeviceBase extends LoggingEntityBase {
    private static int DEVICE_ID = 0;
    private final int id;
    private final String apiDeviceType;
    private final Set<String> apiParameters;
    private final List<Property> properties;
    private final List<ObservableList> lists;
    private final List<ObservableMap> maps;
    
    /**
     * Constructs a new DeviceBase
     * 
     * @param simulation    the simulation that this device is part of
     * @param displayName   the name of the device as it should be shown to the user
     * @param apiDeviceType    the name of the device as specified in the API
     */
    public DeviceBase(SimulationBase simulation, String displayName, String apiDeviceType) {
        super(simulation, displayName, QuantityType.POWER);
        
        id = DEVICE_ID;
        DEVICE_ID++;
        
        this.apiDeviceType = apiDeviceType;
        this.apiParameters = new HashSet<>();
        this.properties = new ArrayList<>();
        this.lists = new ArrayList<>();
        this.maps = new ArrayList<>();
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
     * Returns the set of property keys that the device has, as specified in the 
     * API documentation. These are they key values for the JSON representation
     * of this device.
     * 
     * @return the set of property keys
     */
    public final Set<String> getAPIProperties() {
        return Collections.unmodifiableSet(apiParameters);
    }
    
    /**
     * Returns the properties of this device that may change over the 
     * course of the Simulation. Used by SimulationPrediction to track changes
     * in a device.
     * 
     * @return the list of properties
     */
    public final List<Property> getProperties() {
        return Collections.unmodifiableList(properties);
    }
    
    /**
     * Returns the lists of this device that may change over the course of the
     * Simulation. Used by SimulationPrediction to track changes
     * in a device.
     * 
     * @return the list of lists
     */
    public final List<ObservableList> getLists() {
        return Collections.unmodifiableList(lists);
    }
    
    /**
     * Returns the maps of this device that may change over the course of the
     * Simulation. Used by SimulationPrediction to track changes
     * in a device.
     * 
     * @return the list of maps
     */
    public final List<ObservableMap> getMaps() {
        return Collections.unmodifiableList(maps);
    }
    
    // METHODS

    public void tick(boolean connected) {
        if (!connected) {
            setState(DeviceBase.State.DISCONNECTED);
        } else {
            setState(DeviceBase.State.CONNECTED);
        }
    }
    
    protected final void registerAPIParameter(String parameterName) {
        this.apiParameters.add(parameterName);
    }
    
    protected final void registerProperty(Property property) {
        this.properties.add(property);
    }
    
    protected final void registerList(ObservableList list) {
        this.lists.add(list);
    }
    
    protected final void registerMap(ObservableMap map) {
        this.maps.add(map);
    }
    
    // API
    
    /**
     * Convert this Device and its parameters to a JSON representation as specified 
     * in the API.
     * 
     * @return the JSON representation of the device
     */
    public final JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("deviceID", this.id);
        result.put("deviceType", this.apiDeviceType);
        result.put("consumption", this.getCurrentConsumption());
        
        result.put("parameters", parametersToJSON());
        
        return result;
    }
    
    /**
     * Creates a JSON representation of the parameters of this device as specified
     * in the API.
     * 
     * @return the JSON representation of the device's parameters
     */
    protected abstract JSONObject parametersToJSON();
    
    /**
     * Sets a property to a certain value. Must be overridden by subclasses to
     * set actual parameters. Calling DeviceBase's version will always throw
     * an IllegalArgumentException, and should be called by the subclass if it
     * doesn't know how what to do with the given arguments.
     * 
     * @param parameter  the key of the property to be set (the JSON parameter name)
     * @param value     the value that the property should be set to
     * @throws IllegalArgumentException if the device does not know the given parameter, or cannot apply the given value to it.
     */
    public void updateParameter(String parameter, Object value) {
        throw new IllegalArgumentException("Cannot update parameter " + parameter);
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
}

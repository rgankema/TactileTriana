/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Richard
 */
public class House extends LoggingEntityBase {

    private static int houseID = 0;

    private final ObservableList<DeviceBase> devices;
    private final int id;

    protected final SimulationBase simulation;

    /**
     * Creates a House object
     * @param simulation the simulation of which this house is part.
     */
    public House(SimulationBase simulation) {
        super("House", UnitOfMeasurement.POWER);

        this.simulation = simulation;

        //set the id
        this.id = houseID++;

        devices = FXCollections.observableArrayList();
        devices.addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            while (c.next()) {
                for (DeviceBase removedDevice : c.getRemoved()) {
                    removedDevice.setParentHouse(null);
                }
                for (DeviceBase addedDevice : c.getAddedSubList()) {
                    // State automatically changes on next tick
                    addedDevice.setParentHouse(this);
                }
            }
        });
        
        GlobalSettings.addSettingsChangedHandler(() -> maximumConsumption.set(GlobalSettings.HOUSE_FUSE_MAX_CURRENT * 230));
    }

    /**
     * @return The devices that are connected to the network.
     */
    public ObservableList<DeviceBase> getDevices() {
        return devices;
    }

    /**
     * Returns the ID of the house.
     *
     * @return integer representing the ID of the house.
     */
    public int getId() {
        return id;
    }

    /*
     * The amount of power the house currently consumes. A negative number means
     * the house is producing energy.
     */
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(0.0) {
        @Override
        public void set(double value) {
            if (value > getMaximumConsumption()) {
                value = 0;
                setFuseBlown(true);
            }
            super.set(value);
        }
    };

    /**
     * Returns the current consumption (in watt) property of this house.
     * @return the current consumption of this house.
     */
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption.getReadOnlyProperty();
    }

    /**
     * Returns the current consumption (in watt) property of this house.
     * @return the current consumption of this house.
     */
    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }

    /**
     * Set the current consumption (in watt)
     * @param value consumption (in watt)
     */
    protected final void setCurrentConsumption(double value) {
        this.currentConsumption.set(value);
    }

    /**
     * The absolute maximum of power the house can consume/produce. When more
     * than this is consumed, the fuse in the house will blow.
     */
    private final ReadOnlyDoubleWrapper maximumConsumption = new ReadOnlyDoubleWrapper(230 * GlobalSettings.HOUSE_FUSE_MAX_CURRENT);

    /**
     * Returns the maximum consumption property (in watt). If the current consumption exceeds this value, the fuse will be blown.
     * @return maximum consumption property (in watt)
     */
    public ReadOnlyDoubleProperty maximumConsumptionProperty() {
        return maximumConsumption;
    }

    /**
     * Returns the maximum consumption (in watt). If the current consumption exceeds this value, the fuse will be blown.
     * @return maximum consumption (in watt)
     */
    public final double getMaximumConsumption() {
        return maximumConsumptionProperty().get();
    }

    /**
     * Whether the fuse is blown or not.
     */
    private final ReadOnlyBooleanWrapper fuseBlown = new ReadOnlyBooleanWrapper(false);

    
    /**
     * Returns the fuseBlown property. If the currentConsumption exceeds the maximumConsumption the fuse will be blown. 
     * All the devices will get the state DeviceBase.State.DISCONNECTED (in the next tick) when the fuse is blown.
     * @return fuseBlown property
     */
    public ReadOnlyBooleanProperty fuseBlownProperty() {
        return fuseBlown.getReadOnlyProperty();
    }
    
    /**
     * Returns whether the fuse is blown. If the currentConsumption exceeds the maximumConsumption the fuse will be blown. 
     * All the devices will get the state DeviceBase.State.DISCONNECTED (in the next tick) when the fuse is blown.
     * @return fuseBlown property
     */

    public final boolean isFuseBlown() {
        return fuseBlownProperty().get();
    }

    /**
     * Sets the fuseBlown value.
     * @param fuseBlown 
     */
    protected final void setFuseBlown(boolean fuseBlown) {
        this.fuseBlown.set(fuseBlown);
    }

    /**
     * Repairs the fuse. If more power than the maximum is still
     * produced/consumed, the fuse will blow again immediately.
     */
    public void repairFuse() {
        fuseBlown.set(false);
    }

    /**
     * Calculates the currentConsumption for this House and calls the tick() on all its devices.
     * @param connected is there a cable broken in the tree before this house?
     */
    public void tick(boolean connected) {
        if (isFuseBlown()) {
            connected = false;
        }

        for (DeviceBase device : getDevices()) {
            device.tick(connected);
        }

        setCurrentConsumption(getDevices().stream().mapToDouble(d -> d.getCurrentConsumption()).sum());

        log(simulation.getCurrentTime(), getCurrentConsumption());
    }

    /**
     * Creates a string representation of this House.
     * @param indentation the lavel of indentation in the tree.
     * @return the string representation of this House.
     */
    public String toString(int indentation) {
        String output = "";
        for (int i = 0; i < indentation; i++) {
            output += "\t";
        }
        output += "|-";

        output += "(House:P=" + getCurrentConsumption() + ")";

        return output;
    }

    /**
     * Convert this House and the parameters to a JSON representation as
     * specified in the API
     *
     * @return
     */
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("houseID", this.getId());
        //Add the devices
        JSONArray devices = new JSONArray();
        for (DeviceBase device : this.getDevices()) {
            devices.add(device.toJSON());
        }
        result.put("devices", devices);
        return result;
    }
}

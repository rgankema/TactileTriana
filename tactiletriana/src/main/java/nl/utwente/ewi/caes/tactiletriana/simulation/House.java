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
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Richard
 */
public class House extends LoggingEntityBase {

    private final ObservableList<DeviceBase> devices;

    public House(SimulationBase simulation) {
        super(simulation, "House", QuantityType.POWER);
        devices = FXCollections.observableArrayList();
        devices.addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            while (c.next()) {
                for (DeviceBase removedDevice : c.getRemoved()) {
                    removedDevice.setState(DeviceBase.State.NOT_IN_HOUSE);
                    removedDevice.setParentHouse(null);
                }
                for (DeviceBase addedDevice : c.getAddedSubList()) {
                    // State automatically changes on next tick
                    addedDevice.setParentHouse(this);
                }
            }
        });
    }

    /**
     * @return The devices that are connected to the network.
     */
    public ObservableList<DeviceBase> getDevices() {
        return devices;
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
        this.currentConsumption.set(value);
    }

    /**
     * The absolute maximum of power the house can consume/produce. When more
     * than this is consumed, the fuse in the house will blow.
     */
    private final ReadOnlyDoubleWrapper maximumConsumption = new ReadOnlyDoubleWrapper(230 * SimulationConfig.HOUSE_MAX_FUSE_CURRENT);

    public ReadOnlyDoubleProperty maximumConsumptionProperty() {
        return maximumConsumption;
    }

    public final double getMaximumConsumption() {
        return maximumConsumptionProperty().get();
    }

    /**
     * Whether the fuse is blown or not.
     */
    private final ReadOnlyBooleanWrapper fuseBlown = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty fuseBlownProperty() {
        return fuseBlown.getReadOnlyProperty();
    }

    public final boolean isFuseBlown() {
        return fuseBlownProperty().get();
    }

    
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

    public void tick(boolean connected) {
        if (isFuseBlown()) {
            connected = false;
        }

        for (DeviceBase device : getDevices()) {
            device.tick(connected);
        }

        setCurrentConsumption(getDevices().stream().mapToDouble(d -> d.getCurrentConsumption()).sum());

        log(getCurrentConsumption());
    }

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
     * Convert this House and the parameters to a JSON representation as specified in the API
     * @return 
     */
    public JSONObject toJSON() {
        JSONObject result = new JSONObject();
        result.put("houseID", this.hashCode());
        //Add the devices
        JSONArray devices = new JSONArray();
        for(DeviceBase device : this.getDevices()) {
            devices.add(device.toJSON());
        }
        result.put("devices", devices);
        return result;
    }
}

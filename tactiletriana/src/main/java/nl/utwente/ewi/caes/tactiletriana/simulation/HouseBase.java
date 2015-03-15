/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Richard
 */
public abstract class HouseBase {
    /**
     * @return The devices that are connected to the network.
     */
    public abstract ObservableList<DeviceBase> getDevices();
    
    /*
     * @return The amount of power the house currently consumes. A negative number means
     * house is producing energy.
     */
    public abstract ReadOnlyDoubleProperty currentConsumptionProperty();
    
    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }
    
    /**
     * @return The absolute maximum of power the house can consume/produce. When more than
     * this is consumed, the fuse in the house will blow.
     */
    public abstract ReadOnlyDoubleProperty maximumConsumptionProperty();
    
    public final double getMaximumConsumption() {
        return maximumConsumptionProperty().get();
    }
    
    /**
     * @return Whether the fuse is blown or not.
     */
    public abstract ReadOnlyBooleanProperty fuseBlownProperty();
    
    public final boolean isFuseBlown() {
        return fuseBlownProperty().get();
    }
    
    /**
     * Repairs the fuse. If more power than the maximum is still produced/consumed,
     * the fuse will blow again immediately.
     */
    public abstract void repairFuse();
    
    /**
     * Propagates a tick to all its devices
     * @param time the amount of time that passed since the last tick
     */
    public void tick(double time) {
        for (DeviceBase device : getDevices()) {
            device.tick(time);
        }
    }
}

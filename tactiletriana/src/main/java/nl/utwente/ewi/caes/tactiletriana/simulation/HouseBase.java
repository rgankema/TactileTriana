/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Richard
 */
public abstract class HouseBase {
    /**
     * 
     * @return a list of the devices that are connected to this house
     */
    public abstract ObservableList<DeviceBase> getDevices();
    
    public abstract ReadOnlyDoubleProperty currentConsumptionProperty();
    
    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }
    
    public void tick(double time) {
        for (DeviceBase device : getDevices()) {
            device.tick(time);
        }
    }
}

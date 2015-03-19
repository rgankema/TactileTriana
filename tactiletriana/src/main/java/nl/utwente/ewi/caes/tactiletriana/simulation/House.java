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

/**
 *
 * @author Richard
 */
public class House {
    private final ObservableList<DeviceBase> devices;
    
    public House(){
        devices = FXCollections.observableArrayList();
        
        devices.addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            while(c.next()) {
                for (DeviceBase d : c.getRemoved()) {
                    d.setState(DeviceBase.State.NOT_IN_HOUSE);
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
                setFuseBlown(true);
                value = 0;
            }
            super.set(value);
        }
    };
    
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption.getReadOnlyProperty();
    }
    
    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }
    
    /**
     * The absolute maximum of power the house can consume/produce. When more than
     * this is consumed, the fuse in the house will blow.
     */
    private final ReadOnlyDoubleWrapper maximumConsumption = new ReadOnlyDoubleWrapper(230*100);
    
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
    
    private void setFuseBlown(boolean fuseBlown) {
        this.fuseBlown.set(fuseBlown);
    }
    
    /**
     * Repairs the fuse. If more power than the maximum is still produced/consumed,
     * the fuse will blow again immediately.
     */
    public void repairFuse() {
        fuseBlown.set(false);
    }
    
    /**
     * Propagates a tick to all its devices
     * @param time the amount of time that passed since the last tick
     * @param connected whether the house is connected to the transformer
     */
    public void tick(double time, boolean connected) {
        if (isFuseBlown()) connected = false;
        
        for (DeviceBase device : getDevices()) {
            device.tick(time, connected);
        }
        currentConsumption.set(devices.stream().mapToDouble(DeviceBase::getCurrentConsumption).sum());
    }
    
    public String toString(int indentation){
        String output = "";
        for (int i = 0; i < indentation; i++){
            output += "\t";
        }
        output += "|-";
        
        output += "(House:P="+getCurrentConsumption()+")";
        
        return output;
    }
}

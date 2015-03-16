/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.binding.Bindings;
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
public class House extends HouseBase {
    
    private final ObservableList<DeviceBase> devices;
    
    public House(){
        devices = FXCollections.observableArrayList();
        
        devices.addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            while(c.next()) {
                for (DeviceBase d : c.getAddedSubList()) {
                    d.setState(DeviceBase.State.CONNECTED);
                }
                for (DeviceBase d : c.getRemoved()) {
                    d.setState(DeviceBase.State.NOT_IN_HOUSE);
                }
            }
        });
    }
    
    @Override
    public ObservableList<DeviceBase> getDevices() {
        return devices;
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
    
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(0.0);
    
    @Override
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption.getReadOnlyProperty();
    }

    private final ReadOnlyDoubleWrapper maximumConsumption = new ReadOnlyDoubleWrapper(100 * 230);
    
    @Override
    public ReadOnlyDoubleProperty maximumConsumptionProperty() {
        return maximumConsumption;
    }

    private final ReadOnlyBooleanWrapper fuseBlown = new ReadOnlyBooleanWrapper(false); //TODO: als deze blown raakt moeten alle devices erachter disconnected zijn
    
    @Override
    public ReadOnlyBooleanProperty fuseBlownProperty() {
        return fuseBlown;
    }

    @Override
    public void repairFuse() {
        fuseBlown.set(false);
    }
    
    @Override
    public void tick(double time, boolean connected) {
        super.tick(time, connected);
        currentConsumption.set(devices.stream().mapToDouble(DeviceBase::getCurrentConsumption).sum());
    }
}

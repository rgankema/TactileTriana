/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
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
    
    private ObservableList<DeviceBase> devices;
    
    public House(){
        devices = FXCollections.observableArrayList();
        
        currentConsumption.bind(Bindings.createDoubleBinding(() -> { 
            double sum = 0.0;
            for (DeviceBase device : devices) {
                sum += device.getCurrentConsumption();
            }
            return sum;
        }, devices));
        
        devices.addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            for (DeviceBase d : c.getAddedSubList()) {
                d.setState(DeviceBase.State.CONNECTED);
            }
            for (DeviceBase d : c.getRemoved()) {
                d.setState(DeviceBase.State.DISCONNECTED);
            }
        });
    }
    
    @Override
    public ObservableList<DeviceBase> getDevices() {
        return devices;
    }
    
    @Override
    public String toString(){
        return "(House:P="+getCurrentConsumption()+")";
    }
    
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(0.0);
    
    @Override
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption.getReadOnlyProperty();
    }

    @Override
    public ReadOnlyDoubleProperty maximumConsumptionProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReadOnlyBooleanProperty fuseBlownProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void repairFuse() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import com.sun.javafx.collections.ObservableListWrapper;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Richard
 */
public class House extends HouseBase {
    
    private ObservableList<DeviceBase> devices;
    
    public House(){
        devices = FXCollections.observableArrayList();    
    }
    
    @Override
    public ObservableList<DeviceBase> getDevices() {
        return devices;
    }
    
    @Override
    public void addDevice(DeviceBase d) {
        devices.add(d);
    }
    
    @Override
    public String toString(){
        return "(House:P="+getConsumption()+")";
    }
    
    
    //TODO implement
    public double getConsumption() {
        // sum of all the devices
        double consumption = 0;
        for (DeviceBase d : this.getDevices()){
            consumption += d.getCurrentConsumption();
        }
        return consumption;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import javafx.collections.ObservableList;

/**
 *
 * @author Richard
 */
public class House extends HouseBase {
    
    private ObservableList<DeviceBase> devices;
    
    @Override
    public ObservableList<DeviceBase> getDevices() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void addDevice(DeviceBase d) {
        devices.add(d);
    }
    
    @Override
    public String toString(){
        return "House";
    }
    
    
    //TODO implement
    public double getConsumption() {
        return 10;
    }

    
}

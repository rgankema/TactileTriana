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
public interface IHouse {
    /**
     * 
     * @return a list of the devices that are connected to this house
     */
    public ObservableList<IDevice> getDevices();
}

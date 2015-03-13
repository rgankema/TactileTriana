/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Richard
 */

public abstract class DeviceBase {
    /**
     * 
     * @return the amount of power that the device currently consumes
     */
    public abstract ReadOnlyDoubleProperty currentConsumptionProperty();
    
    public final double getCurrentConsumption() {
        return currentConsumptionProperty().get();
    }
    
    /**
     * Called by the simulation for every tick. The Device calculates its consumption
     * for that tick, and updates it.
     * @param time  the amount of time that passed since the last tick
     */
    public abstract void tick(double time);
}

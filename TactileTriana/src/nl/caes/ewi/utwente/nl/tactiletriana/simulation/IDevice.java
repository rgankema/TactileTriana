/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Richard
 */
public interface IDevice {
    public ReadOnlyDoubleProperty currentConsumptionProperty();
    public double getCurrentConsumption();
    
    /**
     * Called by the simulation for every tick. The Device calculates its consumption
     * for that tick, and updates it.
     * @param time  the amount of time that passed since the last tick
     */
    public void tick(double time);
}

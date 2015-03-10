/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

/**
 *
 * @author mickvdv
 */
public class MockupDevice extends DeviceBase {
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(Double.MAX_VALUE);
    
    @Override
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption;
    }

    @Override
    public double getCurrentConsumption() {
        return currentConsumption.get();
    }

    @Override
    public void tick(double time) {
        currentConsumption.set(300);
    }
    
}

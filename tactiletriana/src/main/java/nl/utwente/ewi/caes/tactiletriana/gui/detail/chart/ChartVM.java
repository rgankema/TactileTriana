/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.chart;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import nl.utwente.ewi.caes.tactiletriana.simulation.Entity;

/**
 *
 * @author Richard
 */
public class ChartVM {
    private Entity entity;
    
    public ChartVM(Entity entity) {
        this.entity = entity;
        
        // Set label of series
        switch (entity.getCharacteristic()) {
            case POWER: 
                seriesName.set("Power Consumption");
                break;
            case VOLTAGE:
                seriesName.set("Voltage");
                break;
            case CURRENT:
                seriesName.set("Current");
                break;
        }
        
        seriesAbsMax.set(entity.getCharacteristicAbsMax());
        
    }
    
    private ReadOnlyStringWrapper seriesName = new ReadOnlyStringWrapper();
    
    public ReadOnlyStringProperty seriesNameProperty() {
        return seriesName;
    }
    
    private ReadOnlyDoubleWrapper seriesAbsMax = new ReadOnlyDoubleWrapper();
    
    public ReadOnlyDoubleProperty seriesAbsMax() {
        return seriesAbsMax;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.chart;

import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import nl.utwente.ewi.caes.tactiletriana.simulation.Entity;

/**
 *
 * @author Richard
 */
public class ChartVM {
    private final Entity entity;
    private final ObservableList<XYChart.Data<String, Number>> seriesData;
    
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
        
        seriesData = FXCollections.observableList(new ArrayList<XYChart.Data<String, Number>>());
        entity.getCharacteristicMap().addListener((MapChangeListener.Change<? extends LocalDateTime, ? extends Double> c) -> {
            LocalDateTime time = c.getKey();
            String timeString = String.format("%02d:%02d", time.getHour(), time.getMinute());
            
            if (c.wasRemoved()) {
                seriesData.removeIf(data -> data.getXValue().equals(timeString) && data.getYValue() == c.getValueRemoved());
            }
            if (c.wasAdded()) {
                if (seriesData.size() > 0 ) {
                    seriesData.add(new XYChart.Data<>(timeString, seriesData.get(seriesData.size() - 1).getYValue()));
                }
                
                seriesData.add(new XYChart.Data<>(timeString, c.getValueAdded()));
                
                if (seriesData.size() > 288) { // 288 keer 5 minuten in een dag
                    seriesData.remove(0);
                }
            }
        });
    }
    
    private ReadOnlyStringWrapper seriesName = new ReadOnlyStringWrapper();
    
    public ReadOnlyStringProperty seriesNameProperty() {
        return seriesName;
    }
    
    private ReadOnlyDoubleWrapper seriesAbsMax = new ReadOnlyDoubleWrapper();
    
    public ReadOnlyDoubleProperty seriesAbsMaxProperty() {
        return seriesAbsMax;
    }
    
    public ObservableList<XYChart.Data<String, Number>> getSeriesData() {
        return seriesData;
    }
    
    
}

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
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntity;

/**
 *
 * @author Richard
 */
public class ChartVM {
    private LoggingEntity entity;
    private ObservableList<XYChart.Data<Number, Number>> seriesData;
    
    public ChartVM(LoggingEntity entity) {
        seriesData = FXCollections.observableList(new ArrayList<XYChart.Data<Number, Number>>());
        
        setEntity(entity);
    }
    
    // BINDABLE PROPERTIES
    
    /**
     * The name of the chart
     */
    private ReadOnlyStringWrapper chartTitle = new ReadOnlyStringWrapper();
    
    public ReadOnlyStringProperty chartTitleProperty() {
        return chartTitle.getReadOnlyProperty();
    }
    
    /**
     * The name of the series
     */
    private ReadOnlyStringWrapper seriesName = new ReadOnlyStringWrapper();
    
    public ReadOnlyStringProperty seriesNameProperty() {
        return seriesName.getReadOnlyProperty();
    }
    
    /**
     * The absolute y value within which the series is bounded
     */
    private ReadOnlyDoubleWrapper yAxisAbsBound = new ReadOnlyDoubleWrapper();
    
    public ReadOnlyDoubleProperty yAxisAbsBoundProperty() {
        return yAxisAbsBound.getReadOnlyProperty();
    }
    
    /**
     * The lower bound of the x axis
     */
    private ReadOnlyDoubleWrapper xAxisLowerBound = new ReadOnlyDoubleWrapper();
    
    public ReadOnlyDoubleProperty xAxisLowerBoundProperty() {
        return xAxisLowerBound.getReadOnlyProperty();
    }
    
    /**
     * The upper bound of the x axis
     */
    private ReadOnlyDoubleWrapper xAxisUpperBound = new ReadOnlyDoubleWrapper();
    
    public ReadOnlyDoubleProperty xAxisUpperBoundProperty() {
        return xAxisUpperBound.getReadOnlyProperty();
    }
    
    /**
     * 
     * @return the data for the series
     */
    public ObservableList<XYChart.Data<Number, Number>> getSeriesData() {
        return seriesData;
    }
    
    // PUBLIC METHODS
    
    public final void setEntity(LoggingEntity entity) {
        if (this.entity != null) {
            this.entity.getLog().removeListener(logListener);
        }
        
        this.entity = entity;
        
        // Set label of series
        switch (entity.getLoggedValueType()) {
            case POWER: 
                seriesName.set("Power Consumption (W)");
                break;
            case VOLTAGE:
                seriesName.set("Voltage (V)");
                break;
            case CURRENT:
                seriesName.set("Current (A)");
                break;
        }
        
        // Set label of chart
        chartTitle.set(entity.getDisplayName() + " " + seriesName.get());
        
        xAxisUpperBound.bind(xAxisLowerBound.add(60*12));
        
        if (entity.getAbsoluteMaximum() != Double.POSITIVE_INFINITY) {
            yAxisAbsBound.set(entity.getAbsoluteMaximum());
        }
        
        seriesData.clear();
        
        logListener = new MapChangeListener<LocalDateTime, Double>() {
            @Override
            public void onChanged(MapChangeListener.Change<? extends LocalDateTime, ? extends Double> c) {
                LocalDateTime time = c.getKey();
                int minuteOfYear = (time.getDayOfYear() - 1) * 24 * 60 + time.getHour() * 60 + time.getMinute();
                
                if (c.wasRemoved()) {
                    int i = 0;
                    for (; i < seriesData.size(); i++) {
                        XYChart.Data data = seriesData.get(i);
                        if (data.getXValue().equals(minuteOfYear) && data.getYValue() == c.getValueRemoved()) {
                            break;
                        }
                    }
                    seriesData.remove(i);
                    if (i > 0) seriesData.remove(i - 1);
                }
                if (c.wasAdded()) {
                    // Add datapoint with previous value to obtain horizontal lines
                    if (seriesData.size() > 0 ) {
                        seriesData.add(new XYChart.Data<>(minuteOfYear, seriesData.get(seriesData.size() - 1).getYValue()));
                    }
                    seriesData.add(new XYChart.Data<>(minuteOfYear, c.getValueAdded()));
                    
                    // Range x axis
                    if (seriesData.size() < 288) {
                        xAxisLowerBound.set((Integer)seriesData.get(0).getXValue());
                    }
                    else {
                        xAxisLowerBound.set((Integer)seriesData.get(seriesData.size() - 288).getXValue());
                    }
                    
                    // Range y axis
                    if (Math.abs(c.getValueAdded()) > yAxisAbsBound.get()) {
                        yAxisAbsBound.set(c.getValueAdded());
                    }
                }
            }
        };
        entity.getLog().addListener(logListener);
    }
    
    private MapChangeListener logListener;
}

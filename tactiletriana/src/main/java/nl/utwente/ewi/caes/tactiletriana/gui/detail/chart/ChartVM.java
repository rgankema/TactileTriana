/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.chart;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.chart.XYChart.Data;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;

/**
 *
 * @author Richard
 */
public class ChartVM {

    private LoggingEntityBase actual;
    private LoggingEntityBase future;
    private final ObservableMap<Class<? extends LoggingEntityBase>, ObservableList<Data<Number, Number>>> seriesByType; 
    private final Map<Class<? extends LoggingEntityBase>, MapChangeListener<LocalDateTime, Double>> listenerByType;
    
    public ChartVM() {
        seriesByType = FXCollections.observableMap(new HashMap<>());
        listenerByType = new HashMap<>();
    }

    // BINDABLE PROPERTIES
    /**
     * The name of the chart
     */
    private final ReadOnlyStringWrapper chartTitle = new ReadOnlyStringWrapper();

    public ReadOnlyStringProperty chartTitleProperty() {
        return chartTitle.getReadOnlyProperty();
    }

    /**
     * The name of the series
     */
    private final ReadOnlyStringWrapper seriesName = new ReadOnlyStringWrapper();

    public ReadOnlyStringProperty seriesNameProperty() {
        return seriesName.getReadOnlyProperty();
    }
    
    /**
     * The lower bound of the x axis
     */
    private final ReadOnlyDoubleWrapper xAxisLowerBound = new ReadOnlyDoubleWrapper();

    public ReadOnlyDoubleProperty xAxisLowerBoundProperty() {
        return xAxisLowerBound.getReadOnlyProperty();
    }

    /**
     * The upper bound of the x axis
     */
    private final ReadOnlyDoubleWrapper xAxisUpperBound = new ReadOnlyDoubleWrapper();

    public ReadOnlyDoubleProperty xAxisUpperBoundProperty() {
        return xAxisUpperBound.getReadOnlyProperty();
    }
    
    public ObservableMap<Class<? extends LoggingEntityBase>, ObservableList<Data<Number, Number>>> getSeriesByType() {
        return seriesByType;
    }
    
    /**
     * 
     * @return the type of value that is shown on the chart
     */
    public LoggingEntityBase.QuantityType getValueType() {
        return actual.getQuantityType();
    }
    
    // PUBLIC METHODS
    
    public final void setEntity(LoggingEntityBase actual, LoggingEntityBase future) {
        // Reset chart
        if (this.future != null) {
            for (Class<? extends LoggingEntityBase> et : this.future.getLogsByEntityType().keySet()) {
                if (listenerByType.get(et) != null)
                    this.future.getLogsByEntityType().get(et).removeListener(listenerByType.get(et));
                listenerByType.remove(et);
                if (seriesByType.get(et) != null)
                    seriesByType.remove(et).clear();
            }          
        }
        
        this.actual = actual;
        this.future = future;

        // Set label of series
        switch (actual.getQuantityType()) {
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
        chartTitle.set(actual.getDisplayName() + " " + seriesName.get());

        // Show 12 hours of data
        xAxisUpperBound.bind(xAxisLowerBound.add(60 * 12));

        for (Class<? extends LoggingEntityBase> et : future.getLogsByEntityType().keySet()) {
            ObservableList<Data<Number, Number>> seriesData = FXCollections.observableArrayList();
            seriesByType.put(et, seriesData);
            Map<LocalDateTime, Double> log = future.getLogsByEntityType().get(et);
            
            // Update chart with recorded data
            for (LocalDateTime time : log.keySet()) {
                int minuteOfYear = (time.getDayOfYear() - 1) * 24 * 60 + time.getHour() * 60 + time.getMinute();
                if (seriesData.size() > 0) {
                    seriesData.add(new Data<>(minuteOfYear, seriesData.get(seriesData.size() - 1).getYValue()));
                }
                seriesData.add(new Data(minuteOfYear, log.get(time)));
            }

            MapChangeListener<LocalDateTime, Double> logListener = 
                    (MapChangeListener<LocalDateTime, Double>) c -> {
                LocalDateTime time = c.getKey();
                int minuteOfYear = (time.getDayOfYear() - 1) * 24 * 60 + time.getHour() * 60 + time.getMinute();

                if (c.wasRemoved()) {
                    int i = 0;
                    for (; i < seriesData.size(); i++) {
                        Data data = seriesData.get(i);
                        if (data.getXValue().equals(minuteOfYear) && data.getYValue().equals(c.getValueRemoved())) {
                            break;
                        }
                    }
                    seriesData.remove(i);
                    if (i > 0) {
                        seriesData.remove(i - 1);
                    }
                }
                if (c.wasAdded()) {
                    // Add datapoint with previous value to obtain horizontal lines
                    if (seriesData.size() > 0) {
                        seriesData.add(new Data<>(minuteOfYear, seriesData.get(seriesData.size() - 1).getYValue()));
                    } else {
                        seriesData.add(new Data<>(minuteOfYear, 0d));
                    }
                    seriesData.add(new Data<>(minuteOfYear, c.getValueAdded()));

                    // Range x axis
                    xAxisLowerBound.set(minuteOfYear - 12 * 60);
                }
            };

            future.getLogsByEntityType().get(et).addListener(logListener);
            listenerByType.put(et, logListener);
        }
    }
}

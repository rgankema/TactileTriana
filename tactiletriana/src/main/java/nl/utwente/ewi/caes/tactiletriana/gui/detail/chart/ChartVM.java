/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.chart;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.chart.XYChart.Data;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;
import static nl.utwente.ewi.caes.tactiletriana.util.Util.toEpochMinutes;

/**
 *
 * @author Richard
 */
public class ChartVM {

    private LoggingEntityBase actual;
    private LoggingEntityBase future;
    private final ObservableMap<String, ObservableList<Data<Number, Number>>> seriesByType;
    
    public ChartVM() {
        seriesByType = FXCollections.observableMap(new HashMap<>());
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
    
    public ObservableMap<String, ObservableList<Data<Number, Number>>> getSeriesByType() {
        return seriesByType;
    }
    
    /**
     * 
     * @return the type of value that is shown on the chart
     */
    public LoggingEntityBase.QuantityType getValueType() {
        return actual.getQuantityType();
    }
    
    public String getDefaultSeries() {
        return actual.getDefault();
    }
    
    // PUBLIC METHODS
    
    public final void setEntity(LoggingEntityBase actual, LoggingEntityBase future) {
        // Reset chart
        seriesByType.clear();
        xAxisLowerBound.unbind();
        xAxisUpperBound.unbind();
        
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
        xAxisLowerBound.bind(xAxisUpperBound.subtract(60 * 12));
        xAxisUpperBound.bind(Bindings.createLongBinding(() -> { 
            return toEpochMinutes(future.getSimulation().getCurrentTime()); 
        }, future.getSimulation().currentTimeProperty()));
        
        // Add series
        for (String logName : future.getLogsByName().keySet()) {
            seriesByType.put(logName, future.getLogsByName().get(logName));
        }
    }
}

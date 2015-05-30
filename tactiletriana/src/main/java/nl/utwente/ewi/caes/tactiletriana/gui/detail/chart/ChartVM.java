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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;

/**
 *
 * @author Richard
 */
public class ChartVM {

    private LoggingEntityBase actual;
    private LoggingEntityBase future;
    private ListChangeListener actualLogListener;
    private ListChangeListener futureLogListener;
    private final ObservableList<XYChart.Data<Integer, Float>> actualSeriesData;
    private final ObservableList<XYChart.Data<Integer, Float>> futureSeriesData;
    
    public ChartVM() {
        actualSeriesData = FXCollections.observableArrayList();
        futureSeriesData = FXCollections.observableArrayList();
    }

    // BINDABLE PROPERTIES
    /**
     * The name of the chart
     */
    private final ReadOnlyStringWrapper chartTitle = new ReadOnlyStringWrapper("Long press to show on chart");

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
    
    /**
     * 
     * @return the type of value that is shown on the chart
     */
    public LoggingEntityBase.QuantityType getValueType() {
        return actual.getQuantityType();
    }

    /**
     *
     * @return the data for the actual series
     */
    public ObservableList<XYChart.Data<Integer, Float>> getActualSeriesData() {
        return actualSeriesData;
    }

    /**
     *
     * @return the data for the future series
     */
    public ObservableList<XYChart.Data<Integer, Float>> getFutureSeriesData() {
        return futureSeriesData;
    }
    
    // PUBLIC METHODS
    
    public final void setEntity(LoggingEntityBase actual, LoggingEntityBase future) {
        if (this.actual != null) {
            this.actual.getLog().removeListener(actualLogListener);
            this.future.getLog().removeListener(futureLogListener);
        }
        
        actualSeriesData.clear();
        futureSeriesData.clear();
        
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

        // Update chart with recorded data
        for (Data<Integer, Float> data : future.getLog()) {
            futureSeriesData.add(data);
        }
        
        for (Data<Integer, Float> data : actual.getLog()) {
            actualSeriesData.add(data);
        }
        
        actualLogListener = new ListChangeListener<Data<Integer, Float>>() {
            
            @Override
            public void onChanged(Change<? extends Data<Integer, Float>> c) {
                while (c.next()) {
                    for (Data<Integer, Float> data : c.getAddedSubList()) {
                        // Remove all data from the future series that has an X before or equal to the new value
                        int i = 0;
                        for (Data<Integer, Float> futureData : futureSeriesData) {
                            // This only works as long as futureSeriesData is sorted, which is a safe assumption
                            if (futureData.getXValue() < data.getXValue()) {
                                i++;
                            } else {
                                break;
                            }
                        }
                        futureSeriesData.remove(0, i);
                        
                        xAxisLowerBound.set(data.getXValue() - 6 * 60);
                        actualSeriesData.add(data);
                    }
                    for (Data<Integer, Float> data : c.getRemoved()) {
                        actualSeriesData.remove(data);
                    }
                }
            }
        };
        
        futureLogListener = new ListChangeListener<Data<Integer, Float>>() {

            @Override
            public void onChanged(Change<? extends Data<Integer, Float>> c) {
                while (c.next()) {
                    for (Data<Integer, Float> data : c.getAddedSubList()) {
                        futureSeriesData.add(data);
                    }
                    for (Data<Integer, Float> data : c.getRemoved()) {
                        futureSeriesData.remove(data);
                    }
                }
            }
        };
        
        actual.getLog().addListener(actualLogListener);
        future.getLog().addListener(futureLogListener);
    }
}

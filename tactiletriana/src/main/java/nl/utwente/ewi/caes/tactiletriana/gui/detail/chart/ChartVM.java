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
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;

/**
 *
 * @author Richard
 */
public class ChartVM {

    private LoggingEntityBase actual;
    private LoggingEntityBase future;
    private MapChangeListener actualLogListener;
    private MapChangeListener futureLogListener;
    private final ObservableList<XYChart.Data<Number, Number>> actualSeriesData;
    private final ObservableList<XYChart.Data<Number, Number>> futureSeriesData;
    
    public ChartVM() {
        actualSeriesData = FXCollections.observableArrayList();
        futureSeriesData = FXCollections.observableArrayList();
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
    
    public LoggingEntityBase.LoggedValueType getValueType() {
        return actual.getLoggedValueType();
    }

    /**
     *
     * @return the data for the actual series
     */
    public ObservableList<XYChart.Data<Number, Number>> getActualSeriesData() {
        return actualSeriesData;
    }

    /**
     *
     * @return the data for the future series
     */
    public ObservableList<XYChart.Data<Number, Number>> getFutureSeriesData() {
        return futureSeriesData;
    }
    
    // PUBLIC METHODS
    
    public final void setEntity(LoggingEntityBase actual, LoggingEntityBase future) {
        // Reset chart
        if (this.actual != null) {
            this.actual.getLog().removeListener(actualLogListener);
            this.future.getLog().removeListener(futureLogListener);           
        }
 
        if (actual instanceof Node) {
            System.out.println(((Node)actual).toString(0));
            System.out.println(((Node)future).toString(0));
        }
        
        actualSeriesData.clear();
        futureSeriesData.clear();
        
        this.actual = actual;
        this.future = future;

        // Set label of series
        switch (actual.getLoggedValueType()) {
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
        for (LocalDateTime time : actual.getLog().keySet()) {
            int minuteOfYear = (time.getDayOfYear() - 1) * 24 * 60 + time.getHour() * 60 + time.getMinute();
            if (actualSeriesData.size() > 0) {
                actualSeriesData.add(new XYChart.Data<>(minuteOfYear, actualSeriesData.get(actualSeriesData.size() - 1).getYValue()));
            }
            actualSeriesData.add(new XYChart.Data(minuteOfYear, actual.getLog().get(time)));
        }
        
        for (LocalDateTime time : future.getLog().keySet()) {
            int minuteOfYear = (time.getDayOfYear() - 1) * 24 * 60 + time.getHour() * 60 + time.getMinute();
            if (futureSeriesData.size() > 0) {
                futureSeriesData.add(new XYChart.Data<>(minuteOfYear, futureSeriesData.get(futureSeriesData.size() - 1).getYValue()));
            }
            futureSeriesData.add(new XYChart.Data(minuteOfYear, future.getLog().get(time)));
        }
        
        // Update chart when a new value is logged
        actualLogListener = (MapChangeListener<LocalDateTime, Double>) c -> {
            LocalDateTime time = c.getKey();
            int minuteOfYear = (time.getDayOfYear() - 1) * 24 * 60 + time.getHour() * 60 + time.getMinute();
            
            if (c.wasRemoved()) {
                int i = 0;
                for (; i < actualSeriesData.size(); i++) {
                    XYChart.Data data = actualSeriesData.get(i);
                    if (data.getXValue().equals(minuteOfYear) && data.getYValue() == c.getValueRemoved()) {
                        break;
                    }
                }
                actualSeriesData.remove(i);
                if (i > 0) {
                    actualSeriesData.remove(i - 1);
                }
            }
            if (c.wasAdded()) {
                // Add datapoint with previous value to obtain horizontal lines
                if (actualSeriesData.size() > 0) {
                    actualSeriesData.add(new XYChart.Data<>(minuteOfYear, actualSeriesData.get(actualSeriesData.size() - 1).getYValue()));
                }
                actualSeriesData.add(new XYChart.Data<>(minuteOfYear, c.getValueAdded()));
                
                // Range x axis
                xAxisLowerBound.set(minuteOfYear - 6 * 60);
            }
        };
        
        futureLogListener = (MapChangeListener<LocalDateTime, Double>) c -> {
            LocalDateTime time = c.getKey();
            int minuteOfYear = (time.getDayOfYear() - 1) * 24 * 60 + time.getHour() * 60 + time.getMinute();
            
            if (c.wasRemoved()) {
                int i = 0;
                for (; i < futureSeriesData.size(); i++) {
                    XYChart.Data data = futureSeriesData.get(i);
                    if (data.getXValue().equals(minuteOfYear) && data.getYValue() == c.getValueRemoved()) {
                        break;
                    }
                }
                futureSeriesData.remove(i);
                if (i > 0) {
                    futureSeriesData.remove(i - 1);
                }
            }
            if (c.wasAdded()) {
                // Add datapoint with previous value to obtain horizontal lines
                if (futureSeriesData.size() > 0) {
                    futureSeriesData.add(new XYChart.Data<>(minuteOfYear, futureSeriesData.get(futureSeriesData.size() - 1).getYValue()));
                }
                futureSeriesData.add(new XYChart.Data<>(minuteOfYear, c.getValueAdded()));
            }
        };
        
        actual.getLog().addListener(actualLogListener);
        future.getLog().addListener(futureLogListener);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.chart;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.util.StringConverter;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;

/**
 *
 * @author Richard
 */
public class ChartView extends Group {

    @FXML
    private StackedAreaChart chart;
    @FXML
    private NumberAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    
    private ChartVM viewModel;

    public ChartView() {
        ViewLoader.load(this);
    }

    public void setViewModel(ChartVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel can only be set once");
        }

        this.viewModel = viewModel;

        
        yAxis.setAutoRanging(true);
        yAxis.labelProperty().bind(viewModel.seriesNameProperty());
        
        xAxis.lowerBoundProperty().bind(viewModel.xAxisLowerBoundProperty());
        xAxis.upperBoundProperty().bind(viewModel.xAxisUpperBoundProperty());
        xAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {
                double totalMinutes = (double) object;
                
                int minutes = (int) (totalMinutes % 60);
                int hours = (int) ((totalMinutes - minutes) / 60) % 24;
                return String.format("%02d:%02d", hours, minutes);
            }

            @Override
            public Number fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        
        chart.titleProperty().bind(viewModel.chartTitleProperty());
        
        viewModel.getSeriesByType().addListener((MapChangeListener.Change<? extends Class<? extends LoggingEntityBase>, ? extends ObservableList<Data<Number, Number>>> c) -> {
            if (c.wasRemoved()) {
                chart.getData().remove(c.getValueRemoved());
            }
            if (c.wasAdded()) {
                Series<Number, Number> s = new Series<>();
                s.setData(c.getValueAdded());
                chart.getData().add(s);
            }
        });
    }
}

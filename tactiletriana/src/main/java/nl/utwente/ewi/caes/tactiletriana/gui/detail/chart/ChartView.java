/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.chart;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class ChartView extends Group {
    @FXML private AreaChart chart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    private Series<Number, Number> series;
    
    private ChartVM viewModel;
    
    public ChartView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(ChartVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel can only be set once");
        
        this.viewModel = viewModel;
        
        series = new Series<>();
        series.nameProperty().bind(viewModel.seriesNameProperty());
        Bindings.bindContent(series.getData(), viewModel.getSeriesData());
        
        
        yAxis.lowerBoundProperty().bind(viewModel.yAxisAbsBoundProperty().negate());
        yAxis.upperBoundProperty().bind(viewModel.yAxisAbsBoundProperty());
        yAxis.tickUnitProperty().bind(viewModel.yAxisAbsBoundProperty().divide(5));
        yAxis.labelProperty().bind(viewModel.seriesNameProperty());
        
        xAxis.lowerBoundProperty().bind(viewModel.xAxisLowerBoundProperty());
        xAxis.upperBoundProperty().bind(viewModel.xAxisUpperBoundProperty());
        
        chart.getData().add(series);
    }
}

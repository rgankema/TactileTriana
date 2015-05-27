/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.chart;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class ChartView extends StackPane {

    @FXML private LineChart chart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    private Series<Integer, Float> actualSeries;
    private Series<Integer, Float> futureSeries;
    
    private ChartVM viewModel;

    public ChartView() {
        ViewLoader.load(this);
        
        // Get label text as defined in FXML
        final String xAxisLabelText = xAxis.getLabel();
        xAxis.labelProperty().bind(Bindings.createStringBinding(() -> (isAxisLabelsVisible()) ? xAxisLabelText : "", axisLabelsVisible));
        xAxis.tickLabelsVisibleProperty().bind(axisLabelsVisible);
        yAxis.tickLabelsVisibleProperty().bind(axisLabelsVisible);
    }

    public void setViewModel(ChartVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel can only be set once");
        }

        this.viewModel = viewModel;

        actualSeries = new Series<>();
        Bindings.bindContent(actualSeries.getData(), viewModel.getActualSeriesData());
        futureSeries = new Series<>();
        Bindings.bindContent(futureSeries.getData(), viewModel.getFutureSeriesData());
        
        yAxis.setAutoRanging(true);
        yAxis.labelProperty().bind(Bindings.createStringBinding(() -> { 
            if (isAxisLabelsVisible()) {
                return viewModel.seriesNameProperty().get();
            }
            return "";
        }, viewModel.seriesNameProperty(), axisLabelsVisible));
        
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
        
        chart.getData().add(actualSeries);
        chart.getData().add(futureSeries);
        
    }
    
    // PROPERTIES
    
    /**
     * Whether the axis labels are visible for this chartview
     */
    private final BooleanProperty axisLabelsVisible = new SimpleBooleanProperty(true);
    
    public BooleanProperty axisLabelsVisibleProperty() {
        return axisLabelsVisible;
    }
    
    public final boolean isAxisLabelsVisible() {
        return axisLabelsVisibleProperty().get();
    }
    
    public final void setAxisLabelsVisible(boolean visible) {
        axisLabelsVisibleProperty().set(visible);
    }
}

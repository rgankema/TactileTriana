/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.chart.ChartView;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime.DateTimeView;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.weather.WeatherView;

/**
 *
 * @author Richard
 */
public class DetailView extends BorderPane {

    @FXML
    private StackPane header;
    @FXML
    private DateTimeView dateTimeView;
    @FXML
    private ChartView mainChart;
    @FXML
    private ChartView subChart1;
    @FXML
    private ChartView subChart2;
    @FXML
    private ChartView subChart3;
    @FXML
    private WeatherView weatherView;
    @FXML
    private ImageView trianaLogo;

    private DetailVM viewModel;

    public DetailView() {
        ViewLoader.load(this);
    }

    public void setViewModel(DetailVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel can only be set once");
        }

        this.viewModel = viewModel;

        dateTimeView.setViewModel(viewModel.getDateTimeVM());
        mainChart.setViewModel(viewModel.getChartVM());
        weatherView.setViewModel(viewModel.getWeatherVM());

        subChart1.setViewModel(viewModel.getSubChartVM(0));
        subChart2.setViewModel(viewModel.getSubChartVM(1));
        subChart3.setViewModel(viewModel.getSubChartVM(2));

        // Fade in and out when a the simulation shifts to a new timespan
        final FadeTransition fadeOut = new FadeTransition(Duration.millis(200), mainChart);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        final FadeTransition fadeIn = new FadeTransition(Duration.millis(300), mainChart);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        final SequentialTransition transition = new SequentialTransition(fadeOut, fadeIn);
        viewModel.setOnSimulationTimeSpanChange(() -> transition.playFromStart());
    }
    /*
    /**
     * Resets the view to the initial state
     */
    /*
    public void reset() {
        viewModel.reset();
    }*/
}

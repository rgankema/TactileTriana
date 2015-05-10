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

    @FXML private DateTimeView dateTimeView;
    @FXML private ChartView chartView;
    @FXML private WeatherView weatherView;
    @FXML private ImageView trianaLogo;

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
        chartView.setViewModel(viewModel.getChartVM());
        weatherView.setViewModel(viewModel.getWeatherVM());
        
        final FadeTransition fadeOut = new FadeTransition(Duration.millis(200), chartView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        final FadeTransition fadeIn = new FadeTransition(Duration.millis(300), chartView);
        fadeOut.setFromValue(0.0);
        fadeOut.setToValue(1.0);
        
        final SequentialTransition transition = new SequentialTransition(fadeOut, fadeIn);
        
        viewModel.setOnSimulationTimeSpanChange(timeSpan -> transition.playFromStart());
    }
}

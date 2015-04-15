/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.weather;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class WeatherView extends GridPane {
    @FXML private Label radianceValueLabel;
    @FXML private Label temperatureValueLabel;
    
    private WeatherVM viewModel;
    
    public WeatherView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(WeatherVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel can only be set once");
        
        radianceValueLabel.textProperty().bind(viewModel.radianceLabelProperty());
        temperatureValueLabel.textProperty().bind(viewModel.temperatureLabelProperty());
    }
}

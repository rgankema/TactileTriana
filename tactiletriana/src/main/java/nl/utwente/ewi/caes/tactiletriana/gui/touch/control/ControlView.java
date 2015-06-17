/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class ControlView extends HBox {

    @FXML private Button pauseButton;
    @FXML private Button disableTrianaButton;

    private ControlVM viewModel;

    public ControlView() {
        ViewLoader.load(this);
    }

    public void setViewModel(ControlVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel may only be set once");
        }

        this.viewModel = viewModel;

        pauseButton.setOnAction(e -> viewModel.toggleSimulationState());
        
        disableTrianaButton.setOnAction(e -> viewModel.toggleTriana());
        
        // Add CSS class for broken
        viewModel.isPausedProperty().addListener(obs -> {
            if (viewModel.isPausedProperty().get()) {
                getStyleClass().add("paused");
            } else {
                getStyleClass().remove("paused");
            }
        });
    }
}

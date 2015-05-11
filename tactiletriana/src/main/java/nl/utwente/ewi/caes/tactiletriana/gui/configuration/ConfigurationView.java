/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.configuration;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario.ScenarioView;

/**
 *
 * @author Richard
 */
public class ConfigurationView extends GridPane {

    @FXML
    private TextField portField;
    @FXML
    private ComboBox touchScreenComboBox;
    @FXML
    private ComboBox detailScreenComboBox;
    @FXML
    private CheckBox fullScreenCheckBox;
    @FXML
    private Button startButton;
    @FXML
    private Button resetButton;
    @FXML
    private StackPane scenarioViewContainer;
    private ScenarioView scenarioView;
    
    private ConfigurationVM viewModel;

    public ConfigurationView() {
        ViewLoader.load(this);
    }

    public void setViewModel(ConfigurationVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel may only be set once");
        }

        this.viewModel = viewModel;

        // Initialize ScenarioView
        scenarioView = new ScenarioView();
        scenarioView.setViewModel(viewModel.getScenarioVM());
        scenarioViewContainer.getChildren().add(scenarioView);
        
        // Bind control properties to VM
        fullScreenCheckBox.selectedProperty().bindBidirectional(viewModel.fullScreenCheckedProperty());
        fullScreenCheckBox.disableProperty().bind(viewModel.fullScreenCheckBoxDisabledProperty());
        touchScreenComboBox.disableProperty().bind(viewModel.screenComboBoxesDisabledProperty());
        touchScreenComboBox.setItems(viewModel.getScreenIndexList());
        touchScreenComboBox.valueProperty().bindBidirectional(viewModel.touchScreenSelectionProperty());
        detailScreenComboBox.disableProperty().bind(viewModel.screenComboBoxesDisabledProperty());
        detailScreenComboBox.setItems(viewModel.getScreenIndexList());
        detailScreenComboBox.valueProperty().bindBidirectional(viewModel.detailScreenSelectionProperty());
        portField.textProperty().bindBidirectional(viewModel.portFieldTextProperty());
        startButton.disableProperty().bind(viewModel.startButtonDisabledProperty());
        startButton.textProperty().bind(viewModel.startButtonTextProperty());
        resetButton.disableProperty().bind(viewModel.resetButtonDisabledProperty());
        scenarioView.disableProperty().bind(viewModel.scenarioViewDisableProperty());
        
        // Event handling
        startButton.setOnAction(e -> viewModel.start());
        resetButton.setOnAction(e -> viewModel.reset());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.launcher;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class LauncherView extends GridPane {
    @FXML private TextField portField;
    @FXML private ComboBox touchScreenComboBox;
    @FXML private ComboBox detailScreenComboBox;
    @FXML private CheckBox fullScreenCheckBox;
    @FXML private Button startButton;
    
    
    
    private LauncherVM viewModel;
    
    public LauncherView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(LauncherVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel may only be set once");
        
        this.viewModel = viewModel;
        
        // Bind control properties to VM
        fullScreenCheckBox.selectedProperty().bindBidirectional(viewModel.fullScreenCheckedProperty());
        touchScreenComboBox.disableProperty().bind(viewModel.screenComboBoxesDisabledProperty());
        touchScreenComboBox.setItems(viewModel.getScreenIndexList());
        touchScreenComboBox.valueProperty().bindBidirectional(viewModel.touchScreenSelectionProperty());
        detailScreenComboBox.disableProperty().bind(viewModel.screenComboBoxesDisabledProperty());
        detailScreenComboBox.setItems(viewModel.getScreenIndexList());
        detailScreenComboBox.valueProperty().bindBidirectional(viewModel.detailScreenSelectionProperty());
        portField.textProperty().bindBidirectional(viewModel.portFieldTextProperty());
        startButton.disableProperty().bind(viewModel.startButtonDisabledProperty());
        
        // Event handling
        startButton.setOnAction(e -> viewModel.start());
    }
}

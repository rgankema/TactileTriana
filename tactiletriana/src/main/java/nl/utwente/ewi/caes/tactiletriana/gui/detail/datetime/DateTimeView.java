/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class DateTimeView extends GridPane {
    @FXML private Label timeLabel;
    @FXML private Label dateLabel;
    
    private DateTimeVM viewModel;
    
    public DateTimeView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(DateTimeVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel can only be set once");
        
        this.viewModel = viewModel;
        
        timeLabel.textProperty().bind(viewModel.timeLabelProperty());
        dateLabel.textProperty().bind(viewModel.dateLabelProperty());
    }
}

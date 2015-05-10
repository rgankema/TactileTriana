/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario.timespan;

import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.HBox;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

public class TimeSpanView extends HBox {
    @FXML private DatePicker startDate;
    @FXML private DatePicker endDate;
    
    private TimeSpanVM viewModel;
    
    public TimeSpanView() {
        ViewLoader.load(this);
    }
    
    public TimeSpanVM getViewModel() {
        return viewModel;
    }
    
    public void setViewModel(TimeSpanVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel may only be set once");
        
        this.viewModel = viewModel;
        
        startDate.valueProperty().bindBidirectional(viewModel.startDateProperty());
        endDate.valueProperty().bindBidirectional(viewModel.endDateProperty());
        
        // Only enable days that are allowed
        startDate.setDayCellFactory(dp -> { 
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item.isBefore(viewModel.minStartDateProperty().get()) ||
                            item.isAfter(viewModel.maxStartDateProperty().get())) {
                        setDisable(true);
                    }
                }
            };
        });
        
        endDate.setDayCellFactory(dp -> { 
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item.isBefore(viewModel.minEndDateProperty().get()) ||
                            item.isAfter(viewModel.maxEndDateProperty().get())) {
                        setDisable(true);
                    }
                }
            };
        });
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime.DateTimeView;

/**
 *
 * @author Richard
 */
public class DetailView extends GridPane {
    @FXML private DateTimeView dateTimeView;
    
    private DetailVM viewModel;
    
    public DetailView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(DetailVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel can only be set once");
        
        this.viewModel = viewModel;
        
        dateTimeView.setViewModel(viewModel.getDateTimeVM());
    }
}

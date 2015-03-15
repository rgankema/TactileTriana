/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.house;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class HouseView extends Pane {
    @FXML private Rectangle rectangle;
    
    private HouseVM viewModel;
    
    public HouseView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(HouseVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel can only be set once");
        
        this.viewModel = viewModel;
        
        rectangle.strokeProperty().bind(Bindings.createObjectBinding(() -> { 
            if (viewModel.isFuseBlown()) {
                return Color.BLACK;
            }
            
            double load = viewModel.getLoad();
            return Color.DARKGRAY.interpolate(Color.RED, load);
        }, viewModel.loadProperty(), viewModel.fuseBlownProperty()));
    }
}

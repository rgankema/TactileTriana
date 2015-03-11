/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class CableView extends Pane{
    @FXML private Line line;
    
    private CableVM viewModel;
    
    public CableView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CableView.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load CableView.fxml", e);
        }
    }
    
    public void setViewModel(CableVM viewModel) {
        line.strokeProperty().unbind();
        
        this.viewModel = viewModel;
        // Bind load and broken in viewmodel to color in view
        line.strokeProperty().bind(Bindings.createObjectBinding(() -> {
            if (viewModel.isBroken()) {
                return Color.BLACK;
            }
            
            double load = viewModel.getLoad();
            return new Color(load, 1.0 - load, 0, 1.0);
        }, viewModel.loadProperty(), viewModel.brokenProperty()));
    }
}

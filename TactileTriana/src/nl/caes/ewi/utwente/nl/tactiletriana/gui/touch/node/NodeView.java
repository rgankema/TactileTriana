/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class NodeView extends Rectangle {
    
    private NodeVM viewModel;
    
    public NodeView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NodeView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load NodeView.fxml", e);
        }
    }
    
    public void setViewModel(NodeVM viewModel) {
        fillProperty().unbind();
        
        this.viewModel = viewModel;
        
        // Bind voltage error to color in view
        fillProperty().bind(Bindings.createObjectBinding(() -> {
            double error = viewModel.getVoltageError();
            return new Color(error, 1.0 - error, 0, 1.0);
        }, viewModel.voltageErrorProperty()));
    }
    
}

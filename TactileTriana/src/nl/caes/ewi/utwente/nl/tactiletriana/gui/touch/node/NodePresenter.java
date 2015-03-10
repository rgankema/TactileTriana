/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class NodePresenter {
    @FXML private Shape root;
    
    private NodeVM viewModel;
    
    public Shape getView() {
        return root;
    }
    
    protected void setViewModel(NodeVM viewModel) {
        this.viewModel = viewModel;
        
        // Bind voltage error to color in view
        root.fillProperty().bind(Bindings.createObjectBinding(() -> {
            double error = viewModel.getVoltageError();
            return new Color(error, 1.0 - error, 0, 1.0);
        }, viewModel.voltageErrorProperty()));
    }
    
}

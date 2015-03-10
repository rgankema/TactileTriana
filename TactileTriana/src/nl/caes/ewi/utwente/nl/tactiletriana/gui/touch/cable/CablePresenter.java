/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class CablePresenter {
    @FXML private Pane root;
    @FXML private Line line;
    @FXML private Pane directionOverlay;
    
    private CableVM viewModel;
    
    public Node getView() {
        return root;
    }
    
    protected void setViewModel(CableVM viewModel) {
        this.viewModel = viewModel;
        
        // Bind load to color in view
        line.strokeProperty().bind(Bindings.createObjectBinding(() -> {
            double error = viewModel.getLoad();
            return new Color(error, 1.0 - error, 0, 1.0);
        }, viewModel.loadProperty()));
    }
}

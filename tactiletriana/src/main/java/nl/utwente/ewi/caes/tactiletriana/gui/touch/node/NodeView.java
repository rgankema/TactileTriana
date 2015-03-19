/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.node;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.utwente.ewi.caes.tactiletriana.App;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class NodeView extends StackPane {
    @FXML private Rectangle rectangle;
    
    private NodeVM viewModel;
    
    public NodeView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(NodeVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel already set");
        
        this.viewModel = viewModel;
        
        // Bind voltage error to color in view
        rectangle.fillProperty().bind(Bindings.createObjectBinding(() -> {
            double error = viewModel.getVoltageError();
            return new Color(error, 1.0 - error, 0, 1.0);
        }, viewModel.voltageErrorProperty()));
        
        if (App.DEBUG) {
            Label label = new Label();
            label.textProperty().bind(viewModel.debugStringProperty());
            getChildren().add(label);
        }
    }
    
}

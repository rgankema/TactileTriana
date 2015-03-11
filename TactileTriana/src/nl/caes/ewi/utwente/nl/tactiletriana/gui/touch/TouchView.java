/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.cs.caes.tactile.control.TactilePane;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class TouchView extends TactilePane {
    
    private TouchVM viewModel;
    
    public TouchView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TouchView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load TouchView.fxml", e);
        }
    }
    
    public void setViewModel(TouchVM viewModel) {
        this.viewModel = viewModel;
    }
    
}

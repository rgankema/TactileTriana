/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.house;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 *
 * @author Richard
 */
public class HouseView extends Pane {
    private HouseVM viewModel;
    
    public HouseView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/HouseView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load HouseView.fxml", e);
        }
    }
    
    public void setViewModel(HouseVM viewModel) {
        this.viewModel = viewModel;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.house;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class HouseView extends Pane {
    private HouseVM viewModel;
    
    public HouseView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(HouseVM viewModel) {
        this.viewModel = viewModel;
    }
}

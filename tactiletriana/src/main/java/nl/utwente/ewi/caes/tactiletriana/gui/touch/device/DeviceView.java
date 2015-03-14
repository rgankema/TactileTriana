/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class DeviceView extends StackPane {
    @FXML private Node configIcon;
    
    private DeviceVM viewModel;
    
    public DeviceView() {
        ViewLoader.load(this);
    }
    
    public DeviceVM getViewModel() {
        return this.viewModel;
    }
    
    public void setViewModel(DeviceVM viewModel) {
        configIcon.visibleProperty().unbind();
        
        this.viewModel = viewModel;
        
        configIcon.visibleProperty().bind(viewModel.configIconShownProperty());
    }
}

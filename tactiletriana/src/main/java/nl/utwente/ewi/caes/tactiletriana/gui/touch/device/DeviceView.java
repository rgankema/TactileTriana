/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM.State;

/**
 *
 * @author Richard
 */
public class DeviceView extends StackPane {
    @FXML private Node configIcon;
    @FXML private Rectangle rectangle;
    
    private DeviceVM viewModel;
    
    public DeviceView() {
        ViewLoader.load(this);
    }
    
    public DeviceVM getViewModel() {
        return this.viewModel;
    }
    
    public void setViewModel(DeviceVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel already set");
        
        this.viewModel = viewModel;
        
        configIcon.visibleProperty().bind(viewModel.configIconShownProperty());
        rectangle.strokeProperty().bind(Bindings.createObjectBinding(() -> { 
            Color color = null;
            if (viewModel.getState() == State.CONSUMING) {
                color = Color.DARKGREY.interpolate(Color.RED, viewModel.getLoad());
            } else if (viewModel.getState() == State.PRODUCING) {
                color = Color.DARKGREY.interpolate(Color.GREEN, viewModel.getLoad());
            }
            return color; 
        }, viewModel.loadProperty(), viewModel.stateProperty()));
    }
}

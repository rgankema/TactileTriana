/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.device.DeviceView;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.house.HouseView;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.network.NetworkView;
import nl.utwente.cs.caes.tactile.control.TactilePane;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class TouchView extends TactilePane {
    @FXML private NetworkView networkView;
    
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
        
        // Track houses
        for (HouseView house : networkView.getHouseViews()) {
            getActiveNodes().add(house);
        }
        
        addDeviceToStack();
    }
    
    private void addDeviceToStack() {
        double x = 1920/2 - 25;
        double y = 1080/2 - 25;
        
        DeviceView device = new DeviceView();
        device.relocate(x, y);
        
        // Add device to pane, in background
        getChildren().add(1, device);
        // Track device
        getActiveNodes().add(device);
        
        // Make device rotate
        device.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
            double rotate = device.getLayoutY() - y;
            if (rotate < -90) rotate = -90.0;
            if (rotate > 90) rotate = 90.0;
            return 90.0 - rotate;
        }, device.layoutYProperty()));
        
        // Add new device when drag starts, remove device if not on house
        TactilePane.inUseProperty(device).addListener(obs -> {
            if (TactilePane.isInUse(device)) {
                addDeviceToStack();
            } else {
                if (!TactilePane.getNodesColliding(device).stream().anyMatch(node -> node instanceof HouseView)) {
                    getChildren().remove(device);
                }
            }
        });
    }
    
    public void setViewModel(TouchVM viewModel) {
        this.viewModel = viewModel;
    }
    
}

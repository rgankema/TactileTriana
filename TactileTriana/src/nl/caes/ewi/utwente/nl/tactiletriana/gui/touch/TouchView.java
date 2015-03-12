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
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.device.DeviceView;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.cs.caes.tactile.control.TactilePane;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class TouchView extends TactilePane {
    
    private TouchVM viewModel;
    private List<DeviceView> devices;
    
    public TouchView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("TouchView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load TouchView.fxml", e);
        }
        
        // Create a stack of (dummy) devices
        devices = new ArrayList<>();
        double x = 1920/2 - 25;
        double y = 1080/2 - 25;
        for (int i = 0; i < 6; i++) {
            DeviceView device = new DeviceView();
            device.relocate(x, y);
            
            devices.add(device);
            getChildren().add(device);
        }
        
        for (Node node : getChildren()) {
            if (node instanceof DeviceView) {
                node.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
                    double rotate = node.getLayoutY() - y;
                    if (rotate < -90) rotate = -90.0;
                    if (rotate > 90) rotate = 90.0;
                    return 90.0 - rotate;
                }, node.layoutYProperty()));
            }
        }
    }
    
    public void setViewModel(TouchVM viewModel) {
        this.viewModel = viewModel;
    }
    
}

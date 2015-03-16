/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Group;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.network.NetworkView;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.MockDevice;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class TouchView extends TactilePane {
    @FXML private NetworkView networkView;
    
    private TouchVM viewModel;
    
    public TouchView() {
        ViewLoader.load(this);
        
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
        device.setViewModel(new DeviceVM(new MockDevice()));
        
        // Add device to group to fix drag bug
        Group group = new Group(device);
        group.relocate(x, y);
        
        // Add device to pane, in background
        getChildren().add(1, group);
        // Track device
        getActiveNodes().add(group);
        
        // Make device rotate       TODO: misschien moet dit in DeviceVM gebeuren?
        device.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
            double rotate = group.getLayoutY() - y;
            if (rotate < -90) rotate = -90.0;
            if (rotate > 90) rotate = 90.0;
            return 90.0 - rotate;
        }, group.layoutYProperty()));
        
        // Add new device when drag starts, remove device if not on house
        TactilePane.inUseProperty(group).addListener(obs -> {
            if (TactilePane.isInUse(group)) {
                addDeviceToStack();
            } else {
                if (!TactilePane.getNodesColliding(group).stream().anyMatch(node -> node instanceof HouseView)) {
                    getChildren().remove(group);
                } else {
                    device.getViewModel().setConfigIconShown(true); // TODO: TIJDELIJK!!! dit moet uiteindelijk uiteraard automatisch gaan in de VM
                }
            }
        });
    }
    
    public void setViewModel(TouchVM viewModel) {
        this.viewModel = viewModel;
    }
    
}

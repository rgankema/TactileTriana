/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Polygon;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.network.NetworkView;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.*;

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
        for (HouseView house : networkView.getHouses()) {
            getActiveNodes().add(house);
        }
        
        DeviceView device = new DeviceView(new Polygon(new double[] { 0d, 50d, 25d, 0d, 50d, 50d }));
        DeviceVM deviceVM = new DeviceVM(new MockDevice());
        
        DeviceView solar = new DeviceView(new Polygon(new double[] { 0d, 50d, 40d, 0d, 40d, 50d }));
        DeviceVM solarVM = new DeviceVM(new SolarPanel());
        
        addDeviceToStack((1920/2 - 40),(1080/2 - 25),device,deviceVM);
        addDeviceToStack((1920/2 + 40),(1080/2 - 25),solar,solarVM);
    }
    
    public void setViewModel(TouchVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel already set");
        
        this.viewModel = viewModel;
        for (int i = 0; i < 6; i++) {
            networkView.getInternalNodes()[i].setViewModel(viewModel.getInternalNodes()[i]);
            networkView.getInternalCables()[i].setViewModel(viewModel.getInternalCables()[i]);
            networkView.getHouseNodes()[i].setViewModel(viewModel.getHouseNodes()[i]);
            networkView.getHouseCables()[i].setViewModel(viewModel.getHouseCables()[i]);
            networkView.getHouses()[i].setViewModel(viewModel.getHouses()[i]);
        }
    }
    
    
    
    private void addDeviceToStack(int x,int y,DeviceView device,DeviceVM deviceVM) {
               
        device.setViewModel(deviceVM);
        
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
                if (deviceVM.getModel() instanceof MockDevice){
                    DeviceView device2 = new DeviceView(new Polygon(new double[] { 0d, 50d, 25d, 0d, 50d, 50d }));
                    DeviceVM deviceVM2 = new DeviceVM(new MockDevice());
                    addDeviceToStack(x,y,device2,deviceVM2);
                } else if (deviceVM.getModel() instanceof SolarPanel){
                    DeviceView solar = new DeviceView(new Polygon(new double[] { 0d, 50d, 40d, 0d, 40d, 50d }));
                    DeviceVM solarVM = new DeviceVM(new SolarPanel());
                    addDeviceToStack(x,y,solar,solarVM);
                }
            } else {
                if (!TactilePane.getNodesColliding(group).stream().anyMatch(node -> node instanceof HouseView)) {
                    getChildren().remove(group);
                    deviceVM.droppedOnHouse(null);
                } else {
                    for (Node node : TactilePane.getNodesColliding(group)) {
                        if (node instanceof HouseView) {
                            deviceVM.droppedOnHouse(((HouseView) node).getViewModel());
                            break;
                        }
                    }
                }
            }
        });
    }
}

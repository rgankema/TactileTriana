/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import nl.utwente.ewi.caes.tactilefx.control.Anchor;
import nl.utwente.ewi.caes.tactilefx.control.TactilePane;
import nl.utwente.ewi.caes.tactilefx.debug.MouseToTouchMapper;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.network.NetworkView;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.BufferTimeShiftable;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.MockDevice;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.SolarPanel;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class TouchView extends TactilePane {

    @FXML
    private NetworkView networkView;

    private TouchVM viewModel;
    
    public TouchView() {
        ViewLoader.load(this);

        addEventFilter(MouseEvent.ANY, new MouseToTouchMapper());
        
        // Track houses
        for (HouseView house : networkView.getHouses()) {
            getActiveNodes().add(house);
        }
    }

    public void setViewModel(TouchVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel already set");
        }

        this.viewModel = viewModel;
        networkView.getTransformer().setViewModel(viewModel.getTransformer());
        for (int i = 0; i < 6; i++) {
            networkView.getInternalNodes()[i].setViewModel(viewModel.getInternalNodes()[i]);
            networkView.getInternalCables()[i].setViewModel(viewModel.getInternalCables()[i]);
            networkView.getHouseNodes()[i].setViewModel(viewModel.getHouseNodes()[i]);
            networkView.getHouseCables()[i].setViewModel(viewModel.getHouseCables()[i]);
            networkView.getHouses()[i].setViewModel(viewModel.getHouses()[i]);
        }
        
        DeviceView mv = new DeviceView(MockDevice.class);
        mv.setViewModel(viewModel.getMockVM());
        DeviceView cv = new DeviceView(BufferTimeShiftable.class);
        cv.setViewModel(viewModel.getCarVM());
        DeviceView sv = new DeviceView(SolarPanel.class);
        sv.setViewModel(viewModel.getSolarPanelVM());
        
        pushDeviceStack(mv, -100);
        pushDeviceStack(cv, 0);
        pushDeviceStack(sv, 100);
    }
    
    private void pushDeviceStack(DeviceView device, double xOffset) {
        // Add device to group to fix drag bug
        Group group = new Group(device);
        // Add device to pane, in background
        getChildren().add(1, group);
        // Track device
        getActiveNodes().add(group);

        TactilePane.setAnchor(group, new Anchor(this, xOffset, 0, Pos.CENTER, false));
        
        // Rotate device
        device.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
            double rotate = -getHeight() / 2 + device.getBoundsInLocal().getHeight() / 2 + group.getLayoutY();
            if (rotate < -90) {
                rotate = -90.0;
            }
            if (rotate > 90) {
                rotate = 90.0;
            }
            return 90.0 - rotate;
        }, group.layoutYProperty(), heightProperty()));

        // Add new device when drag starts, remove device if not on house
        TactilePane.inUseProperty(group).addListener(obs -> {
            if (TactilePane.isInUse(group)) {
                DeviceView newDevice = new DeviceView(device.getType());
                newDevice.setViewModel(viewModel.getDeviceVM(device.getViewModel().getModel().getClass()));
                pushDeviceStack(newDevice, xOffset);
            } else {
                if (!TactilePane.getNodesColliding(group).stream().anyMatch(node -> node instanceof HouseView)) {
                    getChildren().remove(group);
                    getActiveNodes().remove(group);
                    device.getViewModel().droppedOnHouse(null);
                } else {
                    for (Node node : TactilePane.getNodesColliding(group)) {
                        if (node instanceof HouseView) {
                            device.getViewModel().droppedOnHouse(((HouseView) node).getViewModel());
                            break;
                        }
                    }
                }
            }
        });
    }
}

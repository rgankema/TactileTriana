/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import nl.utwente.ewi.caes.tactilefx.control.Anchor;
import nl.utwente.ewi.caes.tactilefx.control.TactilePane;
import nl.utwente.ewi.caes.tactilefx.debug.MouseToTouchMapper;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.customcontrols.FloatPane;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer.TransformerView;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.BufferTimeShiftable;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.MockDevice;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.SolarPanel;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class TouchView extends TactilePane {

    TransformerView tv;
    HouseView[] hv;
    NodeView[] nvh;
    NodeView[] nvi;
    CableView[] cvh;
    CableView[] cvi;
    
    
    private TouchVM viewModel;
    private FloatPane networkOverlay;
    
    public TouchView() {
        ViewLoader.load(this);

        addEventFilter(MouseEvent.ANY, new MouseToTouchMapper());
        
        networkOverlay = new FloatPane();
        networkOverlay.prefWidthProperty().bind(this.widthProperty());
        networkOverlay.prefHeightProperty().bind(this.heightProperty());
        
        tv = new TransformerView();
        hv = new HouseView[6];
        nvh = new NodeView[6];
        nvi = new NodeView[6];
        cvh = new CableView[6];
        cvi = new CableView[6];
        
        for (int i = 0; i < 6; i++) {
            hv[i] = new HouseView();
            getActiveNodes().add(hv[i]);
            nvh[i] = new NodeView();
            nvi[i] = new NodeView();
            cvh[i] = new CableView();
            cvh[i].setManaged(false);
            cvi[i] = new CableView();
            cvi[i].setManaged(false);
            
            // Arrange internal cables
            if (i == 0) {
                cvi[i].setStartNode(tv);
                cvi[i].setEndNode(nvi[i]);
            } else {
                cvi[i].setStartNode(nvi[i - 1]);
                cvi[i].setEndNode(nvi[i]);
            }
            
            // Arrange house cables
            cvh[i].setStartNode(nvi[i]);
            cvh[i].setEndNode(nvh[i]);
            
            // Setup FloatPane parameters
            Insets margin = new Insets(10);
            Pos pos = null;
            switch (i) {
                case 0:
                    pos = Pos.TOP_LEFT;
                    break;
                case 1:
                    pos = Pos.TOP_CENTER;
                    break;
                case 2:
                    pos = Pos.TOP_RIGHT;
                    break;
                case 3:
                    pos = Pos.BOTTOM_RIGHT;
                    break;
                case 4:
                    pos = Pos.BOTTOM_CENTER;
                    break;
                case 5:
                    pos = Pos.BOTTOM_LEFT;
                    break;
            }
            
            FloatPane.setAlignment(hv[i], pos);
            FloatPane.setMargin(hv[i], margin);
            
            FloatPane.setAlignment(nvh[i], pos);
            FloatPane.setMargin(nvh[i], new Insets(195, 225, 195, 225));
            
            FloatPane.setAlignment(nvi[i], pos);
            FloatPane.setMargin(nvi[i], new Insets(325, 225, 325, 225));
            
            networkOverlay.getChildren().addAll(cvi[i], cvh[i], hv[i], nvh[i], nvi[i]);
        }
        FloatPane.setAlignment(tv, Pos.CENTER_LEFT);
        FloatPane.setMargin(tv, new Insets(200));
        networkOverlay.getChildren().add(tv);
        
        getChildren().add(networkOverlay);
        
        List<Node> toBackground = new ArrayList<>();
        for (Node node : networkOverlay.getChildren()) {
            if (node instanceof CableView) {
                toBackground.add(node);
            }
        }
        for (Node node : toBackground) {
            node.toBack();
        }
        for (Node node : getChildren()) {
            setDraggable(node, false);
        }
    }

    public void setViewModel(TouchVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel already set");
        }

        this.viewModel = viewModel;
        
        tv.setViewModel(viewModel.getTransformer());
        for (int i = 0; i < 6; i++) {
            hv[i].setViewModel(viewModel.getHouses()[i]);
            cvi[i].setViewModel(viewModel.getInternalCables()[i]);
            cvh[i].setViewModel(viewModel.getHouseCables()[i]);
            nvi[i].setViewModel(viewModel.getInternalNodes()[i]);
            nvh[i].setViewModel(viewModel.getInternalNodes()[i]);
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
        // Animate new device
        FadeTransition ft = new FadeTransition(Duration.millis(500), group);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.playFromStart();
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
            if (TactilePane.isInUse(group) && device.getViewModel().getState() == DeviceVM.State.DISCONNECTED) {
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

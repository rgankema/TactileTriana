/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import nl.utwente.ewi.caes.tactilefx.control.Anchor;
import nl.utwente.ewi.caes.tactilefx.control.Bond;
import nl.utwente.ewi.caes.tactilefx.control.TactilePane;
import nl.utwente.ewi.caes.tactilefx.debug.MouseToTouchMapper;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.customcontrols.FloatPane;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.background.BackgroundView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.control.ControlView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer.TransformerView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.trash.TrashView;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.*;

/**
 * The root view for the touch screen.
 * 
 * CSS class: touch-view
 *
 * @author Richard
 */
public class TouchView extends TactilePane {
    private final static Image WARNING_ICON = new Image("images/warning-icon.png", 50, 50, true, true);
    
    private TouchVM viewModel;
    
    @FXML private BackgroundView backgroundView;
    @FXML private TrashView trashView;
    @FXML private FloatPane networkOverlay;
    @FXML private ControlView controlView;
    
    private TransformerView tv;
    private HouseView[] hv;
    private NodeView[] nvh;
    private NodeView[] nvi;
    private CableView[] cvh;
    private CableView[] cvi;
    
    
    public TouchView() {
        ViewLoader.load(this);

        addEventFilter(MouseEvent.ANY, new MouseToTouchMapper());
        
        // Setup background
        backgroundView.prefWidthProperty().bind(this.widthProperty());
        backgroundView.prefHeightProperty().bind(this.heightProperty());
        
        // Setup trash view
        TactilePane.setAnchor(trashView, new Anchor(this, 500, 0, Pos.CENTER, false));
        getActiveNodes().add(trashView.getActiveZone());
      
        // Setup network overlay
        networkOverlay.prefWidthProperty().bind(this.widthProperty());
        networkOverlay.prefHeightProperty().bind(this.heightProperty());
        
        // Usually we would do layout in FXML, but in this case it's far easier
        // to do it in code
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
        
        List<Node> toBackground = new ArrayList<>();
        for (Node node : networkOverlay.getChildren()) {
            if (node instanceof CableView) {
                toBackground.add(node);
            }
        }
        for (Node node : toBackground) {
            node.toBack();
        }
    }

    public void setViewModel(TouchVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel already set");
        
        this.viewModel = viewModel;
        
        // Set view models of children
        controlView.setViewModel(viewModel.getControlVM());
        backgroundView.setViewModel(viewModel.getBackgroundVM());
        
        tv.setViewModel(viewModel.getTransformer());
        for (int i = 0; i < 6; i++) {
            hv[i].setViewModel(viewModel.getHouses()[i]);
            cvi[i].setViewModel(viewModel.getInternalCables()[i]);
            cvh[i].setViewModel(viewModel.getHouseCables()[i]);
            nvi[i].setViewModel(viewModel.getInternalNodes()[i]);
            nvh[i].setViewModel(viewModel.getHouseNodes()[i]);
            
            // Anchor warning icons to houses and cables
            addWarningIcon(hv[i], viewModel.getHouses()[i].fuseBlownProperty());
            addWarningIcon(cvi[i], viewModel.getInternalCables()[i].brokenProperty());
            addWarningIcon(cvh[i], viewModel.getHouseCables()[i].brokenProperty());
        }
        
        // Populate the device stacks
        for (DeviceVM device : viewModel.getDevices()) {
            pushDeviceStack(device);
        }
        viewModel.getDevices().addListener((ListChangeListener.Change<? extends DeviceVM> c) -> {
            while (c.next()) {
                for (DeviceVM device : c.getAddedSubList()) {
                    pushDeviceStack(device);
                }
                for (DeviceVM device : c.getRemoved()) {
                    for (Iterator<Node> it = getChildren().iterator(); it.hasNext();) {
                        Node node = it.next();
                        if (node instanceof DeviceView) {
                            DeviceView dv = (DeviceView) node;
                            if (dv.getViewModel() == device) {
                                it.remove();
                                break;
                            }
                        }
                    }
                }
            }
        });
    }
    
    // HELP METHODS
    
    // Creates a new DeviceView for a DeviceVM and places it on the appropriate stack
    private void pushDeviceStack(DeviceVM deviceVM) {
        // Get xOffset based on type of device
        int xOffset = 0;
        Class type = deviceVM.getModelClass();
        if (type == Buffer.class) {
            xOffset = -300;
        } else if (type == ElectricVehicle.class) {
            xOffset = -200;
        } else if (type == SolarPanel.class) {
            xOffset = -100;
        } else if (type == DishWasher.class) {
            xOffset = 0;
        } else if (type == WashingMachine.class) {
            xOffset = 100;
        } else if (type == BufferConverter.class) {
            xOffset = 200;
        } else if (type == TrianaHouseController.class) {
            xOffset = 300;
        }
        
        // Build new device view
        DeviceView device = new DeviceView(type);
        device.setViewModel(deviceVM);
        TactilePane.setAnchor(device, new Anchor(this, xOffset, 0, Pos.CENTER, false));
        TactilePane.setSlideOnRelease(device, true);
        rotateNode(device, false);
        // Fade in new device
        FadeTransition ft = new FadeTransition(Duration.millis(500), device);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.playFromStart();
        // Add device to pane, in background
        getChildren().add(3, device);
        // Track device for collisions
        getActiveNodes().add(device);
       
        // Animation that moves device to trash bin if it's been unused for too long
        final PauseTransition pause = new PauseTransition(Duration.millis(3500));
        pause.setOnFinished(e -> { 
            TactilePane.getBonds(device).add(new Bond(trashView, 0, 0.2));
        });
        
        // If device is not in use, and is not in a house, start a timer to remove
        // the device from the screen. If it is in use again, reset that timer.
        TactilePane.inUseProperty(device).addListener(obs -> {
            if (TactilePane.isInUse(device)) {
                pause.stop();
                TactilePane.getBonds(device).clear();
            } else {
                TactilePane.vectorProperty(device).addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        if (TactilePane.getVector(device).equals(Point2D.ZERO)) {
                            if (!TactilePane.getNodesColliding(device).stream().anyMatch(node -> node instanceof HouseView)) {
                                pause.playFromStart();
                            }
                            TactilePane.vectorProperty(device).removeListener(this);
                        }
                    }
                });
            }
        });
        
        // When the device is dropped on the trash bin, remove it. If it's dropped
        // on a house, connect it.
        TactilePane.setOnInArea(device, e -> {
            if (e.getOther() == trashView.getActiveZone() && !TactilePane.isInUse(device)) {
                getChildren().remove(device);
                StageController.getInstance().removeFromChart(device.getViewModel());
            } else if (e.getOther() instanceof HouseView) {
                if (!TactilePane.isInUse(device)) {
                    device.getViewModel().droppedOnHouse(((HouseView) e.getOther()).getViewModel());
                }
            }
        });
        
        // When the device leaves the area of a house, disconnect it
        TactilePane.setOnAreaLeft(device, e -> { 
            if (e.getOther() instanceof HouseView) {
                device.getViewModel().droppedOnHouse(null);
            }
        });
        
        // Relocate device if it gets out of the TouchView's bounds
        device.boundsInParentProperty().addListener(obs -> { 
            double deviceMaxX = device.getBoundsInParent().getMaxX();
            double deviceMaxY = device.getBoundsInParent().getMaxY();
            double deltaX = Math.max(0, deviceMaxX - getWidth());
            double deltaY = Math.max(0, deviceMaxY - getHeight());
            device.setLayoutX(device.getLayoutX() - deltaX);
            device.setLayoutY(device.getLayoutY() - deltaY);
        });
    }
    
    // Adds a warning icon to a Node that shows when the showWarningBinding is true
    private void addWarningIcon(Node node, ObservableBooleanValue showWarningBinding) {
        ImageView warning = new ImageView(WARNING_ICON);
        TactilePane.setDraggable(warning, false);
        TactilePane.setAnchor(warning, new Anchor(node, 0, 0, Pos.CENTER, true));
        warning.setDisable(true);
        warning.visibleProperty().bind(showWarningBinding);
        getChildren().add(warning);
        rotateNode(warning, true);
    }
    
    // Adds a listener to the node that will make it rotate towards the edge of the screen that its closest to
    private void rotateNode(Node node, boolean snapTo90Degrees) {
        node.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
            double rotate = -getHeight() / 2 + node.getBoundsInLocal().getHeight() / 2 + node.getLayoutY() + node.getTranslateY();
            if (rotate < -90) {
                rotate = -90.0;
            }
            if (rotate > 90) {
                rotate = 90.0;
            }
            if (snapTo90Degrees) {
                if (rotate > 45) {
                    rotate = 90;
                } else if (rotate < -45) {
                    rotate = -90;
                } else {
                    rotate = 0;
                }
            }
            return 90.0 - rotate;
        }, node.layoutYProperty(), node.translateYProperty(), heightProperty()));
    }
}

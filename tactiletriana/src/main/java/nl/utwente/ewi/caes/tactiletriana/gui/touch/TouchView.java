/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import nl.utwente.ewi.caes.tactilefx.control.Anchor;
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

    TransformerView tv;
    HouseView[] hv;
    NodeView[] nvh;
    NodeView[] nvi;
    CableView[] cvh;
    CableView[] cvi;
    
    private TouchVM viewModel;
    
    @FXML private BackgroundView backgroundView;
    @FXML private TrashView trashView;
    @FXML private ControlView controlView;
    
    private FloatPane networkOverlay;
    
    private final Image WARNING_ICON = new Image("images/warning-icon.png", 50, 50, true, true);
    
    public TouchView() {
        ViewLoader.load(this);

        addEventFilter(MouseEvent.ANY, new MouseToTouchMapper());
        
        setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        
        networkOverlay = new FloatPane();
        networkOverlay.prefWidthProperty().bind(this.widthProperty());
        networkOverlay.prefHeightProperty().bind(this.heightProperty());
        
        backgroundView.prefWidthProperty().bind(this.widthProperty());
        backgroundView.prefHeightProperty().bind(this.heightProperty());
        
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
        
        TactilePane.setAnchor(trashView, new Anchor(this, 500, 0, Pos.CENTER, false));
        getActiveNodes().add(trashView.getActiveZone());
        
        TactilePane.setAnchor(controlView, new Anchor(this, 50, 0, Pos.CENTER_LEFT, false));
        controlView.toFront();
    }

    public void setViewModel(TouchVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel already set");
        }

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
            
            ImageView warning = new ImageView(WARNING_ICON);
            TactilePane.setDraggable(warning, false);
            TactilePane.setAnchor(warning, new Anchor(hv[i], Pos.CENTER));
            warning.visibleProperty().bind(viewModel.getHouses()[i].fuseBlownProperty());
            getChildren().add(warning);
            rotateNode(warning);
            
            warning = new ImageView(WARNING_ICON);
            TactilePane.setDraggable(warning, false);
            TactilePane.setAnchor(warning, new Anchor(cvi[i], Pos.CENTER));
            warning.visibleProperty().bind(viewModel.getInternalCables()[i].brokenProperty());
            getChildren().add(warning);
            rotateNode(warning);
            
            warning = new ImageView(WARNING_ICON);
            TactilePane.setDraggable(warning, false);
            TactilePane.setAnchor(warning, new Anchor(cvh[i], Pos.CENTER));
            warning.visibleProperty().bind(viewModel.getHouseCables()[i].brokenProperty());
            getChildren().add(warning);
            rotateNode(warning);
        }
        
        initDeviceStacks();
    }
    
    private void initDeviceStacks() {
        DeviceView cv = new DeviceView(ElectricVehicle.class);
        cv.setViewModel(viewModel.getElectricVehicleVM());
        DeviceView sv = new DeviceView(SolarPanel.class);
        sv.setViewModel(viewModel.getSolarPanelVM());
        DeviceView dv = new DeviceView(DishWasher.class);
        dv.setViewModel(viewModel.getDishWasherVM());
        DeviceView wv = new DeviceView(WashingMachine.class);
        wv.setViewModel(viewModel.getWashingMachineVM());
        DeviceView bv = new DeviceView(Buffer.class);
        bv.setViewModel(viewModel.getBufferVM());
        DeviceView bcv = new DeviceView(BufferConverter.class);
        bcv.setViewModel(viewModel.getBufferConverterVM());
        DeviceView hcv = new DeviceView(TrianaHouseController.class);
        hcv.setViewModel(viewModel.getTrianaHouseControllerVM());
        
        pushDeviceStack(bv, -300);
        pushDeviceStack(cv, -200);
        pushDeviceStack(sv, -100);
        pushDeviceStack(dv, 0);
        pushDeviceStack(wv, 100);
        pushDeviceStack(bcv, 200);
        pushDeviceStack(hcv, 300);
    }
    
    private void pushDeviceStack(DeviceView device, double xOffset) {
        
        // Animate new device
        FadeTransition ft = new FadeTransition(Duration.millis(500), device);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.playFromStart();
        // Add device to pane, in background
        getChildren().add(2, device);
        // Track device
        getActiveNodes().add(device);
       
        TactilePane.setAnchor(device, new Anchor(this, xOffset, 0, Pos.CENTER, false));
        
        // Rotate device
        rotateNode(device);

        final TranslateTransition transition = new TranslateTransition(Duration.millis(1000), device);
        transition.setInterpolator(Interpolator.EASE_IN);
        final PauseTransition pause = new PauseTransition(Duration.millis(3500));
        pause.setOnFinished(e -> { 
            transition.setByX(trashView.getLayoutX() - device.getLayoutX());
            transition.setByY(trashView.getLayoutY() - device.getLayoutY());
            transition.play();
        });
        
        TactilePane.inUseProperty(device).addListener(obs -> {
            if (device.getViewModel().isOnStack()) {
                // When device is being used for the first time, add a new one to the stack
                device.getViewModel().removeFromStack();
                DeviceView newDevice = new DeviceView(device.getViewModel().getModelClass());
                newDevice.setViewModel(viewModel.getDeviceVM(device.getViewModel().getModelClass()));
                pushDeviceStack(newDevice, xOffset);
            } else if (!TactilePane.isInUse(device)) {
                // When device collides with a house, connect it
                if (TactilePane.getNodesColliding(device).stream().anyMatch(node -> node instanceof HouseView)) {
                    for (Node node : TactilePane.getNodesColliding(device)) {
                        if (node instanceof HouseView) {
                            device.getViewModel().droppedOnHouse(((HouseView) node).getViewModel());
                            break;
                        }
                    }
                } else {
                    pause.playFromStart();
                }
            } else {
                pause.stop();
                transition.stop();
            }
        });
        
        // When the device is dropped on the trash bin, remove it
        TactilePane.setOnInArea(device, e -> {
            if (e.getOther() == trashView.getActiveZone() && !TactilePane.isInUse(device)) {
                getChildren().remove(device);
                StageController.getInstance().removeFromChart(device.getViewModel());
            }
        });
        
        // When the device leaves the area of its house, disconnect it
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
    
    /**
     * Removes all devices from the view.
     */
    public void reset() {
        getChildren().removeIf(node -> node instanceof DeviceView);
        initDeviceStacks();
    }
    
    private void rotateNode(Node node) {
        node.rotateProperty().bind(Bindings.createDoubleBinding(() -> {
            double rotate = -getHeight() / 2 + node.getBoundsInLocal().getHeight() / 2 + node.getLayoutY() + node.getTranslateY();
            if (rotate < -90) {
                rotate = -90.0;
            }
            if (rotate > 90) {
                rotate = 90.0;
            }
            return 90.0 - rotate;
        }, node.layoutYProperty(), node.translateYProperty(), heightProperty()));
    }
}

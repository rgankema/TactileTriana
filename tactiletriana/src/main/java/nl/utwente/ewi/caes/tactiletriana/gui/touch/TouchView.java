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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.customcontrols.FloatPane;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.TouchVM.Season;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.control.ControlView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer.TransformerView;
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
    private FloatPane networkOverlay;
    private ControlView controlView;
    private ImageView bgDay;
    private ImageView bgNight;
    
    private final Image BG_SPRING_DAY = new Image("images/background-spring.jpg");
    private final Image BG_SPRING_NIGHT = new Image("images/background-spring-night.jpg");
    private final Image BG_SUMMER_DAY = new Image("images/background-summer.jpg");
    private final Image BG_SUMMER_NIGHT = new Image("images/background-summer-night.jpg");
    private final Image BG_AUTUMN_DAY = new Image("images/background-fall.jpg");
    private final Image BG_AUTUMN_NIGHT = new Image("images/background-fall-night.jpg");
    private final Image BG_WINTER_DAY = new Image("images/background-winter.jpg");
    private final Image BG_WINTER_NIGHT = new Image("images/background-winter-night.jpg");
    
    
    
    public TouchView() {
        ViewLoader.load(this);

        addEventFilter(MouseEvent.ANY, new MouseToTouchMapper());
        
        setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        
        bgDay = new ImageView();
        bgDay.fitWidthProperty().bind(this.widthProperty());
        bgDay.fitHeightProperty().bind(this.heightProperty());
        
        bgNight = new ImageView();
        bgNight.fitWidthProperty().bind(this.widthProperty());
        bgNight.fitHeightProperty().bind(this.heightProperty());
        
        networkOverlay = new FloatPane();
        networkOverlay.prefWidthProperty().bind(this.widthProperty());
        networkOverlay.prefHeightProperty().bind(this.heightProperty());
        
        controlView = new ControlView();
        controlView.setRotate(90);
        FloatPane.setAlignment(controlView, Pos.CENTER_LEFT);
        FloatPane.setMargin(controlView, new Insets(25));
        networkOverlay.getChildren().add(controlView);
        
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
        
        getChildren().addAll(bgNight, bgDay, networkOverlay);
        
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
            nvh[i].setViewModel(viewModel.getHouseNodes()[i]);
        }
        
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
        
        pushDeviceStack(bv, -250);
        pushDeviceStack(cv, -150);
        pushDeviceStack(sv, -50);
        pushDeviceStack(dv, 50);
        pushDeviceStack(wv, 150);
        pushDeviceStack(bcv, 250);
        
        
        controlView.setViewModel(viewModel.getControlVM());
        
        bgDay.opacityProperty().bind(viewModel.darknessFactorProperty().negate().add(1));
        bgNight.opacityProperty().bind(viewModel.darknessFactorProperty());
        
        viewModel.seasonProperty().addListener(obs -> { 
            // Temporarily replace day background with old night background
            ImageView temp = new ImageView(bgNight.getImage());
            temp.fitWidthProperty().bind(this.widthProperty());
            temp.fitHeightProperty().bind(this.heightProperty());
            getChildren().set(1, temp);
            
            // Fade out old night background
            FadeTransition fade = new FadeTransition(Duration.millis(SimulationConfig.SYSTEM_TICK_TIME * (120 / SimulationConfig.TICK_MINUTES)), temp);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> { 
                getChildren().set(1, bgDay);
            });
            fade.playFromStart();
            
            // Find the proper image
            Season season = viewModel.getSeason();
            Image day = null, night = null;
            if (season == Season.SPRING) {
                day = BG_SPRING_DAY;
                night = BG_SPRING_NIGHT;
            } else if (season == Season.SUMMER) {
                day = BG_SUMMER_DAY;
                night = BG_SUMMER_NIGHT;
            } else if (season == Season.AUTUMN) {
                day = BG_AUTUMN_DAY;
                night = BG_AUTUMN_NIGHT;
            } else if (season == Season.WINTER) {
                day = BG_WINTER_DAY;
                night = BG_WINTER_NIGHT;
            }
            bgDay.setImage(day);
            bgNight.setImage(night);
        });
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
        getChildren().add(3, group);
        // Track device
        getActiveNodes().add(group);
       
        TactilePane.setAnchor(group, new Anchor(this, xOffset, 0, Pos.CENTER, false));
        //group.setLayoutX(1920 / 2 - 30 + xOffset);
        //group.setLayoutY(1080 / 2 - 30);
        
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
                DeviceView newDevice = new DeviceView(device.getViewModel().getModelClass());
                newDevice.setViewModel(viewModel.getDeviceVM(device.getViewModel().getModelClass()));
                pushDeviceStack(newDevice, xOffset);
            } else {
                if (!TactilePane.getNodesColliding(group).stream().anyMatch(node -> node instanceof HouseView)) {
                    getChildren().remove(group);
                    getActiveNodes().remove(group);
                    StageController.getInstance().removeFromChart(device.getViewModel());
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
        
        // Relocate device if it gets out of the TouchView's bounds
        group.boundsInParentProperty().addListener(obs -> { 
            double deviceMaxX = group.getBoundsInParent().getMaxX();
            double deltaX = Math.max(0, deviceMaxX - getWidth());
            double deviceMaxY = group.getBoundsInParent().getMaxY();
            double deltaY = Math.max(0, deviceMaxY - getHeight());
            group.setLayoutX(group.getLayoutX() - deltaX);
            group.setLayoutY(group.getLayoutY() - deltaY);
        });
    }
}

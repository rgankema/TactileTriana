/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.events.EventUtil;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM.State;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.*;

/**
 * The view for a single device.
 * 
 * CSS class: device-view
 * 
 * @author Richard
 */
public class DeviceView extends StackPane {
    @FXML private Node configIcon;
    @FXML private Label batteryLabel;
    @FXML private ImageView deviceIcon;
    private DeviceConfigView configPanel;

    private DeviceVM viewModel;
    private final Class<? extends DeviceBase> type;
    
    public DeviceView(Class<? extends DeviceBase> type) {
        ViewLoader.load(this);

        this.type = type;
        
        if (type == ElectricVehicle.class) {
            deviceIcon.setImage(new Image("images/car.png",50,50,false,true));
            getStyleClass().add("electric-vehicle");
        } else if (type == SolarPanel.class) {
            deviceIcon.setImage(new Image("images/solarpanel.png",50,50,false,true));
            getStyleClass().add("solar-panel");
        } else if (type == DishWasher.class) {
            deviceIcon.setImage(new Image("images/dishwasher.png",50,50,false,true));
            getStyleClass().add("dish-washer");
        } else if (type == WashingMachine.class) {
            deviceIcon.setImage(new Image("images/washingmachine.png",50,50,false,true));
            getStyleClass().add("washing-machine");
        } else if (type == Buffer.class) {
            deviceIcon.setImage(new Image("images/buffer.png",50,50,false,true));
            getStyleClass().add("buffer");
        } else if (type == BufferConverter.class) {
            deviceIcon.setImage(new Image("images/bufferconverter.png",50,50,false,true));
            getStyleClass().add("bufferconverter");
        }else throw new UnsupportedOperationException("No DeviceView for type " + type.toString());

        this.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBorder(buildBorder(Color.DARKGREY));
    }

    public DeviceVM getViewModel() {
        return viewModel;
    }
    
    public void setViewModel(DeviceVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel already set");
        }
        if (viewModel.getModelClass() != type) {
            throw new IllegalArgumentException("ViewModel does not reference a model of type " + type.toString());
        }
        
        this.viewModel = viewModel;

        // Bind config icon visibility to viewmodel
        configIcon.visibleProperty().bind(viewModel.configIconShownProperty());

        // Bind bordercolor to state
        this.borderProperty().bind(Bindings.createObjectBinding(() -> {
            Color color = Color.DARKGREY;
            if (viewModel.getState() == State.CONSUMING) {
                color = color.interpolate(Color.RED, viewModel.getLoad());
            } else if (viewModel.getState() == State.PRODUCING) {
                color = color.interpolate(Color.GREEN, viewModel.getLoad());
            }
            return buildBorder(color);
        }, viewModel.loadProperty(), viewModel.stateProperty()));

        // Handle touch events on config icon
        configIcon.setOnTouchPressed(e -> {
            viewModel.configIconPressed();
            getParent().toFront();
            e.consume();
        });

        // Show/hide config panel when necessary
        viewModel.configPanelShownProperty().addListener(obs -> {
            if (viewModel.isConfigPanelShown()) {
                if (configPanel == null) {
                    configPanel = new DeviceConfigView(viewModel.getDeviceConfigVM());
                }
                getChildren().remove(deviceIcon);
                getChildren().add(0, configPanel);
            } else {
                getChildren().remove(configPanel);
                getChildren().add(0, deviceIcon);
            }
        });
        
        // Show/hide battery icon
        batteryLabel.visibleProperty().bind(viewModel.batteryIconVisibleProperty());
        batteryLabel.textProperty().bind(viewModel.stateOfChargeProperty().multiply(100d).asString("%.0f%%"));
        
        // Show on chart on long press
        EventUtil.addShortAndLongPressEventHandler(this, null, e -> {
            viewModel.longPressed();
        });
        
        viewModel.shownOnChartProperty().addListener(obs -> {
            if (viewModel.isShownOnChart()) {
                getStyleClass().add("on-chart");
            } else {
                getStyleClass().remove("on-chart");
            }
        });
        
        viewModel.chartIndexProperty().addListener(obs -> { 
            int index = viewModel.getChartIndex();
            if (index == -1) {
                getStyleClass().removeIf(s -> s.startsWith("chart-"));
            } else {
                getStyleClass().add("chart-" + index);
            }
        });
    }

    // HELPER METHODS
    // Returns a Border for a given color
    private Border buildBorder(Paint color) {
        return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    }
}

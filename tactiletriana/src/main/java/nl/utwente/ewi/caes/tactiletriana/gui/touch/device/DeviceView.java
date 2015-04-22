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
import javafx.scene.shape.Polygon;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.events.TrianaEvents;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM.State;

/**
 *
 * @author Richard
 */
public class DeviceView extends StackPane {
    @FXML
    private Node configIcon;
    private Node deviceIcon;
    private DeviceConfigView configPanel;

    private DeviceVM viewModel;
    private final DeviceType type;
    
    public DeviceView(DeviceType type) {
        ViewLoader.load(this);

        this.type = type;
        
        deviceIcon = null;
        switch(type) {
            case CAR:
                deviceIcon = new ImageView(new Image("images/car.png",50,50,false,true));
                break;
            case MOCK:
                deviceIcon = new Polygon(new double[]{0d, 50d, 25d, 0d, 50d, 50d});
                break;
            case SOLAR_PANEL:
                deviceIcon = new ImageView(new Image("images/solarpanel.png",50,50,false,true));
                break;
        }
        getChildren().add(0, deviceIcon);

        this.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBorder(buildBorder(Color.DARKGREY));
    }
    
    public DeviceType getType() {
        return type;
    }

    public DeviceVM getViewModel() {
        return viewModel;
    }
    
    public void setViewModel(DeviceVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel already set");
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
            e.consume();
        });

        // Show/hide config panel when necessary
        viewModel.configPanelShownProperty().addListener(obs -> {
            if (viewModel.isConfigPanelShown()) {
                if (configPanel == null) {
                    configPanel = new DeviceConfigView(viewModel.getParameters());
                }
                getChildren().remove(deviceIcon);
                getChildren().add(0, configPanel);
            } else {
                getChildren().remove(configPanel);
                getChildren().add(0, deviceIcon);
            }
        });
        
        // Show on chart on long press
        TrianaEvents.addShortAndLongPressEventHandler(this, null, e -> {
            viewModel.longPressed();
        });
    }

    // HELPER METHODS
    // Returns a Border for a given color
    private Border buildBorder(Paint color) {
        return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    }
    
    public enum DeviceType {
        MOCK,
        CAR,
        SOLAR_PANEL
    }
}

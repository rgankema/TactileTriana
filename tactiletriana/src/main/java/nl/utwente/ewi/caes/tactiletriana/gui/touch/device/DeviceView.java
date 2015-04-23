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
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
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
    private final Node deviceIcon;
    private DeviceConfigView configPanel;

    private DeviceVM viewModel;

    public DeviceView(Node deviceIcon) {
        ViewLoader.load(this);

        this.deviceIcon = deviceIcon;
        getChildren().add(0, deviceIcon);

        this.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBorder(buildBorder(Color.DARKGREY));
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
}

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
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM.State;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.deviceconfig.DeviceConfigView;

/**
 *
 * @author Richard
 */
public class DeviceView extends StackPane {
    @FXML private Node configIcon;
    private Node deviceIcon;
    private DeviceConfigView configPanel;
    
    private DeviceVM viewModel;
    
    
    public DeviceView(Node deviceIcon) {
        ViewLoader.load(this);
        
        this.deviceIcon = deviceIcon;
        getChildren().add(0, deviceIcon);
        
        this.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        this.setBorder(buildBorder(Color.DARKGREY));
    }
    
    public DeviceVM getViewModel() {
        return this.viewModel;
    }
    
    public void setViewModel(DeviceVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel already set");
        
        this.viewModel = viewModel;
        
        configIcon.visibleProperty().bind(viewModel.configIconShownProperty());
        this.borderProperty().bind(Bindings.createObjectBinding(() -> { 
            Color color = Color.DARKGREY;
            if (viewModel.getState() == State.CONSUMING) {
                color = color.interpolate(Color.RED, viewModel.getLoad());
            } else if (viewModel.getState() == State.PRODUCING) {
                color = color.interpolate(Color.GREEN, viewModel.getLoad());
            }
            return buildBorder(color); 
        }, viewModel.loadProperty(), viewModel.stateProperty()));
        
        configIcon.setOnMousePressed(e -> { 
            viewModel.configIconPressed();
            e.consume();
        });
        
        viewModel.configPanelShownProperty().addListener(obs -> { 
            if (viewModel.isConfigPanelShown()) {
                if (configPanel == null) {
                    configPanel = new DeviceConfigView();
                    configPanel.setViewModel(viewModel.getDeviceConfig());
                }
                getChildren().remove(deviceIcon);
                getChildren().add(0, configPanel);
            } else {
                getChildren().remove(configPanel);
                getChildren().add(0, deviceIcon);
            }
        });
    }
    
    private Border buildBorder(Paint color) {
        return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(5)));
    }
}

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
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;
import nl.utwente.ewi.caes.tactilefx.control.TactilePane;
import nl.utwente.ewi.caes.tactilefx.debug.MouseToTouchMapper;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.network.NetworkView;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.*;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class TouchView extends TactilePane {

    @FXML
    private NetworkView networkView;

    private TouchVM viewModel;
    private Simulation simulation;

    public TouchView(Simulation simulation) {
        this.simulation = simulation;
        ViewLoader.load(this);

        addEventFilter(MouseEvent.ANY, new MouseToTouchMapper());
        
        // Track houses
        for (HouseView house : networkView.getHouses()) {
            getActiveNodes().add(house);
        }
        
        
        DeviceView device = new DeviceView(new Polygon(new double[]{0d, 50d, 25d, 0d, 50d, 50d}));
        DeviceVM deviceVM = new DeviceVM(new MockDevice(this.simulation));

        DeviceView solar = new DeviceView(getNewSolarBackground());
        DeviceVM solarVM = new DeviceVM(new SolarPanel(this.simulation));
        
        DeviceView car = new DeviceView(getNewCarBackground());
        DeviceVM carVM = new DeviceVM(new BufferTimeShiftable(this.simulation));

        addDeviceToStack((1920 / 2 - 40), (1080 / 2 - 25), device, deviceVM);
        addDeviceToStack((1920 / 2 + 40), (1080 / 2 - 25), solar, solarVM);
        addDeviceToStack((1920 / 2 - 120), (1080 / 2 - 25), car, carVM);

        // Lelijke hack om devices te verwijderen na reset. moet allemaal ooit mooier
        this.simulation.startedProperty().addListener(i -> {
            if (!this.simulation.isStarted()) {
                getChildren().removeIf(n -> n instanceof Group && ((Group) n).getChildren().get(0) instanceof DeviceView);
                getActiveNodes().clear();

                DeviceVM deviceVM2 = new DeviceVM(new MockDevice(this.simulation));
                DeviceVM solarVM2 = new DeviceVM(new SolarPanel(this.simulation));
                DeviceVM carVM2 = new DeviceVM(new BufferTimeShiftable(this.simulation));

                addDeviceToStack((1920 / 2 - 40), (1080 / 2 - 25), new DeviceView(new Polygon(new double[]{0d, 50d, 25d, 0d, 50d, 50d})), deviceVM2);
                addDeviceToStack((1920 / 2 + 40), (1080 / 2 - 25), new DeviceView(getNewSolarBackground()), solarVM2);
                addDeviceToStack((1920 / 2 + 40), (1080 / 2 - 25), new DeviceView(getNewCarBackground()), carVM2);
            }
        });
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
    }

    private void addDeviceToStack(int x, int y, DeviceView device, DeviceVM deviceVM) {

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
            if (rotate < -90) {
                rotate = -90.0;
            }
            if (rotate > 90) {
                rotate = 90.0;
            }
            return 90.0 - rotate;
        }, group.layoutYProperty()));

        // Add new device when drag starts, remove device if not on house
        TactilePane.inUseProperty(group).addListener(obs -> {
            if (TactilePane.isInUse(group)) {
                if (deviceVM.getModel() instanceof MockDevice) {
                    DeviceView device2 = new DeviceView(new Polygon(new double[]{0d, 50d, 25d, 0d, 50d, 50d}));
                    DeviceVM deviceVM2 = new DeviceVM(new MockDevice(this.simulation));
                    addDeviceToStack(x, y, device2, deviceVM2);
                } else if (deviceVM.getModel() instanceof SolarPanel) {
                    DeviceView solar = new DeviceView(getNewSolarBackground());
                    DeviceVM solarVM = new DeviceVM(new SolarPanel(this.simulation));
                    addDeviceToStack(x, y, solar, solarVM);
                } else if (deviceVM.getModel() instanceof BufferTimeShiftable){
                    DeviceView car = new DeviceView(getNewCarBackground());
                    DeviceVM carVM = new DeviceVM(new BufferTimeShiftable(this.simulation));
                    addDeviceToStack(x, y, car, carVM);
                }
            } else {
                if (!TactilePane.getNodesColliding(group).stream().anyMatch(node -> node instanceof HouseView)) {
                    getChildren().remove(group);
                    getActiveNodes().remove(group);
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
    
    private ImageView getNewSolarBackground(){
        return new ImageView(new Image("images/solarpanel.png",50,50,false,true));
    }
    
    private ImageView getNewCarBackground(){
        return new ImageView(new Image("images/car.png",50,50,false,true));
    }
}

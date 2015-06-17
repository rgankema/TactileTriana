/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.background.BackgroundVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.control.ControlVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.DeviceVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer.TransformerVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.*;

/**
 *
 * @author Richard
 */
public class TouchVM {

    private final Simulation model;

    private final TransformerVM transformer;
    private final NodeVM internalNodes[];
    private final NodeVM houseNodes[];
    private final CableVM internalCables[];
    private final CableVM houseCables[];
    private final HouseVM houses[];

    private final ControlVM control;
    private final BackgroundVM background;

    private final ObservableList<DeviceVM> devices;
    
    public TouchVM(Simulation model) {
        this.model = model;

        this.internalNodes = new NodeVM[6];
        this.houseNodes = new NodeVM[6];
        this.internalCables = new CableVM[6];
        this.houseCables = new CableVM[6];
        this.houses = new HouseVM[6];

        this.transformer = new TransformerVM(model.getTransformer());
        internalCables[0] = new CableVM(model.getTransformer().getCables().get(0));
        Node node = model.getTransformer().getCables().get(0).getChildNode();
        for (int i = 0; i < 6; i++) {
            internalNodes[i] = new NodeVM(node);
            for (Cable cable : node.getCables()) {
                Node childNode = cable.getChildNode();
                if (childNode.getHouse() != null) {
                    houseNodes[i] = new NodeVM(childNode);
                    houseCables[i] = new CableVM(cable);
                    houses[i] = new HouseVM(childNode.getHouse());
                } else {
                    if (i < 5) {
                        internalCables[i + 1] = new CableVM(cable);
                    }
                    node = childNode;
                }
            }
        }

        this.control = new ControlVM(model);
        this.background = new BackgroundVM(model);
        this.devices = FXCollections.observableArrayList();
        
        reset();
    }

    // VIEWMODELS
    public TransformerVM getTransformer() {
        return transformer;
    }

    public NodeVM[] getInternalNodes() {
        return internalNodes;
    }

    public NodeVM[] getHouseNodes() {
        return houseNodes;
    }

    public CableVM[] getInternalCables() {
        return internalCables;
    }

    public CableVM[] getHouseCables() {
        return houseCables;
    }

    public HouseVM[] getHouses() {
        return houses;
    }
    
    public ObservableList<DeviceVM> getDevices() {
        return devices;
    }

    public ControlVM getControlVM() {
        return this.control;
    }
    
    public BackgroundVM getBackgroundVM() {
        return this.background;
    }
    
    // PUBLIC METHODS
    
    public void reset() {
        devices.clear();
        addToDevices(getSolarPanelVM());
        addToDevices(getElectricVehicleVM());
        addToDevices(getDishWasherVM());
        addToDevices(getWashingMachineVM());
        addToDevices(getBufferVM());
        addToDevices(getBufferConverterVM());
        addToDevices(getTrianaHouseControllerVM());
    }
    
    // HELP METHODS
    
    private void addToDevices(DeviceVM device) {
        InvalidationListener listener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (!device.isOnStack()) {
                    
                    addToDevices(getDeviceVM(device.getModelClass()));
                    device.onStackProperty().removeListener(this);
                }
            }
        };
        device.onStackProperty().addListener(listener);
        devices.add(device);
    }
    
    private DeviceVM getDeviceVM(Class<? extends DeviceBase> deviceClass) {
        if (deviceClass.equals(SolarPanel.class)) {
            return getSolarPanelVM();
        } else if (deviceClass.equals(ElectricVehicle.class)) {
            return getElectricVehicleVM();
        } else if (deviceClass.equals(DishWasher.class)) {
            return getDishWasherVM();
        } else if (deviceClass.equals(WashingMachine.class)) {
            return getWashingMachineVM();
        } else if (deviceClass.equals(Buffer.class)) {
            return getBufferVM();
        } else if (deviceClass.equals(BufferConverter.class)) {
            return getBufferConverterVM();
        } else if (deviceClass.equals(TrianaHouseController.class)) {
            return getTrianaHouseControllerVM();
        }
        return null;
    }

    private DeviceVM getSolarPanelVM() {
        return new DeviceVM(new SolarPanel(model));
    }

    private DeviceVM getElectricVehicleVM() {
        return new DeviceVM(new ElectricVehicle(model));
    }

    private DeviceVM getDishWasherVM() {
        return new DeviceVM(new DishWasher(model));
    }

    private DeviceVM getWashingMachineVM() {
        return new DeviceVM(new WashingMachine(model));
    }

    private DeviceVM getBufferVM() {
        return new DeviceVM(new Buffer(model));
    }

    private DeviceVM getBufferConverterVM() {
        return new DeviceVM(new BufferConverter(model));
    }
    
    private DeviceVM getTrianaHouseControllerVM() {
        return new DeviceVM(new TrianaHouseController(model));
    }
}

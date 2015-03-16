/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;

/**
 *
 * @author Richard
 */
public class DeviceVM {
    private DeviceBase model;
    
    public DeviceVM(DeviceBase model) {
        this.model = model;
        
        load.bind(Bindings.createDoubleBinding(() -> { 
            return Math.min(1.0, Math.abs(model.getCurrentConsumption()) / 3700d); // TODO: zeker dat 3700 goed is? gewoon van Gerwin overgenomen
        }, model.currentConsumptionProperty()));
        
        state.bind(Bindings.createObjectBinding(() -> {
            if (model.getCurrentConsumption() < 0)
                return State.PRODUCING;
            else
                return State.CONSUMING;
        }, model.currentConsumptionProperty()));
    }
    
    private final ReadOnlyDoubleWrapper load = new ReadOnlyDoubleWrapper();
    
    public ReadOnlyDoubleProperty loadProperty() {
        return load.getReadOnlyProperty();
    }
    
    public double getLoad() {
        return load.get();
    }
    
    private final ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>();
    
    public ReadOnlyObjectProperty<State> stateProperty() {
        return state.getReadOnlyProperty();
    }
    
    public State getState() {
        return state.get();
    }
    
    private final BooleanProperty configIconShown = new SimpleBooleanProperty(false);
    
    public BooleanProperty configIconShownProperty() {
        return configIconShown;
    }
    
    public boolean isConfigIconShown() {
        return configIconShown.get();
    }
    
    public void setConfigIconShown(boolean configIconShown) {
        this.configIconShown.set(configIconShown);
    }
    
    public enum State {
        DISCONNECTED,
        PRODUCING,
        CONSUMING
    }
}

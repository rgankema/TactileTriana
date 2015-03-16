/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;

/**
 *
 * @author Richard
 */
public class DeviceVM {
    public enum State {
        DISCONNECTED,
        PRODUCING,
        CONSUMING
    }
    
    private DeviceBase model;
    
    public DeviceVM(DeviceBase model) {
        this.model = model;
        
        load.bind(Bindings.createDoubleBinding(() -> { 
            return Math.min(1.0, Math.abs(model.getCurrentConsumption()) / 3700d); // TODO: zeker dat 3700 goed is? gewoon van Gerwin overgenomen
        }, model.currentConsumptionProperty()));
        
        state.bind(Bindings.createObjectBinding(() -> {
            if (model.getState() == DeviceBase.State.NOT_IN_HOUSE)
                return State.DISCONNECTED;
            if (model.getCurrentConsumption() < 0)
                return State.PRODUCING;
            else
                return State.CONSUMING;
        }, model.currentConsumptionProperty()));
        
        configIconShown.bind(Bindings.createBooleanBinding(() -> { 
            return getState() != State.DISCONNECTED;
        }, model.stateProperty()));
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
    
    public final State getState() {
        return state.get();
    }
    
    private final ReadOnlyBooleanWrapper configIconShown = new ReadOnlyBooleanWrapper(false);
    
    public ReadOnlyBooleanProperty configIconShownProperty() {
        return configIconShown.getReadOnlyProperty();
    }
    
    public final boolean isConfigIconShown() {
        return configIconShown.get();
    }
    
    private final ReadOnlyBooleanWrapper configPanelShown = new ReadOnlyBooleanWrapper(false);
    
    public ReadOnlyBooleanProperty configPanelShownProperty() {
        return configPanelShown.getReadOnlyProperty();
    }
    
    public boolean isConfigPanelShown() {
        return configPanelShown.get();
    }
    
    private void setConfigPanelShown(boolean configPanelShown) {
        this.configPanelShown.set(configPanelShown);
    }
    
    // EVENT HANDLING
    
    public void configIconClicked() {
        setConfigPanelShown(!isConfigPanelShown());
    }
}

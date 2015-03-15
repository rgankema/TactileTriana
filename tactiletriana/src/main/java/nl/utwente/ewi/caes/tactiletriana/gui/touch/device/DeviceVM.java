/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.property.BooleanProperty;
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
    }
    
    private final BooleanProperty configIconShown = new SimpleBooleanProperty(false);
    
    public boolean isConfigIconShown() {
        return configIconShown.get();
    }
    
    public void setConfigIconShown(boolean configIconShown) {
        this.configIconShown.set(configIconShown);
    }
    
    public BooleanProperty configIconShownProperty() {
        return configIconShown;
    }
}

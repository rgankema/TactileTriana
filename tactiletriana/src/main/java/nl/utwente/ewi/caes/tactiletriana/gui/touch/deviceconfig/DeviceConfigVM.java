/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.deviceconfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.Parameter;

/**
 *
 * @author Richard
 */
public class DeviceConfigVM {
    private final List parameters;
    
    public DeviceConfigVM(Parameter[] deviceParameters) {
        this.parameters = Collections.unmodifiableList(Arrays.asList(deviceParameters));
    }
    
    /**
     * Whether the DeviceConfigView is visible
     */
    private final BooleanProperty visible = new SimpleBooleanProperty(false);
    
    public BooleanProperty visibleProperty() {
        return visible;
    }
    
    public boolean isVisible() {
        return visible.get();
    }
    
    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }
    
    /**
     * @return the list of parameters that can be configured
     */
    public List<Parameter> getParameters() {
        return parameters;
    }
}

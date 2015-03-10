/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import nl.caes.ewi.utwente.nl.tactiletriana.simulation.CableBase;

/**
 *
 * @author Richard
 */
public class CableVM {
    private CableBase model;
    
    public CableVM(CableBase model) {
        this.model = model;
       
        Bindings.createDoubleBinding(() -> { return Math.min(1.0, Math.abs(model.getCurrent()) / model.getMaximumCurrent()); }, model.currentProperty(), model.maximumCurrentProperty());
    }
    
    /**
     * The load on the cable on a scale from 0 to 1.
     */
    private final ReadOnlyDoubleWrapper load = new ReadOnlyDoubleWrapper(0);
    
    public double getLoad() {
        return load.get();
    }
    
    public ReadOnlyDoubleProperty loadProperty() {
        return load.getReadOnlyProperty();
    }
        
}

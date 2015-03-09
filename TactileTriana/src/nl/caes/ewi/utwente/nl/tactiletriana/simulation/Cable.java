/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

/**
 *
 * @author Richard
 */
public class Cable implements ICable {
    //TODO: betere waarde
    private double maxCurrent = Double.MAX_VALUE;
    
    // SIMPLE PROPERTIES
    
    @Override
    public Node getNodeA() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Node getNodeB() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    // BINDABLE PROPERTIES
    
    private ReadOnlyBooleanWrapper broken = new ReadOnlyBooleanWrapper(false) {
        @Override
        public void set(boolean value) {
            if (value) {
                setCurrent(0);
            }
            super.set(value);
        }
    };
    
    public boolean isBroken() {
        return broken.get();
    }
    
    private void setBroken(boolean value) {
        broken.set(value);
    }
    
    @Override
    public ReadOnlyBooleanProperty brokenProperty() {
        return broken.getReadOnlyProperty();
    }
    
    private ReadOnlyDoubleWrapper current = new ReadOnlyDoubleWrapper(0.0) {
        @Override
        public void set(double value) {
            if (value > maxCurrent) {
                setBroken(true);
            }
            super.set(value);
        }
    };

    public double getCurrent() {
        return current.get();
    }
    
    private void setCurrent(double value) {
        current.set(value);
    }
    
    @Override
    public ReadOnlyDoubleProperty currentProperty() {
        return current.getReadOnlyProperty();
    }

}

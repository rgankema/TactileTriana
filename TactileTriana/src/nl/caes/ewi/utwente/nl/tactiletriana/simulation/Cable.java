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
    
    private ReadOnlyBooleanWrapper broken;
    
    public boolean isBroken() {
        return privateBrokenProperty().get();
    }
    
    private void setBroken(boolean broken) {
        privateBrokenProperty().set(broken);
    }
    
    private ReadOnlyBooleanWrapper privateBrokenProperty() {
        if (broken == null) {
            broken = new ReadOnlyBooleanWrapper(false);
        }
        return broken;
    }
    
    @Override
    public ReadOnlyBooleanProperty brokenProperty() {
        return privateBrokenProperty().getReadOnlyProperty();
    }
    
    private ReadOnlyDoubleWrapper current;

    public double getCurrent() {
        return privateCurrentProperty().get();
    }
    
    private void setCurrent(double current) {
        privateCurrentProperty().set(current);
    }
    
    private ReadOnlyDoubleWrapper privateCurrentProperty() {
        if (current == null) {
            current = new ReadOnlyDoubleWrapper();
        }
        return current;
    }
    
    @Override
    public ReadOnlyDoubleProperty currentProperty() {
        return privateCurrentProperty().getReadOnlyProperty();
    }

}

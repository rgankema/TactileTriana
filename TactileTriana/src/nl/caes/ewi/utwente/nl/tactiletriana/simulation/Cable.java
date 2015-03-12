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
public class Cable extends CableBase implements ISimulationEntity {
    private final Node child;
    private final double resistance;
    
    /**
     * Instantiates a new cable connected to two nodes
     * @param child     The node away from the transformer
     */
    public Cable(Node child) {
        this.child = child;
        this.resistance = 0.00005;
    }
    
    // SIMPLE PROPERTIES

    @Override
    public Node getChildNode() {
        return child;
    }
    
    // BINDABLE PROPERTIES
    
    private final ReadOnlyBooleanWrapper broken = new ReadOnlyBooleanWrapper(false) {
        @Override
        public void set(boolean value) {
            if (value) {
                setCurrent(0);
            }
            super.set(value);
        }
    };
    
    private void setBroken(boolean value) {
        broken.set(value);
    }
    
    @Override
    public ReadOnlyBooleanProperty brokenProperty() {
        return broken.getReadOnlyProperty();
    }
    
    private final ReadOnlyDoubleWrapper current = new ReadOnlyDoubleWrapper(0.0) {
        @Override
        public void set(double value) {
            if (Math.abs(value) > getMaximumCurrent()) {
                setBroken(true);
            }
            super.set(value);
        }
    };
    
    private void setCurrent(double value) {
        current.set(value);
    }
    
    @Override
    public ReadOnlyDoubleProperty currentProperty() {
        return current.getReadOnlyProperty();
    }
    
    private final ReadOnlyDoubleWrapper maximumCurrent = new ReadOnlyDoubleWrapper(Double.MAX_VALUE);   //TODO: betere waarde verzinnen
    
    @Override
    public ReadOnlyDoubleProperty maximumCurrentProperty() {
        return maximumCurrent;
    }
    
    // METHODS
    
    //stub
    public double doForwardBackwardSweep(ISimulationEntity from, double v) {
        return 10;
    }
    
    @Override
    public String toString(){
        return "(Cable:R="+ resistance +  ",I="+ this.getCurrent() + ") -> " + this.getChildNode().toString();
    }
    
}

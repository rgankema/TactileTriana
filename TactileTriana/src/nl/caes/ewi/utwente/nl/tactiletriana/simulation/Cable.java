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
public class Cable implements ICable,ISimulationObject {
    private final Node parent;
    private final Node child;
    
    private int resistance;
    /**
     * Instantiates a new cable connected to two nodes
     * @param parent    The node in the direction of the transformer
     * @param child     The node away from the transformer
     */
    public Cable(Node parent, Node child) {
        this.parent = parent;
        this.child = child;
    }
    
    // SIMPLE PROPERTIES
    

    @Override
    public Node getNodeChild() {
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
    
    private final ReadOnlyDoubleWrapper current = new ReadOnlyDoubleWrapper(0.0) {
        @Override
        public void set(double value) {
            if (Math.abs(value) > getMaximumCurrent()) {
                setBroken(true);
            }
            super.set(value);
        }
    };

    @Override
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
    
    private final ReadOnlyDoubleWrapper maximumCurrent = new ReadOnlyDoubleWrapper(Double.MAX_VALUE);   //TODO: betere waarde verzinnen
    
    @Override
    public double getMaximumCurrent() {
        return maximumCurrent.get();
    }
    
    @Override
    public ReadOnlyDoubleProperty maximumCurrentProperty() {
        return maximumCurrent;
    }
    
    //stub
    public double doForwardBackwardSweep(ISimulationObject from, double v) {
        return 10;
    }

    @Override
    public double getResistance() {
        return this.resistance;
    }
    
    @Override
    public String toString(){
        return "Cable:" + this.getCurrent() + " -> " + this.getNodeChild().toString();
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

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
    private final double length;    
    
    /**
     * Instantiates a new cable connected to two nodes
     * @param child     The node away from the transformer
     */
    public Cable(Node child) {
        this.child = child;
        //TODO provide sane defaults
        this.resistance = 0.00005;
        this.length = 10;
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
            if (value) { // isBroken(). In tick() this is propageted throught the entire tree
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
    
    private final ReadOnlyDoubleWrapper maximumCurrent = new ReadOnlyDoubleWrapper(100.0);   //TODO: betere waarde verzinnen
    
    @Override
    public ReadOnlyDoubleProperty maximumCurrentProperty() {
        return maximumCurrent;
    }
    
    // METHODS
    
    //stub
    @Override
    public double doForwardBackwardSweep(double v) {
       //update the voltages in the forward sweep
        double voltage = v - (getCurrent() * (resistance*length));

        setCurrent(child.doForwardBackwardSweep(voltage));
        
        return getCurrent();
    }
    
    public String toString(int indentation){
        String output = "";
        for (int i = 0; i < indentation; i++){
            output += "\t";
        }
        output += "|-";
        
        output += "(Cable:R="+ resistance +  ",I="+ this.getCurrent() + ") -> " + this.getChildNode().toString(indentation);
        return output;
    }

    @Override
    public void resetEntity(double voltage, double current) {
        this.setCurrent(current);
    }
}

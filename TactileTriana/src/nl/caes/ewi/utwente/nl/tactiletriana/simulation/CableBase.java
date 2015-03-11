/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 * A connection between to nodes
 */

//TODO check if the ISimulation implements should go here or in the Cable class
public abstract class CableBase implements ISimulationEntity{
    /**
     * 
     * @return the current flowing through the cable
     */
    public abstract ReadOnlyDoubleProperty currentProperty();
    
    public final double getCurrent() {
        return currentProperty().get();
    }
    
    
    /**
     * 
     * @return the absolute maximum current that can flow through the cable before it breaks
     */
    public abstract ReadOnlyDoubleProperty maximumCurrentProperty();
    
    public final double getMaximumCurrent() {
        return maximumCurrentProperty().get();
    }
    
    /**
     * 
     * @return whether the cable is broken or not
     */
    public abstract ReadOnlyBooleanProperty brokenProperty();
    
    public final boolean isBroken() {
        return brokenProperty().get();
    }
    
    /**
     * 
     * @return the node that is the child of this cable
     */
    public abstract NodeBase getChildNode();
}

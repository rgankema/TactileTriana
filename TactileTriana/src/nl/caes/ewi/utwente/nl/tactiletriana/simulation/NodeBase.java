/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import java.util.Set;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Richard
 */
public abstract class NodeBase {

    public ReadOnlyDoubleProperty voltageProperty();
    public double getVoltage();
    
    /**
     * 
     * @return a list of cables that connect to this node
     */
    public abstract Set<CableBase> getCables();
    
    /**
     * 
     * @return the house this Node is connected to, may be null
     */
    public abstract IHouse getHouse();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.List;
import java.util.Set;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Richard
 */
public abstract class NodeBase {


    public abstract ReadOnlyDoubleProperty voltageProperty();
    
    public final double getVoltage() {
        return voltageProperty().get();
    }
    /**
     * 
     * @return a list of cables that connect to this node
     */
    public abstract List<CableBase> getCables();
    
    /**
     * 
     * @return the house this Node is connected to, may be null
     */
    public abstract HouseBase getHouse();
    
    public void tick(double time, boolean connected) {
        if (getHouse() != null) {
            getHouse().tick(time, connected);
        }
        for (CableBase cable : getCables()) {
            cable.tick(time, connected);
        }
    }
}

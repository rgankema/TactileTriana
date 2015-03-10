/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Richard
 */
public class Node extends NodeBase implements ISimulationEntity {
    private final Set<CableBase> cables;
    private final House house;
    
    public Node(House house) {
        this.cables = new HashSet<>();
        this.house = house;
    }
    

    
    @Override
    public Set<CableBase> getCables() {
        return this.cables;
    }

    @Override
    public House getHouse() {
        return this.house;
    }

    @Override
    public ReadOnlyDoubleProperty voltageProperty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //stub
    @Override
    public double doForwardBackwardSweep(ISimulationEntity from, double v) {
        //TODO: impelement
        return 10;
    }
    
    @Override
    public String toString(){
        String output =  "(Node:U="+ getVoltage() + ") -> " + getHouse().toString() + "\n";
        for (CableBase c: this.getCables()){
            output += "->";
            output += c.toString();
        }
        return output;
    }
    
}

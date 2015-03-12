/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

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

    private final ReadOnlyDoubleWrapper voltage = new ReadOnlyDoubleWrapper(230.0);
    
    protected final void setVoltage(double voltage) {
        this.voltage.set(voltage);
    }
    
    @Override
    public ReadOnlyDoubleProperty voltageProperty() {
        return voltage.getReadOnlyProperty();
    }
    
    //stub
    @Override
    public double doForwardBackwardSweep(ISimulationEntity from, double v) {
        //TODO: implement
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

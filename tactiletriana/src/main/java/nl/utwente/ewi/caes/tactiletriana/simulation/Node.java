/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

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
    
    //ForwardBackwardSweep Load-flow algorithm.
    //The network is build as a tree. The node has references to outgoing (with respect to the root of the tree(transformer)) cables. 
    @Override
    public double doForwardBackwardSweep(double v) {
        double current = 0.0;
        //Forward sweep, update the voltages
        this.setVoltage(v);
        
        for(CableBase c : cables){
            
                current += ((Cable)c).doForwardBackwardSweep(this.getVoltage());
            
        }
        if(house != null){
            current += (house.getCurrentConsumption()/this.getVoltage()); //I = P/U //Apparently this one is inversed?
            //System.out.println(current);
        }
        //System.out.println(voltage);
        
        return current;
    }
    
    
    public String toString(int indentation){
        String output = "";
        for (int i = 0; i < indentation; i++){
            output += "\t";
        }
        output += "|-";
        
        if (getHouse() != null) {
            output =  "(Node:U="+ getVoltage() + ")\n" + getHouse().toString(indentation+1) + "\n";
        } else {
            output =  "(Node:U="+ getVoltage() + ") -> " + "\n";
        }
        for (CableBase c: this.getCables()){
            output += c.toString(indentation+1);
        }
        return output;
    }

    @Override
    public void resetEntity(double voltage, double current) {
        this.setVoltage(voltage);
        
        for(CableBase c : cables){
            ((Cable)c).resetEntity(voltage, current);
        }
    }
    
}

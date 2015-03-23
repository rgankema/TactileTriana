/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

/**
 *
 * @author Richard
 */
public class Node implements ISimulationEntity{
    private final List<Cable> cables;
    private final House house;
    
    public Node(House house) {
        this.cables = new ArrayList<>();
        this.house = house;
    }

    /**
     * The voltage measured on this node
     */
    private final ReadOnlyDoubleWrapper voltage = new ReadOnlyDoubleWrapper(230.0);
    
    public ReadOnlyDoubleProperty voltageProperty() {
        return voltage.getReadOnlyProperty();
    }
    
    public final double getVoltage() {
        return voltageProperty().get();
    }
    
    protected void setVoltage(double voltage) {
        this.voltage.set(voltage);
    }
    
    /**
     * 
     * @return a list of cables that connect to this node
     */
    public List<Cable> getCables() {
        return this.cables;
    }
    
    /**
     * 
     * @return the house this Node is connected to, may be null
     */
    public House getHouse() {
        return this.house;
    }
    
    public void tick(Simulation simulation, boolean connected) {
        if (getHouse() != null) {
            getHouse().tick(simulation, connected);
        }
        for (Cable cable : getCables()) {
            cable.tick(simulation, connected);
        }
    }
    
    //ForwardBackwardSweep Load-flow algorithm.
    //The network is build as a tree. The node has references to outgoing (with respect to the root of the tree(transformer)) cables. 
    @Override
    public double doForwardBackwardSweep(double v) {
        double current = 0.0;
        
        //Forward sweep, update the voltages
        this.setVoltage(v);
        
        for(Cable c : cables){
            current += c.doForwardBackwardSweep(this.getVoltage());
        }
        if(house != null){
            if (this.getVoltage() != 0) {
                current += (house.getCurrentConsumption() / this.getVoltage()); //I = P/U //Apparently this one is inversed?
            }
        }
        return current;
    }
    
    @Override
    public void reset() {
        this.setVoltage(230d);
        
        for(Cable c : cables){
            c.reset();
        }
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
        for (Cable c: this.getCables()){
            output += c.toString(indentation+1);
        }
        return output;
    }
}

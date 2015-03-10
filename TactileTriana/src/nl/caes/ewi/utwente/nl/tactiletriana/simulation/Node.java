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
public class Node implements INode,IFBSweep {
    private final Set<ICable> cables;
    private final House house;
    
    public Node(House house) {
        this.cables = new HashSet<>();
        this.house = house;
    }
    

    
    @Override
    public Set<ICable> getCables() {
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

    @Override
    public double getVoltage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    //stub
    public double doForwardBackwardSweep(IFBSweep from, double v) {
        return 10;
    }

    @Override
    /*
    Let op een node weet alleen de outgoing cables!
    */
    public void addCable(ICable c) {
        this.cables.add(c);
    }
    
}

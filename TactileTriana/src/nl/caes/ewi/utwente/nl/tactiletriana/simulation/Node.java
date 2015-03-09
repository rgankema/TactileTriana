/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Richard
 */
public class Node implements INode {
    private Set<ICable> cables;
    private House house;
    
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
    
}

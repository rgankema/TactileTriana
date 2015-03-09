/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import java.util.List;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;

/**
 *
 * @author Richard
 */
public interface ICable {
    public ReadOnlyDoubleProperty currentProperty();
    
    public ReadOnlyBooleanProperty brokenProperty();
    
    /**
     * 
     * @return one of the nodes this cable connects to
     */
    public INode getNodeA();
    /**
     * 
     * @return the other node this cable connects to
     */
    public INode getNodeB();
}

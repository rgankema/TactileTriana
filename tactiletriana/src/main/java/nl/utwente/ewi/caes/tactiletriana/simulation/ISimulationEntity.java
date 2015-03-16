/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;

/**
 *
 * @author jd
 */
public interface ISimulationEntity {
    
    //Implement the ForwardBackwardSweep Load-flow algorithm 
    //The network is assumed to be structured as a tree. 
    //This function is called recursively from the root of the tree. A cycle in the network graph(breaking the tree structure) breaks this function. 
    public double doForwardBackwardSweep(double v);
    
    //Reset the Simulation Entity to the default state and prepare the node for a new Load-flow calculation. Child SimulationEntities should be updated recursively.
    public void resetEntity(double voltage, double current);    // Zou het hele idee van een reset niet moeten zijn dat je GEEN parameters mee geeft?
    
    //Return the nodes in the network
    public ArrayList<Node> getNodes();
}

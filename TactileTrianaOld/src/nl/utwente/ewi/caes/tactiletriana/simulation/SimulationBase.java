/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

/**
 *
 * @author Richard
 */
public abstract class SimulationBase {
    /**
     * 
     * @return the rootnode of the network
     */
    public abstract Transformer getTransformer();
    
    /**
     * Starts the simulation
     */
    public abstract void start();
    
    /**
     * Pauses the simulation
     */
    public abstract void stop();
}

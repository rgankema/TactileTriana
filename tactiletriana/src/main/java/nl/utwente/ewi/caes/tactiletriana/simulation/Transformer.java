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
public class Transformer extends Node {

    public Transformer(SimulationBase simulation) {
        super(null, simulation);
        setVoltage(230);
    }
    
    /**
     * Initialises the network with voltage at 230 and current at 0.
     */
    public void prepareForwardBackwardSweep() {
        for (Cable c : getCables()) {
            c.prepareForwardBackwardSweep(230);
        }
    }
}

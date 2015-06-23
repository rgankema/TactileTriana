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

    public final double TRANSFORMER_CURRENT = 230.0;

    public Transformer(SimulationBase simulation) {
        super(null, simulation);
        setVoltage(230);
    }
    
    @Override
    public void prepareForwardBackwardSweep(){
        
        this.getCables().get(0);
    }
}

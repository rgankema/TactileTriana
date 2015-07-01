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

    /**
     * Creates a Transformer, a Node which has its current fixed on 230.0 Volt.
     * @param simulation 
     */
    public Transformer(SimulationBase simulation) {
        super(null, simulation);
        setVoltage(TRANSFORMER_CURRENT);
    }
}

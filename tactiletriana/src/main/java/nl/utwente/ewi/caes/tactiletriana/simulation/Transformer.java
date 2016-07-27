/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

/**
 * A special kind of Node whose voltage is always 230.
 */
public class Transformer extends Node {

    public Transformer(SimulationBase simulation) {
        super(null, simulation);
        setVoltage(230);
    }
}

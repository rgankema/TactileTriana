/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

/**
 *
 * @author jd
 */
public interface ISimulationEntity {
    
    public double doForwardBackwardSweep(ISimulationEntity from, double v); 
    
}
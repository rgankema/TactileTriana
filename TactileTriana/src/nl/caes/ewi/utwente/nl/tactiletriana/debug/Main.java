/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.debug;

import nl.caes.ewi.utwente.nl.tactiletriana.simulation.Simulation;

/**
 *
 * @author mickvdv
 */
public class Main {
    public static void main(String[] args) {
        Simulation s = Simulation.getInstance();
        
        s.initiateForwardBackwardSweep();
        System.out.println(s.getTransformer().toString());
        
        s.initiateTick(300);
        System.out.println(s.getTransformer().toString());
        
        s.initiateForwardBackwardSweep();
        System.out.println(s.getTransformer().toString());
        
    }
    
}

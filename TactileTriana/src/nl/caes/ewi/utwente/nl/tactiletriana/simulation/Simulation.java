/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import java.util.Set;

/**
 *
 * @author Richard
 */
public class Simulation implements ISimulation {
    
    private static Simulation instance = null;
    private Transformer transformer;
    //Time between ticks of the simulation (in milliseconds)
    private int tickTime = 200;
    
    public static Simulation getInstance() {
        if (instance == null) {
            instance = new Simulation();
        }
        return instance;
    }
    
    private Simulation() {
        transformer = new Transformer();
    }
    
    public Transformer getTransformer() {
        return transformer;
    }
    
    public void start() {
        while(true){
            transformer.initiateForwardBackwardSweep();
            try {
                Thread.sleep(tickTime);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

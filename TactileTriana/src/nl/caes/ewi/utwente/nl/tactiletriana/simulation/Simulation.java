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
        
        // de tree maken
        transformer = new Transformer();
        Node[] nodes = new Node[6];
        Cable[] cables = new Cable[6];
        House[] houses = new House[6];
        
        // maak huizen aan met cables en dat soort grappen
        for(int i = 0; i <= 5; i ++){
            houses[i] = new House();
            nodes[i] = new Node(houses[i]);
            
            if (i == 0){
                cables[i] = new Cable(transformer, nodes[i]);
                transformer.addCable(cables[i]);
            }
            else{
                cables[i] = new Cable(nodes[i-1], nodes[i]);
                nodes[i-1].addCable(cables[i]);
            }
        }
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

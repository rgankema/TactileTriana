/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Richard
 */
public class Simulation {
    public static final int NUMBER_OF_HOUSES = 6;   // number of houses
    public static final int TICK_TIME = 100;        // time between ticks in ms
     
    private static Simulation instance = null;
    private final Transformer transformer;
    private final Map<Node, Double> lastVoltageByNode;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private double time;
    
    public static Simulation getInstance() {
        if (instance == null) {
            instance = new Simulation();
        }
        return instance;
    }
    
    private Simulation() {
        // keep an array of nodes for later reference
        this.lastVoltageByNode = new HashMap<>();
        
        // de tree maken
        transformer = new Transformer();
        Node[] internalNodes = new Node[NUMBER_OF_HOUSES];
        Node[] houseNodes = new Node[NUMBER_OF_HOUSES];
        Cable[] cables = new Cable[NUMBER_OF_HOUSES];
        House[] houses = new House[NUMBER_OF_HOUSES];
        
        // maak huizen aan met cables en dat soort grappen
        for(int i = 0; i <= NUMBER_OF_HOUSES-1; i ++){
            houses[i] = new House();
            
            houseNodes[i] = new Node(houses[i]);
            internalNodes[i] = new Node(null);
            Cable houseCable = new Cable(houseNodes[i]);
            internalNodes[i].getCables().add(houseCable);
            
            cables[i] = new Cable(internalNodes[i]);
            if (i == 0) {
                transformer.getCables().add(cables[i]);
            }
            else {
                internalNodes[i-1].getCables().add(cables[i]);
            }
            
            lastVoltageByNode.put(internalNodes[i], 230d);
            lastVoltageByNode.put(houseNodes[i], 230d);
        }
        
        // initialise time
        this.time = 0;
    }
    
    // PROPERTIES
    
    public Transformer getTransformer() {
        return transformer;
    }
    
    // PUBLIC METHODS
    
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            getTransformer().tick(time, true);
            initiateForwardBackwardSweep();
            time += 1d/60d; // een minuut per tick voor nu
            if (time == 24){
                time = 0;
            }
        }, TICK_TIME, TICK_TIME, TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        scheduler.shutdown();
    }
    
    // FORWARD BACKWARD SWEEP METHODS

    // Start the forward backward sweep algorithm
    private void initiateForwardBackwardSweep() {
        // First reset the nodes.
        transformer.reset();
        // Run the ForwardBackwardSweep Load-flow calculation until converged or the iteration limit is reached
        for(int i = 0; i < 20; i++) {
            transformer.doForwardBackwardSweep(230); // this runs recursivly down the tree
            
            if (hasFBSConverged(0.0001)) break;
            
            // Store last voltage to check for convergence
            for (Node node : this.lastVoltageByNode.keySet()) {
                lastVoltageByNode.put(node, node.getVoltage());
            }
        }
    }
    
    // Calculate if the FBS algorithm has converged. 
    private boolean hasFBSConverged(double error) {
        boolean result = true;
        
        //Loop through the network-tree and compare the previous voltage from each with the current voltage.
        //If the difference between the previous and current voltage is smaller than the given error, the result is true
        for (Node node : this.lastVoltageByNode.keySet()) {
            if (!result) break;
            result = (Math.abs(lastVoltageByNode.get(node) - node.getVoltage()) < error);
        }
        return result;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.MockDevice;

/**
 *
 * @author Richard
 */
public class Simulation extends SimulationBase implements Runnable {
    // Declare simulation constants
    public static final int NUMBER_OF_HOUSES = 6;
    //Time between ticks of the simulation (in seconds) 
    public static final int TICK_TIME = 1;
    
    private boolean simulationRunning = false;
     
    private static Simulation instance = null;
    private final Transformer transformer;
    
    private double time = 0;
    
    private final ScheduledExecutorService scheduler =
     Executors.newScheduledThreadPool(1);
       
    
    public static Simulation getInstance() {
        if (instance == null) {
            instance = new Simulation();
        }
        return instance;
    }
    
    private Simulation() {
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
        }
    }
    
    @Override
    public Transformer getTransformer() {
        return transformer;
    }
    
    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(this, TICK_TIME, TICK_TIME, TimeUnit.SECONDS);
    }
       
    public void initiateForwardBackwardSweep() {
        //First reset the nodes.
        transformer.resetEntity(230, 0);
        //Run the ForwardBackwardSweep Load-flow calculation until converged or the iteration limit is reached
        for(int i = 0; (i < 20) && !calculateFBSConvergence(0.000001); i++) {
            System.out.println("Iteration" + i);
            transformer.doForwardBackwardSweep(230); // this runs recursivly down the tree
        }
    }
    
    //Calculate if the FBS algorithm has converged. 
    private boolean calculateFBSConvergence(double error) {
        boolean result = true;
        //Loop through the network-tree and compare the previous voltage from each with the current voltage.
        //If the difference between the previous and current voltage is smaller than the given error, the result is true
        ArrayList<Node> nodes = transformer.getNodes();
        
        for(int i = 0; (i < nodes.size()) && result; i++) {
            System.out.println("FWBWS Iteratie: " + i);
            if(Math.abs(nodes.get(i).getPreviousVoltage() - nodes.get(i).getVoltage()) > error) {
                result = false;
            }
        }
        return result;
    }
    
    private void initiateTick(double time){
        this.getTransformer().tick(time, true);
    }
    
    @Override
    public void stop() {
        scheduler.shutdown();
    }

    public void run() {
        simulationRunning = true;
        initiateForwardBackwardSweep();
        initiateTick(time);
        time = time + 0.25;
        if (time == 24){
            time = 0;
        }
        //Debug lines:
        System.out.println("time: "+time);
        System.out.println(transformer.toString());
    }
    
    
    //TODO: remove this
    public static void main(String args[]){
        Simulation s = new Simulation();
        s.start();
    }

}

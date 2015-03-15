/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import nl.utwente.ewi.caes.tactiletriana.simulation.devices.MockDevice;

/**
 *
 * @author Richard
 */
public class Simulation extends SimulationBase {
    // Declare simulation constants
    public static final int NUMBER_OF_HOUSES = 6;
    public static final int TICK_TIME = 200;
     
    private static Simulation instance = null;
    private final Transformer transformer;
    //Time between ticks of the simulation (in milliseconds) 
    //Richard hier: dit klopt niet. De tick moet de tijd zijn die in de simulatie voorbij gaat in minuten. Verander plx
   
    
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
            
            //DEBUG:
            // Add a mockup device to every house.
            // For testing purposes!
            houses[i].getDevices().add(new MockDevice());
            
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
        while(true){
            initiateForwardBackwardSweep();
            try {
                Thread.sleep(TICK_TIME);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
       
    private void initiateForwardBackwardSweep() {
        //First Reset the nodes.
        transformer.resetEntity(230, 0);
        //Run the ForwwardBackWardSweep Load-flow calculation 10 times and assume convergence.
        for(int i = 0; i < 10; i++) {
            transformer.doForwardBackwardSweep(230);
        }
    }
    
    private void initiateTick(double time){
        this.getTransformer().tick(time);
    }
    
    @Override
    public void stop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

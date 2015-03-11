/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

/**
 *
 * @author Richard
 */
public class Simulation extends SimulationBase {
    // Declare simulation constants
    public final int NUMBER_OF_HOUSES = 6;
    public final int TICK_TIME = 200;
     
    private static Simulation instance = null;
    private Transformer transformer;
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
        Node[] nodes = new Node[NUMBER_OF_HOUSES];
        Cable[] cables = new Cable[NUMBER_OF_HOUSES];
        House[] houses = new House[NUMBER_OF_HOUSES];
        
        // maak huizen aan met cables en dat soort grappen
        for(int i = 0; i <= NUMBER_OF_HOUSES-1; i ++){
            houses[i] = new House();
            nodes[i] = new Node(houses[i]);
            
            if (i == 0){
                cables[i] = new Cable(nodes[i]);
                transformer.getCables().add(cables[i]);
            }
            else{
                cables[i] = new Cable(nodes[i]);
                nodes[i-1].getCables().add(cables[i]);
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
                Thread.sleep(TICK_TIME);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simprediction;

import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author mickvdv
 */
public class SimulationPrediction extends Simulation {
    private static SimulationPrediction instance;
    
    protected SimulationPrediction(){
        super();
        
        // Koppelhuizen maken
    }
    
    public static SimulationPrediction getPredictionInstance() {
      if(instance == null) {
         instance = new SimulationPrediction();
      }
      return instance;
   }
    
}

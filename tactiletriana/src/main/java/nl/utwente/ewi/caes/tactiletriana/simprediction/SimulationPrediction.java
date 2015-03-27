/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simprediction;

import java.util.ArrayList;
import nl.utwente.ewi.caes.tactiletriana.App;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author mickvdv
 */
public class SimulationPrediction extends Simulation {
    private static SimulationPrediction instance;
    private Simulation mainSimulation;
    
    
    ArrayList<HousePrediction> housePredictors;
    public SimulationPrediction(Simulation mainSimulation){
        super();
        this.mainSimulation = mainSimulation;
        
        // Koppelhuizen maken
        housePredictors = new ArrayList<>();
        
        
    }
    
    /*
    public static SimulationPrediction getInstance() {
      if(instance == null) {
         instance = new SimulationPrediction();
      }
      return instance;
   }*/
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author niels
 */
public class BufferTimeShiftable extends DeviceBase{
    
    //Capacity in W/h
    private double capacity = 85000; //FIXME add other car models (currently Tesla)
    private double currentCharge = 0;
    
    public BufferTimeShiftable(Simulation simulation) {
        super(simulation, "BufferTimeShiftable");
    }
    
    
    @Override
    public void tick(Simulation simulation, boolean connected) {
        super.tick(simulation, connected);

        //Set the current consumption according to current temperature, radiation and time
        setCurrentConsumption(getCurrentConsumption(simulation.getCurrentTime()));
        System.out.println(currentCharge);
    }
    
    
    public double getCurrentConsumption(LocalDateTime date){
        int h = date.getHour();
        double result = 0;
        if (((0 <= h && h <= 8) || ( 18 <= h && h <= 23)) && !isCharged()){
            result = 7500;
            chargeBuffer(result,SimulationConfig.SIMULATION_TICK_TIME);
        //Drain battery during day
        } else if (!(0 <= h && h <= 8) || ( 18 <= h && h <= 23)){
            chargeBuffer(-7500,SimulationConfig.SIMULATION_TICK_TIME);
        }
        return result;
    }
    
    
    public void chargeBuffer(double W, double timestep){
        if (!isCharged()){
            currentCharge = currentCharge + W*(timestep/60);
        }
        //Make sure charge doesn't exceed capacity and doesn't get below zero
        if (currentCharge > capacity){
            currentCharge = capacity;
        } else if (currentCharge < 0){
            currentCharge = 0;
        }
    }
    
    public boolean isCharged(){
        return currentCharge == capacity;
    }
            
}

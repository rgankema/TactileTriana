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
    private double capacity; 
    //Current charge of the buffer in W
    private double currentCharge = 0;
    //Amount of power this buffer consumes in W
    private double chargingPower;
    //Name of this BufferTimeShiftable
    private String modelName;
    
    /**
     * Constructs a BufferTimeShiftable device (electric vechicle). The model is determined by the modelNumber parameter.
     * @param simulation The simulation object of the current simulation.
     * @param modelNumber This parameter determines which model of electric vechicles is instantiated. Each model has a different capacity and charging power. 
                          0 = Tesla model S, 1 = Audi A3 E-tron, 2 = Ford C-Max, 3 = Volkswagen e-Golf, 4 = BMW i3
     */
    public BufferTimeShiftable(Simulation simulation, int modelNumber) {
        super(simulation,"BufferTimeShiftable");
        if (modelNumber == 0){
            this.modelName = "Tesla Model S";
            this.capacity = 85000;
            this.chargingPower = 20000;
        } else if (modelNumber == 1){
            this.modelName = "Audi A3 E-tron";
            this.capacity = 8800;
            this.chargingPower = 3700;
        } else if (modelNumber == 2){
            this.modelName = "Ford C-Max";
            this.capacity = 7500;
            this.chargingPower = 3700;
        } else if (modelNumber == 3){
            this.modelName = "Volkswagen e-Golf";
            this.capacity = 24000;
            this.chargingPower = 3700;
        } else if (modelNumber == 4){
            this.modelName = "BMW i3";
            this.capacity = 125000;
            this.chargingPower = 7400;
        }
    }
    
    
    @Override
    public void tick(Simulation simulation, boolean connected) {
        super.tick(simulation, connected);

        //Set the current consumption according to current temperature, radiation and time
        setCurrentConsumption(getCurrentConsumption(simulation.getCurrentTime()));
    }
    
    
    public double getCurrentConsumption(LocalDateTime date){
        int h = date.getHour();
        double result = 0;
        //Only charge on non-work hours
        //FIXME change implementation for profiles with triana and stuff
        if (((0 <= h && h <= 8) || ( 18 <= h && h <= 23)) && !isCharged()){
            result = chargingPower;
            chargeBuffer(result,SimulationConfig.SIMULATION_TICK_TIME);
        //Drain battery during day
        } else if (!(0 <= h && h <= 8) || ( 18 <= h && h <= 23)){
            //FIXME do something else instead of draining with 10KW
            chargeBuffer(-10000,SimulationConfig.SIMULATION_TICK_TIME);
        }
        return result;
    }
    
    //Charge the buffer with an amount of W, can also be negative (draining the battery)
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
    
    //Returns if the battery is charged or not
    public boolean isCharged(){
        return currentCharge == capacity;
    }
        
    public String getModelName() {
        return modelName;
    }
    
            
}

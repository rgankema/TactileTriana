/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.*;

/**
 *
 * @author niels
 */
public abstract class TimeShiftable extends DeviceBase {
        
    //FIXME implement possibility of multiple start and endtimes
    private int startTime = 8;
    private int endTime = 13;
    protected double[] programUsage;
    private int currentMinute;

    public TimeShiftable(Simulation simulation, String displayName) {
        super(simulation, displayName);
    }
    
    
    @Override
    public void tick (Simulation simulation, boolean connected){
        super.tick(simulation,connected);
        
        setCurrentConsumption(getCurrentConsumption(simulation.getCurrentTime()));
    }

    public double getCurrentConsumption(LocalDateTime currentTime){
        double result = 0;
        int h = currentTime.getHour();
        if (startTime <= h && h <= endTime){
            result = getCurrentConsumptionInProgram();
        }
        return result;        
    }

    
    //Returns the average consumption that was consumed in the timestep.
    public double getCurrentConsumptionInProgram(){
        double result = 0;
        //Collect #timestep usages, if currentMinute >= programUsage.length, then program is done
        for (int i=currentMinute; i < programUsage.length && i < currentMinute+SimulationConfig.SIMULATION_TICK_TIME; i++){
            result = result + programUsage[i];
        }
        currentMinute = currentMinute+SimulationConfig.SIMULATION_TICK_TIME;
        //Calculate the average of the #timestep usages
        result = result / SimulationConfig.SIMULATION_TICK_TIME;
        return result;
    }   
    
}



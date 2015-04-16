/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author niels
 */
public class BufferTimeShiftable extends DeviceBase{

    public BufferTimeShiftable(Simulation simulation) {
        super("BufferTimeShiftable", simulation);
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
        if ((0 <= h && h <= 8) || ( 18 <= h && h <= 23)){
            result = 1000;
        }
        return result;
    }
    
    
    
}

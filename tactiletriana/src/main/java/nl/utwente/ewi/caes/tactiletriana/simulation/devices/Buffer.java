/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author mickvdv
 */
public class Buffer extends BufferBase {
    
    public Buffer(Simulation simulation) {
        super(simulation, "Buffer");
    }

    @Override
    public void tick(double timePassed, boolean connected) {
        super.tick(timePassed, connected);
        
        // Calculate state of charge change based on previous consumption
        double deltaHours = timePassed / 60d;
        double deltaSOC = getCurrentConsumption() * deltaHours;
        setStateOfCharge(getStateOfCharge() - deltaSOC);
        
        LocalDateTime currentTime = getSimulation().getCurrentTime();
        
        double consumption;
        // If no planning available, help out parent house
        if (simulation.getController() == null || simulation.getController().getPlannedConsumption(this, currentTime) == null) {
            // Likely to change, tick time is probably going to be variable
            int timestep = SimulationConfig.SIMULATION_TICK_TIME;

            consumption = -getParentHouse().getCurrentConsumption();
            // The house is producing energy, so consume
            if (consumption > 0) {
                if (consumption > this.getMaxPower()) {
                    consumption = this.getMaxPower();
                }
                // Don't charge if already at max capacity
                if (getStateOfCharge() == getCapacity()) {
                    consumption = 0;
                }
            }
            // The house is consuming energy, so produce
            else if (consumption < 0) {
                if (consumption < -this.getMaxPower()){
                    consumption = -this.getMaxPower();
                }
                // Don't produce more energy than available
                if ( (getStateOfCharge() - (consumption * timestep)) < 0){
                    consumption = -this.getStateOfCharge() / timestep;
                }
            }
        } else {
            consumption = simulation.getController().getPlannedConsumption(this, currentTime);
        }
        this.setCurrentConsumption(consumption);
    }
}

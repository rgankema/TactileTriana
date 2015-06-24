/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.WashingMachineData;

/**
 * Class that simulates a washing machine based on a realistic profile.
 */
public class WashingMachine extends TimeShiftableBase {
    
    /**
     * Instantiates a WashingMachine object. Using the consumption profile provided
     * by {@link WashingMachineData}.
     * @param simulation The object of the current running simulation.
     */
    public WashingMachine(SimulationBase simulation) {
        super(simulation, "WashingMachine", WashingMachineData.getInstance().getProfile());
    }
}

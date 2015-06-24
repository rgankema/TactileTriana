/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.DishWasherData;

/**
 * Class that simulates a dishwasher based on a realistic profile.
 * @author niels
 */
public class DishWasher extends TimeShiftableBase {
    
    /**
     * Instantiates a DishWasher object. Using the consumption profile provided
     * by {@link DishWasherData}.
     * @param simulation The object of the current running simulation.
     */
    public DishWasher(SimulationBase simulation) {
        super(simulation, "DishWasher", DishWasherData.getInstance().getProfile());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.DishWasherData;

/**
 *
 * @author niels
 */
public class DishWasher extends TimeShiftableBase {        

    public DishWasher(SimulationBase simulation){
        super(simulation, "DishWasher", DishWasherData.getInstance().getProfile());
    }
}

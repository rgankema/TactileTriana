/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.WashingMachineData;

/**
 *
 * @author niels
 */
public class WashingMachine extends TimeShiftableBase{        

    public WashingMachine(Simulation simulation){
        super(simulation, "WashingMachine", WashingMachineData.getInstance().getProfile());
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author niels
 */
public abstract class BufferTimeShiftableBase extends BufferBase {
    
    /**
     * Constructs a BufferTimeShiftable device.
     * @param simulation The simulation object of the current simulation.
     * @param displayName The name of the device as shown to the user.
     */
    public BufferTimeShiftableBase(Simulation simulation, String displayName) {
        super(simulation, displayName, "BufferTimeShiftable");
        
        // register properties
        
    }
    
    // TODO: Insert start/end time properties
    
}

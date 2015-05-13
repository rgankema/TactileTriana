/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Richard
 */
public interface IController {

    /**
     * Returns the amount of power that the device should consume at a given
     * time. May return <code>null</code>, in which case the Controller does not
     * have a plan for that device at the given moment.
     *
     * @param device The device that needs to know its planned consumption
     * @param time The time for which it needs to know its consumption
     * @return The amount of power the device may consume. <code> null
     * </code> if there is no planning available.
     */
    public Double getPlannedConsumption(DeviceBase device, LocalDateTime time);
    
    /**
     * Updates the map of planned consumptions for a device. 
     * The HashMap given in the argument is used to determine the planned consumption on a given time in the simulation for a device.
     * 
     * @param device The DeviceBase for which the planning is updated
     * @param planning 
     */    
    public void updatePlannedConsumption(DeviceBase device, HashMap<LocalDateTime, Double> planning);
    
    /**
     * Indicates whether a TimeShiftable Device is planned to start at the given time.
     * 
     * @param time Time to check if the Device is starting on
     * @param device The Device that is checked for a planning
     * @return True if the TimeShiftable Device is planned to start on the given time, false otherwise
     */    
    public boolean plannedToStart(DeviceBase device, LocalDateTime time); 
    
    /**
     * Update the time a TimeShiftable Device is planned to start.
     * 
     * @param device The Device which receives the planning
     * @param time The time the Device will start.
     */
    public void updatePlannedToStart(DeviceBase device, ArrayList<LocalDateTime> time);
    
    /**
     * Update the plans for all Devices from the backend.
     * 
     * @param timeout Time to wait on the backend to finish retrieving the plans. 
     * @param time Simulation time at which will be recorded as the time the plannings where last updated.
     * @return 
     */
    public boolean retrievePlanning(int timeout, LocalDateTime time);
    
    /**
     * Returns the time the plannings where last updated.
     * 
     * @return 
     */
    public LocalDateTime lastPlanningTime();
    
    public LocalDateTime lastRequestedPlanningTime();
    
    
    
}

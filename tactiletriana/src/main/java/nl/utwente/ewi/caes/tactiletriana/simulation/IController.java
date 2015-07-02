/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;

/**
 * Interface for classes that want to control the simulation by giving power
 * consumption plans to devices.
 * 
 * @author Richard
 *
 */
public interface IController {

    /**
     * Update the plans for all Devices from the backend.
     *
     * @param timeout   Time to wait on the backend to finish retrieving the
     * plans.
     * @param time Simulation time at which will be recorded as the time the
     * plannings where last updated.
     * @return if new planning has been received.
     */
    public boolean retrievePlanning(int timeout, LocalDateTime time);

    /**
     * Returns the time the planning was last updated.
     * @return the time the planning was last updated
     */
    public LocalDateTime getLastPlanningTime();

    /**
     * Returns the time when the last submit planning request has been sent.
     * @return time the last submit planning request.
     */
    public LocalDateTime getLastRequestedPlanningTime();

}

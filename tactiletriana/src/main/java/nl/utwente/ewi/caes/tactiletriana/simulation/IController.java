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
 * TODO: Jan Harm
 *
 */
public interface IController {

    /**
     * Update the plans for all Devices from the backend.
     *
     * @param timeout Time to wait on the backend to finish retrieving the
     * plans.
     * @param time Simulation time at which will be recorded as the time the
     * plannings where last updated.
     * @return if new planning has been received.
     */
    public boolean retrievePlanning(int timeout, LocalDateTime time);

    /**
     * Returns the time the planning was last updated.
     * @return the time the palling was last updated
     */
    public LocalDateTime lastPlanningTime();

    /**
     * Returns the time when the last submit planning request has been sent.
     * @return time the last submit planning request.
     */
    public LocalDateTime lastRequestedPlanningTime();

}

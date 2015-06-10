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
     * Update the plans for all Devices from the backend.
     *
     * @param timeout Time to wait on the backend to finish retrieving the
     * plans.
     * @param time Simulation time at which will be recorded as the time the
     * plannings where last updated.
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;

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
}

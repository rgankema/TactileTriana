/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.data;

import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;

/**
 * An interface for objects that returns power consumption profiles for devices.
 *
 * @author Richard
 * @param <T> the type of device that needs data
 */
public interface IDeviceDataProvider<T extends DeviceBase> {

    /**
     * Returns a profile for the device. This profile may be random, so two
     * calls of this method might not return the same profile.
     *
     * @return a power consumption profile
     */
    public double[] getProfile();

    /**
     * Returns a profile for the device, given a key. During the execution of
     * the program, the same profile will be returned for the same key. However,
     * two different keys may still return the same profile.
     *
     * @param key a key that maps to a certain profile
     * @return a power consumption profile
     */
    public double[] getProfile(Object key);
}

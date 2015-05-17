/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.data;

/**
 * Interface for an object that provides weather profiles. A profile is an array 
 * where each entry represents the average value of an entity for the duration 
 * of a time step.
 * 
 * For example: if a time step lasts 5 minutes, and {@code float[] temperature}
 * is a profile of the temperature in one year, then {@code temperature[0]} is
 * the average temperature for the first 5 minutes of the year.
 * 
 * @author Richard
 */
public interface IWeatherDataProvider {
    /**
     * @return the latitude that corresponds with the weather profiles
     */
    public double getLatitude();
    
    /**
     * @return the longitude that corresponds with the weather profiles
     */
    public double getLongitude();
    
    /**
     * @return the temperature profile for one year
     */
    public double[] getTemperatureProfile();
    
    /**
     * @return the radiance profile for one year
     */
    public double[] getRadianceProfile();
}

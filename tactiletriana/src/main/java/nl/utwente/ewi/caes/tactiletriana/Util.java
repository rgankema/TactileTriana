/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Collection of static helper methods and constants.
 * 
 * @author Richard
 */
public class Util {
    private static final Random random = new Random();
    
    /**
     * The total amount of ticks that make up one year.
     */
    public static final int TOTAL_TICKS_IN_YEAR;
    static {
        int tickMinutes = SimulationConfig.TICK_MINUTES;
        TOTAL_TICKS_IN_YEAR = (365 * 24 * 60 % tickMinutes == 0 ) 
                ? 365 * 24 * 60 / tickMinutes 
                : 365 * 24 * 60 / tickMinutes + 1;
    }
    
    /**
     * Returns a random integer between 0 (inclusive) and range (exclusive)
     * @param range the maximum integer
     * @return the random integer
     */
    public static int nextRandomInt(int range) {
        return random.nextInt(range);
    }
    
    /**
     * Returns the minute of the year the specified LocalDateTime is in.
     * 
     * @param localDateTime the LocalDateTime to get the minute of
     * @return the minute of the year
     */
    public static int toMinuteOfYear(LocalDateTime localDateTime) {
        return localDateTime.getDayOfYear() * 24 * 60 + localDateTime.getHour() * 60 + localDateTime.getMinute();
    }
    
    /**
     * Returns the time step of the year the specified LocalDateTime is in.
     * 
     * @param localDateTime the LocalDateTime to get the time step of
     * @return the time step of the year
     */
    public static int toTimeStep(LocalDateTime localDateTime) {
        return toMinuteOfYear(localDateTime) / SimulationConfig.TICK_MINUTES;
    }
}

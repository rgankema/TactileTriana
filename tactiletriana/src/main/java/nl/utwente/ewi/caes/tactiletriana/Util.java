/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.time.DayOfWeek;
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
        int tickMinutes = TrianaSettings.TICK_MINUTES;
        TOTAL_TICKS_IN_YEAR = (365 * 24 * 60 % tickMinutes == 0)
                ? 365 * 24 * 60 / tickMinutes
                : 365 * 24 * 60 / tickMinutes + 1;
    }

    /**
     * Returns a random integer between 0 (inclusive) and range (exclusive)
     *
     * @param range the maximum integer
     * @return the random integer
     */
    public static int nextRandomInt(int range) {
        return random.nextInt(range);
    }

    /**
     * Determines if the given date is in the weekend.
     *
     * @param time The date for which it has to be determined whether it is in
     * the weekend
     * @return {@code true} if the day of the week is Saturday or Sunday,
     * {@code false} otherwise.
     */
    public static boolean isWeekend(LocalDateTime time) {
        DayOfWeek dow = time.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
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
        return toMinuteOfYear(localDateTime) / TrianaSettings.TICK_MINUTES;
    }

    /**
     * Converts the given minute of the year to a LocalDateTime with year 2014.
     *
     * @param minuteOfYear the minute of the year
     * @return the LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(int minuteOfYear) {
        return LocalDateTime.of(2014, 0, 0, 0, 0).plusMinutes(minuteOfYear);
    }

    /**
     * Returns a string formatted as hh:mm where hh is the the amount of hours
     * that fit in the total amount of minutes, and mm the remaining amount of
     * minutes
     *
     * @param totalMinutes the total of amount to minutes to convert to a string
     * @return the formatted string
     */
    public static String minutesToTimeString(int totalMinutes) {
        int minutePart = (totalMinutes % 60);
        int hourPart = ((totalMinutes - minutePart) / 60) % 24;
        return String.format("%02d:%02d", hourPart, minutePart);
    }
}

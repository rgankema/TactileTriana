/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Richard
 */
public final class TimeScenario {
    private final List<TimeSpan> timeSpans;
    private final List<Consumer<TimeSpan>> callbacks;
    
    /**
     * Creates a new TimeScenario
     */
    public TimeScenario() {
        this.timeSpans = new ArrayList<>();
        this.callbacks = new ArrayList<>();
    }
    
    /**
     * Adds a new TimeSpan to the scenario. The new time must start later than
     * the last time span in this scenario.
     * 
     * @param timeSpan the time span to add
     */
    public void add(TimeSpan timeSpan) {
        if (timeSpans.isEmpty()) {
            timeSpans.add(timeSpan);
        } else {
            TimeSpan last = timeSpans.get(timeSpans.size() - 1);
            if (last.end.isAfter(timeSpan.start)) {
                throw new IllegalArgumentException("Specified timespan must start after the end of the last added timespan");
            }
            timeSpans.add(timeSpan);
        }
    }
    
    /**
     * 
     * @return the current time in the scenario
     */
    public LocalDateTime getStart() {
        return timeSpans.get(0).start;
    }
    
    /**
     * Calculates the next time in the scenario given a current time and an amount of minutes to
     * increase that time by.
     * 
     * @param currentTime the base time that should be increased
     * @param deltaMinutes the amount of minutes to increase the time by
     * @return the next time. {@code null} if no time spans have been added yet
     */
    public LocalDateTime getNext(LocalDateTime currentTime, int deltaMinutes) {
        // TimeScenario isn't properly initialised
        if (timeSpans.isEmpty()) {
            throw new IllegalStateException("No time spans have been added yet");
        }
        // Find time span we're currently in
        int index = 0;
        for (index = 0; index < timeSpans.size(); index++) {
            if (!timeSpans.get(index).start.isAfter(currentTime) && !currentTime.isAfter(timeSpans.get(index).end)) {
                break;
            }
        }
        // Could not find the time span, throw exception
        if (index == timeSpans.size()) {
            throw new IllegalArgumentException("Specified time is not in any of the time spans");
        }
        // If the next time is after the current time span, start the next time span
        LocalDateTime nextTime = currentTime.plusMinutes(deltaMinutes);
        if (nextTime.isAfter(timeSpans.get(index).end)) {
            index++;
            if (index == timeSpans.size()) {
                index = 0;
            }
            nextTime = timeSpans.get(index).start;
            for (Consumer c : callbacks) {
                c.accept(timeSpans.get(index));
            }
        }
        return nextTime;
    }
    
    /**
     * Adds a consumer that is called each time a new time span has started
     * 
     * @param callback the consumer
     */
    public void addNewTimeSpanStartedCallback(Consumer<TimeSpan> callback) {
        callbacks.add(callback);
    }
    
    /**
     * Stops calling a given consumer when a new time span starts.
     * 
     * @param callback the consumer
     */
    public void removeNewTimeSpanStartedCallback(Consumer<TimeSpan> callback) {
        callbacks.remove(callback);
    }
    
    /**
     * Represents a time span between two dates
     */
    public static final class TimeSpan {
        private final LocalDateTime start;
        private final LocalDateTime end;
        
        public TimeSpan(LocalDateTime start, LocalDateTime end) {
            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Start must be before end");
            }
            
            this.start = start;
            this.end = end;
        }
        
        public LocalDateTime getStart() {
            return this.start;
        }
        
        public LocalDateTime getEnd() {
            return this.end;
        }
    }
}

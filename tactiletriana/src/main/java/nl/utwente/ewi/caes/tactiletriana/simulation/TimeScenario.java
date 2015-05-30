/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Richard
 */
public final class TimeScenario {
    
    // STATIC METHODS
    
    /**
     * Parse a representation of a TimeScenario:
     * Format: DateBegin,DateEnd|DateBegin,DateEnd| etc
     * @param input
     * @return 
     */
    public static TimeScenario parse(String input){
        String[] tokens = input.split("[|]");
        TimeSpan[] timeSpans = new TimeSpan[tokens.length];
        for(int i = 0; i < tokens.length; i++){
            String ts = tokens[i];
            String[] data = ts.split(",");
            LocalDate begin = LocalDate.parse(data[0]);
            LocalDate end = LocalDate.parse(data[1]);
            TimeSpan timeSpan = new TimeSpan(begin, end);
            timeSpans[i] = timeSpan;
        }
        return new TimeScenario(timeSpans);
    }
    
    // MEMBER FIELDS
    
    private final List<TimeSpan> timeSpans;
    
    private LocalDateTime currentTime;
    private int timeSpanIndex = 0;
    
    /**
     * Creates a new TimeScenario
     * 
     * @param timeSpans the time spans the scenario consists of
     */
    public TimeScenario(TimeSpan... timeSpans) {
        if (timeSpans.length == 0) throw new IllegalArgumentException("TimeScenario requires at least one TimeSpan");
        
        this.timeSpans = new ArrayList<>();
        
        for (TimeSpan timeSpan : timeSpans) {
            if (this.timeSpans.isEmpty()) {
                this.timeSpans.add(timeSpan);
            } else {
                TimeSpan last = this.timeSpans.get(this.timeSpans.size() - 1);
                if (last.end.isAfter(timeSpan.start)) {
                    throw new IllegalArgumentException("Specified timespan must start after the end of the last added timespan");
                }
                this.timeSpans.add(timeSpan);
            }
        }
        
        currentTime = LocalDateTime.of(timeSpans[0].start, LocalTime.MIN);
    }
    
    // PROPERTIES
    
    public List<TimeSpan> getTimeSpans(){
        return timeSpans;
    }
    
    /**
     * 
     * @return the current time in the scenario
     */
    public LocalDateTime getCurrentTime() {
        return currentTime;
    }
    
    
    // METHODS
    
    public void reset() {
        currentTime = LocalDateTime.of(timeSpans.get(0).start, LocalTime.MIN);
    }
    
    /**
     * Shifts the time in the scenario given a current time and an amount of minutes to
     * increase that time by.
     * 
     * @param deltaMinutes the amount of minutes to increase the time by
     * @return whether the next time was in a different time span
     */
    public boolean next(int deltaMinutes) {
        
        // If the next time is after the current time span, start the next time span
        currentTime = currentTime.plusMinutes(deltaMinutes);
        if (currentTime.toLocalDate().isAfter(timeSpans.get(timeSpanIndex).end)) {
            timeSpanIndex++;
            if (timeSpanIndex == timeSpans.size()) {
                timeSpanIndex = 0;
            }
            currentTime = LocalDateTime.of(timeSpans.get(timeSpanIndex).start, LocalTime.MIN);
            return true;
        }
        return false;
    }
    
    @Override
    public String toString(){
        String output = "";
        for (TimeSpan s : this.timeSpans){
            output = output + s.getStart() + "," + s.getEnd() + "|";
        }
        return output;
    }
    
    // NESTED CLASSES
    
    /**
     * Represents a time span between two dates
     */
    public static final class TimeSpan {
        private final LocalDate start;
        private final LocalDate end;
        
        public TimeSpan(LocalDate start, LocalDate end) {
            if (start.isAfter(end)) {
                throw new IllegalArgumentException("Start may not be after end");
            }
            
            this.start = start;
            this.end = end;
        }
        
        public LocalDate getStart() {
            return this.start;
        }
        
        public LocalDate getEnd() {
            return this.end;
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Base class for TimeShiftables.
 */
public abstract class TimeShiftableBase extends DeviceBase {
    public static final String API_TIMES = "times";
    public static final String API_STATIC_PROFILE = "static_profile";
    public static final String API_STARTED_ON = "started_on";
    public static final String API_TS_PLANNING = "ts_planning";
    
    private int currentTimeStep = 0;   // The step in the program at which the device currently is
    private boolean active;            // The device may now do its program
    private boolean programRemaining;  // Whether the device still has a program to do for the day (only used in "dumb" mode)
    
    /**
     * Constructs a new TimeShiftableBase.
     * 
     * @param simulation    The Simulation this device belongs to
     * @param displayName   The name of the device as shown to the user
     * @param profile       The consumption profile of the device, with a
     *                      consumption value for every tick
     */
    public TimeShiftableBase(SimulationBase simulation, String displayName, double[] profile) {
        super(simulation, displayName, "TimeShiftable");
        
        setStaticProfile(profile);
        this.programRemaining = true;
        
        // Register properties for API
        registerAPIProperty(API_TIMES);
        registerAPIProperty(API_STATIC_PROFILE);
        registerAPIProperty(API_STARTED_ON);
        registerAPIProperty(API_TS_PLANNING);
        
        // Register properties for prediction
        registerProperty(startTime);
        registerProperty(endTime);
        registerProperty(staticProfile);
        registerProperty(planning);
    }
    
    // PROPERTIES
    
    /**
     * The consumption in watt for every time step from start to finish.
     */
    private final ObjectProperty<double[]> staticProfile = new SimpleObjectProperty<>();
    
    public ObjectProperty<double[]> staticProfileProperty() {
        return this.staticProfile;
    }
    
    public final double[] getStaticProfile() {
        return staticProfileProperty().get();
    }
    
    protected final void setStaticProfile(double[] profile) {
        this.staticProfile.set(profile);
    }
    
    /**
     * A list of times when the device is scheduled to start its program.
     */
    private final ObjectProperty<ObservableList<LocalDateTime>> planning = new SimpleObjectProperty<>();
    
    public ObjectProperty<ObservableList<LocalDateTime>> planningProperty() {
        return planning;
    }
    
    public final ObservableList<LocalDateTime> getPlanning() {
        return planningProperty().get();
    }
    
    public final void setPlanning(ObservableList<LocalDateTime> planning) {
        planningProperty().set(planning);
    }
    
    /**
     * The time (in minutes from the start of the day) from which point the device may start operating
     */
    private final IntegerProperty startTime = new SimpleIntegerProperty();
    
    public IntegerProperty startTimeProperty() {
        return startTime;
    }
    
    public int getStartTime() {
        return startTimeProperty().get();
    }

    public void setStartTime(int start) {
        startTimeProperty().set(start);
    }
    
    /**
     * Last moment in minutes that the device may start its program
     */
    private final IntegerProperty endTime = new SimpleIntegerProperty();
    
    public IntegerProperty endTimeProperty() {
        return endTime;
    }
    
    public final int getEndTime() {
        return endTime.get();
    }

    public final void setEndTime(int endTime) {
        this.endTime.set(endTime);
    }
    
    /**
     * Last time this device started its program, absolute date.
     */
    protected final ReadOnlyObjectWrapper<LocalDateTime> startedOn = new ReadOnlyObjectWrapper<>();
    
    public ReadOnlyObjectProperty<LocalDateTime> startedOnProperty() {
        return startedOn.getReadOnlyProperty();
    }
    
    public final LocalDateTime getStartedOn() {
        return startedOn.get();
    }
    
    protected final void setStartedOn(LocalDateTime time) {
        startedOn.set(time);
    }
    
    // METHODS
    
    @Override
    public void tick(boolean connected){
        super.tick(connected);
        
        // Check if we should start now
        LocalDateTime currentDateTime = getSimulation().getCurrentTime();
        if (getPlanning().size() > 0) {
            LocalDateTime nextPlannedStart = getPlanning().get(0);
            if (!nextPlannedStart.isAfter(currentDateTime)) {
                getPlanning().remove(0);
                active = true;
                setStartedOn(currentDateTime);
            }
            
        } else { // No planning available
            int currentTime = currentDateTime.getHour() * 60 + currentDateTime.getMinute();
            
            if (currentTime - SimulationConfig.TICK_MINUTES < 0) {
                programRemaining = true;
            }
            
            // If not done for this period, check if we may start
            if (!active && programRemaining) {
                if (currentTime >= getStartTime() || 
                        // Relevant if start time starts somewhere at the end of the day
                        currentTime - SimulationConfig.TICK_MINUTES <= 0) {
                    if (currentTime <= getEndTime() || getEndTime() < getStartTime()) {
                        active = true;
                    }
                }
            }
        }
        
        // If active, consume energy until done
        if (active) {
            double consumption = getStaticProfile()[currentTimeStep];
            currentTimeStep++;
            if (currentTimeStep >= getStaticProfile().length) {
                currentTimeStep = 0;
                active = false;
                programRemaining = false;
            }
            setCurrentConsumption(consumption);
        }
    }
    
    // Static profile won't change during the course of the Simulation, so only
    // make this once.
    private JSONArray staticProfileJSON;
    
    @Override
    public JSONObject parametersToJSON() {
        JSONObject result = new JSONObject();
        
        // Build static profile
        if (staticProfileJSON == null) {
            staticProfileJSON = new JSONArray();
            for (int i = 0; i < getStaticProfile().length; i++) {
                staticProfileJSON.add(getStaticProfile()[i]);
            }
        }
        
        // Build times
        JSONObject interval = new JSONObject();
        interval.put("start_time", getStartTime());
        interval.put("end_time", getEndTime());
        JSONArray times = new JSONArray();
        times.add(interval);
        
        // Build ts planning
        JSONArray tsPlanning = new JSONArray();
        for (LocalDateTime time : getPlanning()) {
            tsPlanning.add(toMinuteOfYear(time));
        }
        
        result.put(API_STATIC_PROFILE, staticProfileJSON);
        result.put(API_TIMES, times);
        result.put(API_STARTED_ON, toMinuteOfYear(getStartedOn()));
        result.put(API_TS_PLANNING, tsPlanning);
        return result;
    }
}



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
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
    
    private final ObservableList<LocalDateTime> planning;
    
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
        
        this.planning = FXCollections.observableArrayList();
        setStaticProfile(profile);
        this.programRemaining = true;
        
        // Register properties for API
        registerAPIParameter(API_TIMES);
        registerAPIParameter(API_STATIC_PROFILE);
        registerAPIParameter(API_STARTED_ON);
        registerAPIParameter(API_TS_PLANNING);
        
        // Register properties for prediction
        registerProperty(startTime);
        registerProperty(endTime);
        registerProperty(staticProfile);
        registerList(planning);
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
     * @return a list of times when the device is scheduled to start its program.
     */
    public final ObservableList<LocalDateTime> getPlanning() {
        return planning;
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
    public void doTick(boolean connected){
        // Check if we should start now
        LocalDateTime currentDateTime = simulation.getCurrentTime();
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
    
    @Override
    public void updateParameter(String parameterName, Object value) {
        
        //Check if the parameter is in the available parameters list.
        Set<String> availableParameters = this.getAPIProperties();
        if(availableParameters.contains(parameterName)) {
            //Parse the parameter value based on the parameterName
            switch (parameterName) {
                case API_TIMES:
                    //TODO fix dit als duidelijk is of in de API ook maar 1 starttijd en eindtijd wordt gebruikt
                    break;
                case API_STATIC_PROFILE:
                    //The JSON format of a static_profile is [double,double,...] with the doubles being consumption values
                    //Cast the value to the type it should be. Value is actually a JSONArray which is a subclass of ArrayList
                    List<Double> profile = (List<Double>) value;
                    Double[] profileArray = profile.toArray(new Double[0]);
                    //TODO fix this, always use ArrayList<Double> or something for the profile property
                    //converting from a double[] to Double[] cannot be done automatically, UGH
                    //
                    double[] profileArray2 = Stream.of(profileArray).mapToDouble(Double::doubleValue).toArray();
                    setStaticProfile(profileArray2);
                    break;
                case API_STARTED_ON:
                    //The value for this parameter is an Integer respresenting the minutes since the start of the simulation
                    int startedOnMinutes = (Integer) value;
                    //Convert the time in minutes since the start of the simulation to a LocalDateTime
                    LocalDateTime startedOn = LocalDateTime.of(2014, 1, 1, 0, 0).plusMinutes(startedOnMinutes);
                    setStartedOn(startedOn);
                    break;
                case API_TS_PLANNING:
                    //The value should be an ArrayList of integers representing minutes since the start of the simulation
                    List<Integer> timesInt = (ArrayList<Integer>) value;
                    //Update planning
                    for(Integer time: timesInt) {
                        getPlanning().add(LocalDateTime.of(2014,1,1,0,0).plusMinutes(time));
                    }
                    break;
            }
            
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



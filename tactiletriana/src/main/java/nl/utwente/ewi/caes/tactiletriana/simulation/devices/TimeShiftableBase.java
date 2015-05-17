/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.*;

/**
 * Base class for TimeShiftables.
 * 
 * Has the following properties as specified in the API:
 * <ul>
 *  <li>startTimes</li>
 *  <li>endTimes</li>
 *  <li>static_profile</li>
 * </ul>
 * 
 * Note that the start and end times are in fact not lists, but doubles, because
 * the simulation doesn't support multiple start end end times as of yet.
 */
public abstract class TimeShiftableBase extends DeviceBase {
        
    private int currentMinute = 0;     // The step in the program at which the device currently is
    private boolean active;            // The device may now do its program
    private boolean programRemaining;  // Whether the device still has a program to do for the day
    
    /**
     * Constructs a new TimeShiftableBase. Registers the {@code startTimes},
     * {@code endTimes} and {@code static_profile} properties as specified in the API.
     * 
     * @param simulation    The Simulation this device belongs to
     * @param displayName   The name of the device as shown to the user
     * @param profile       The consumption profile of the device, with a
     *                      consumption value for every tick
     */
    public TimeShiftableBase(Simulation simulation, String displayName, double[] profile) {
        super(simulation, displayName, "TimeShiftable");
        
        setStaticProfile(profile);
        
        // register properties
        addProperty("startTimes", startTime);
        addProperty("endTimes", endTime);
        addProperty("static_profile", staticProfile);
        
        this.programRemaining = true;
    }
    
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
     * The time (in minutes from the start of the day) from which point the device may start operating
     */
    private final DoubleProperty startTime = new SimpleDoubleProperty();
    
    public DoubleProperty startTimeProperty() {
        return startTime;
    }
    
    public double getStartTime() {
        return startTimeProperty().get();
    }

    public void setStartTime(double start) {
        startTimeProperty().set(start);
    }
    
    
    /**
     * Last moment in minutes that the device may start its program
     */
    protected final DoubleProperty endTime = new SimpleDoubleProperty();
    
    public DoubleProperty endTimeProperty() {
        return endTime;
    }
    
    public final double getEndTime() {
        return endTime.get();
    }

    public final void setEndTime(double endTime) {
        this.endTime.set(endTime);
    }
    
    @Override
    public void tick (boolean connected){
        super.tick(connected);
        
        double consumption = 0;
        
        IController controller = getSimulation().getController();
        LocalDateTime currentDateTime = getSimulation().getCurrentTime();
        if (controller != null && controller.getPlannedConsumption(this, currentDateTime) != null) {
            consumption = controller.getPlannedConsumption(this, currentDateTime);
        } else { // No planning available
            double currentTime = currentDateTime.getHour() * 60 + currentDateTime.getMinute();
            
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
            
            // If active, consume energy until done
            if (active) {
                consumption = getStaticProfile()[currentMinute];
                currentMinute++;
                if (currentMinute >= getStaticProfile().length) {
                    currentMinute = 0;
                    active = false;
                    programRemaining = false;
                }
            }
        }
        
        setCurrentConsumption(consumption);
    }
    
}



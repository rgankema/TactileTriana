/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.*;

/**
 *
 * @author niels
 */
public abstract class TimeShiftable extends DeviceBase {
        
    //The usage in W per minute mapped in an array
    protected double[] programUsage;

    public TimeShiftable(Simulation simulation, String displayName) {
        super(simulation, displayName);
    }
        
    
    //time in h when this timeshiftable is ready to operate (0 <= startTime < 24)
    protected final DoubleProperty startTime = new SimpleDoubleProperty(){
        public void setValue(double value) {
            if (get() == value) {
                return;
            }
            //Can't be smaller than 0
            if (value < 0) {
                value = 0;
            }
            //Can't be bigger than 24
            if (value > 24) {
                value = 24;
            }

            super.set(value);
        } 
    
    };
    
    public double getStartTime() {
        return timeWindow.get();
    }

    public void setStartTime(double start) {
        this.timeWindow.set(start);
    }

    public DoubleProperty startTimeProperty() {
        return startTime;
    }
    
    //window in which the timeshiftable must operate in minutes, endtime = startTime+timeWindow.
    //Must be greater than or equal to programUsage.length.
    protected final DoubleProperty timeWindow = new SimpleDoubleProperty(){
        public void setValue(double value) {
            if (get() == value) {
                return;
            }
            //Can't be smaller than the length of the program
            if (value < programUsage.length) {
                value = programUsage.length;
            }
            //Can't be bigger than a day?
            if (value > 24*60) {
                value = 24*60;
            }

            super.set(value);
        } 
    
    };
    
    public double getTimeWindow() {
        return timeWindow.get();
    }

    public void setTimeWindow(double timewindow) {
        this.timeWindow.set(timewindow);
    }

    public DoubleProperty timeWindowProperty() {
        return timeWindow;
    }
    
    
    @Override
    public void tick (Simulation simulation, boolean connected){
        super.tick(simulation,connected);
        
        setCurrentConsumption(getCurrentConsumption(simulation.getCurrentTime()));
    }

    public double getCurrentConsumption(LocalDateTime currentTime){
        double result = 0;
        int h = currentTime.getHour();
        //reset on new day
        //FIXME: change this when triana controller gets implemented
        if (h == 0){
            currentMinute = 0;
        }
        if (startTime.get() >= h){
            result = getCurrentConsumptionInProgram();
        }
        return result;        
    }
    
    
    private int currentMinute;
    
    //Returns the average consumption that was consumed in the timestep.
    public double getCurrentConsumptionInProgram(){
        double result = 0;
        //Collect #timestep usages, if currentMinute >= programUsage.length, then program is done
        for (int i=currentMinute; i < programUsage.length && i < currentMinute+SimulationConfig.SIMULATION_TICK_TIME; i++){
            result = result + programUsage[i];
        }
        currentMinute = currentMinute+SimulationConfig.SIMULATION_TICK_TIME;
        //Calculate the average of the #timestep usages
        result = result / SimulationConfig.SIMULATION_TICK_TIME;
        return result;
    }   
    
}



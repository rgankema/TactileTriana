/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import static nl.utwente.ewi.caes.tactiletriana.Util.toTimeStep;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.IDeviceDataProvider;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.BufferConverterData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author niels
 */
public class BufferConverter extends DeviceBase{
    
    public static int profileCounter = 0;
    
    //coefficient of performance
    public static final String API_COP = "COP";
    public static final String API_PROFILE = "profile";
    //Number of current profile (0-5)
    private final IntegerProperty profileNumber;
    //Data of the heat demand in W
    private final IDeviceDataProvider<BufferConverter> data;
    
    /**
     *
     * @param profileNumber a number between 0 and 5 (inclusive) which selects
     * the profile data on which this instance is based
     * @param simulation the Simulation this device belongs to
     */
    public BufferConverter(int profileNumber, SimulationBase simulation) {
        super(simulation, "BufferConverter", "BufferConverter");

        if (profileNumber < 0 || profileNumber > 5) {
            throw new IllegalArgumentException("profileNumber must be in the range of 0 to 5");
        }

        this.profileNumber =  new SimpleIntegerProperty();
        this.profileNumber.set(profileNumber);
        this.data = BufferConverterData.getInstance();
        
        // Register API properties
        registerAPIParameter(API_PROFILE);       
        registerAPIParameter(API_COP);
                
        registerProperty(COP);
        registerProperty(this.profileNumber);
        
        //Set default of COP to 4
        setCOP(4);
    }
    
    public BufferConverter(SimulationBase simulation){
        //Make sure profiles cycle
        this(BufferConverter.profileCounter%6 ,simulation);
        profileCounter++;
    }
    
    /**
     * Coefficient of performance of this BufferConverter
     */
    private final DoubleProperty COP = new SimpleDoubleProperty();
    
    public DoubleProperty COPProperty() {
        return COP;
    }
    
    public double getCOP() {
        return COP.get();
    }

    public void setCOP(double power) {
        this.COP.set(power);
    }
    
    @Override
    public void updateParameter(String parameter, Object value){
        if(parameter.equals(API_COP)){
            setCOP((double) value);
        } else {                      
            super.updateParameter(parameter, value);
        }        
    }
    
    @Override
    public void doTick(boolean connected) {
        setCurrentConsumption(data.getProfile(profileNumber.get())[toTimeStep(simulation.getCurrentTime())] / getCOP());
    }

    @Override
    protected JSONObject parametersToJSON() {
        JSONObject result = new JSONObject();
        JSONArray jsonProfile = new JSONArray();
        
        int time = toTimeStep(simulation.getCurrentTime());
        for (int i = time; i < time + 24 * 60 / SimulationConfig.TICK_MINUTES; i++) {
            jsonProfile.add(data.getProfile(profileNumber)[i]);
        }
        result.put(API_PROFILE, jsonProfile);
        return result;
    }
    
}

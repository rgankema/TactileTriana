/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import static nl.utwente.ewi.caes.tactiletriana.Util.TOTAL_TICKS_IN_YEAR;
import static nl.utwente.ewi.caes.tactiletriana.Util.toTimeStep;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.IDeviceDataProvider;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.UncontrollableData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author jd
 */
public class UncontrollableLoad extends DeviceBase {
    public static final String API_PROFILE = "profile";
    
    private final int profileNumber;
    private final IDeviceDataProvider<UncontrollableLoad> data;
    
    /**
     *
     * @param profileNumber a number between 0 and 5 (inclusive) which selects
     * the profile data on which this instance is based
     * @param simulation the Simulation this device belongs to
     */
    public UncontrollableLoad(int profileNumber, SimulationBase simulation) {
        super(simulation, "Uncontrollable Load", "Uncontrollable");

        if (profileNumber < 0 || profileNumber > 5) {
            throw new IllegalArgumentException("profileNumber must be in the range of 0 to 5");
        }

        this.profileNumber = profileNumber;
        this.data = UncontrollableData.getInstance();
        
        // Register API properties
        registerAPIParameter(API_PROFILE);
    }
    
    /**
     * The consumption profile for the coming day
     */
    private final ObjectProperty<double[]> profile = new SimpleObjectProperty<>();
    
    public ObjectProperty<double[]> profileProperty() {
        return profile;
    }
    
    public final double[] getProfile() {
        return profile.get();
    }

    @Override
    public void tick(boolean connected) {
        super.tick(connected);
        
        updateProfile();
        
        setCurrentConsumption(getProfile()[0]);
    }
    
    // Shifts the profile one tick further
    private void updateProfile() {
        LocalDateTime time = simulation.getCurrentTime();
        int timeStep = toTimeStep(time);
        // This is extremely inefficient, but really the only way if you want
        // profile to be a property that can be bound to.
        double[] newProfile = new double[24*60];
        for (int i = 0, j = timeStep; i < 24 * 60; i++, j++) {
            newProfile[i] = data.getProfile(profileNumber)[(j % TOTAL_TICKS_IN_YEAR)];
        }
        profile.set(newProfile);
    }

    @Override
    protected JSONObject parametersToJSON() {
        JSONObject result = new JSONObject();
        JSONArray jsonProfile = new JSONArray();
        for (int i = 0; i < getProfile().length; i++) {
            jsonProfile.add(getProfile()[i]);
        }
        result.put(API_PROFILE, jsonProfile);
        return result;
    }
}

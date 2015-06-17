/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
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

        this.data = UncontrollableData.getInstance();
        this.profileNumber.set(profileNumber);
        
        // Register API properties
        registerAPIParameter(API_PROFILE);
        
        // Register properties for prediction
        registerProperty(this.profileNumber);
    }
    
    // PROPERTIES
    
    private final ReadOnlyIntegerWrapper profileNumber = new ReadOnlyIntegerWrapper();
    
    public ReadOnlyIntegerProperty profileNumberProperty() {
        return profileNumber.getReadOnlyProperty();
    }
    
    public final int getProfileNumber() {
        return profileNumber.get();
    }
    
    // METHODS

    @Override
    public void doTick(boolean connected) {
        setCurrentConsumption(data.getProfile(getProfileNumber())[toTimeStep(simulation.getCurrentTime())]);
    }

    @Override
    protected JSONObject parametersToJSON() {
        JSONObject result = new JSONObject();
        JSONArray jsonProfile = new JSONArray();

        int time = toTimeStep(simulation.getCurrentTime());
        for (int i = time; i < time + 24 * 60 / GlobalSettings.TICK_MINUTES; i++) {
            jsonProfile.add(data.getProfile(getProfileNumber())[i]);
        }
        result.put(API_PROFILE, jsonProfile);
        return result;
    }
}

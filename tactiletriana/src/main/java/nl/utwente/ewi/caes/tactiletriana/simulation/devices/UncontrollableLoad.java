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
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.IDeviceDataProvider;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.UncontrollableData;

/**
 *
 * @author jd
 */
public class UncontrollableLoad extends DeviceBase {

    private final int profileNumber;
    private final IDeviceDataProvider<UncontrollableLoad> data;
    
    /**
     *
     * @param profileNumber a number between 0 and 5 (inclusive) which selects
     * the profile data on which this instance is based
     * @param simulation the Simulation this device belongs to
     */
    public UncontrollableLoad(int profileNumber, Simulation simulation) {
        super(simulation, "Uncontrollable Load", "Uncontrollable");

        if (profileNumber < 0 || profileNumber > 5) {
            throw new IllegalArgumentException("profileNumber must be in the range of 0 to 5");
        }

        this.profileNumber = profileNumber;
        this.data = UncontrollableData.getInstance();
        
        addProperty("profile", profile);
    }
    
    /**
     * The consumption profile for the coming day
     */
    private final ObjectProperty<float[]> profile = new SimpleObjectProperty<>();
    
    public ObjectProperty<float[]> profileProperty() {
        return profile;
    }
    
    public final float[] getProfile() {
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
        float[] newProfile = new float[24*60];
        for (int i = 0, j = timeStep; i < 24 * 60; i++, j++) {
            newProfile[i] = data.getProfile(profileNumber)[(j % TOTAL_TICKS_IN_YEAR)];
        }
        profile.set(newProfile);
    }
}

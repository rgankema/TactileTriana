/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
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
     * @param profileNumber - A number between 0 and 5 (inclusive) which selects
     * the profile data on which this instance is based
     */
    public UncontrollableLoad(int profileNumber, Simulation simulation) {
        super(simulation, "Uncontrollable Load", "Uncontrollable");

        if (profileNumber < 0 || profileNumber > 5) {
            throw new IllegalArgumentException("profileNumber must be in the range of 0 to 5");
        }

        this.profileNumber = profileNumber;
        this.data = UncontrollableData.getInstance();
    }

    @Override
    public void tick(boolean connected) {
        super.tick(connected);
        LocalDateTime t = simulation.getCurrentTime();
        setCurrentConsumption(data.getProfile(profileNumber)[toTimeStep(t)]);
    }
}

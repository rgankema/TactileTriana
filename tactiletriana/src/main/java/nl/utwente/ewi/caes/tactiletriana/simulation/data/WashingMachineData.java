/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import static nl.utwente.ewi.caes.tactiletriana.GlobalSettings.TICK_MINUTES;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.WashingMachine;

/**
 *
 * @author Richard
 */
public class WashingMachineData implements IDeviceDataProvider<WashingMachine> {

    private static double[] profile;

    private static WashingMachineData instance;

    public static WashingMachineData getInstance() {
        if (instance == null) {
            instance = new WashingMachineData();
        }
        return instance;
    }

    private WashingMachineData() {
        double[] minuteProfile = new double[72];

        try (Stream<String> dataset = Files.lines(Paths.get("src/main/resources/datasets/washing_machine_dataset.txt"))) {

            // Wrapper for i, because Java's lambda functions are idiotic
            class Wrapper {

                int value = 0;
            }
            Wrapper i = new Wrapper();
            dataset.forEach(s -> {
                minuteProfile[i.value] = Float.parseFloat(s);
                i.value++;
            });
        } catch (IOException ex) {
            throw new RuntimeException("Error while parsing washing machine dataset");
        }

        // Convert data to tick format rather than minute format
        int profileLength = (minuteProfile.length % TICK_MINUTES == 0)
                ? minuteProfile.length / TICK_MINUTES
                : minuteProfile.length / TICK_MINUTES + 1;
        profile = new double[profileLength];
        for (int i = 0; i < profile.length; i++) {
            profile[i] = 0;
            int minutesPerTick = 0;
            for (int j = i * TICK_MINUTES; j < (i + 1) * TICK_MINUTES && j < minuteProfile.length; j++) {
                profile[i] += minuteProfile[j];
                minutesPerTick++;
            }
            profile[i] /= minutesPerTick;
        }
    }

    @Override
    public double[] getProfile() {
        return profile;
    }

    @Override
    public double[] getProfile(Object key) {
        return profile;
    }

}

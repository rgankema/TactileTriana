/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import static nl.utwente.ewi.caes.tactiletriana.GlobalSettings.TICK_MINUTES;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.DishWasher;

/**
 * Data provider for dishwashers. Uses a consumption profile of a real dishwasher.
 * @author Richard
 */
public class DishWasherData implements IDeviceDataProvider<DishWasher> {

    private static double[] profile;

    private static DishWasherData instance;

    public static DishWasherData getInstance() {
        if (instance == null) {
            instance = new DishWasherData();
        }
        return instance;
    }

    private DishWasherData() {
        float[] minuteProfile = new float[153];

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/datasets/dishwasher_dataset.txt")))) {
            Stream<String> dataset = reader.lines();
            
            // Wrapper for i, because Java's lambda functions are idiotic
            class Wrapper {

                int value = 0;
            }
            Wrapper i = new Wrapper();
            dataset.forEach(s -> {
                minuteProfile[i.value] = Float.parseFloat(s);
                i.value++;
            });
        } catch (Exception ex) {
            throw new RuntimeException("Error while parsing dish washer dataset");
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

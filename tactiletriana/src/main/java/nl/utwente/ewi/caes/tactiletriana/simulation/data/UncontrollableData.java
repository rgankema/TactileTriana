/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import nl.utwente.ewi.caes.tactiletriana.Concurrent;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.UncontrollableLoad;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import static nl.utwente.ewi.caes.tactiletriana.Util.*;

/**
 * Data provider for profiles representing the uncontrollable energy consumption
 * of a house over a year.
 *
 * @author Richard
 */
public final class UncontrollableData implements IDeviceDataProvider<UncontrollableLoad> {

    private static UncontrollableData instance;

    public static UncontrollableData getInstance() {
        if (instance == null) {
            instance = new UncontrollableData();
        }
        return instance;
    }

    private final Map<Integer, double[]> profileByKey = new TreeMap<>();
    private final Future<Void> future;

    private UncontrollableData() {
        // Load profiles async
        future = Concurrent.getExecutorService().submit(() -> {
            loadProfile();
            return null;
        });

    }

    @Override
    public double[] getProfile() {
        return getProfile(nextRandomInt(6));
    }

    @Override
    public double[] getProfile(Object key) {
        try {
            // Wait until profiles are loaded
            future.get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new RuntimeException("Error loading Uncontrollable Load profile");
        }
        return profileByKey.get(key);
    }

    // HELPER METHODS
    private void loadProfile() {
        //Load the profile data into an array from the CSV file containing power consumptions for 6 houses.
        double[][] profiles = new double[6][525608];

        File csvData = new File("src/main/resources/datasets/house_profiles.csv");
        CSVFormat format = CSVFormat.DEFAULT.withDelimiter(';');
        try (CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), format)) {
            for (CSVRecord csvRecord : parser) {
                for (int p = 0; p < 6; p++) {
                    profiles[p][(int) parser.getRecordNumber()] = Double.parseDouble(csvRecord.get(p));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while parsing house profile dataset", e);
        }

        // Convert to profile with value per timestep   
        int tickMinutes = GlobalSettings.TICK_MINUTES;
        int minutesInYear = 365 * 24 * 60;
        int ticksInYear = getTotalTicksInYear();
        
        for (int key = 0; key < 6; key++) {
            double[] profile = new double[ticksInYear];
            // Get average of all minutes in timestep
            for (int ts = 0; ts < ticksInYear; ts++) {
                profile[ts] = 0;
                int minutesInTimeStep = 0;
                for (int min = ts * tickMinutes; min < (ts + 1) * tickMinutes && min < minutesInYear; min++) {
                    profile[ts] += profiles[key][min];
                    minutesInTimeStep++;
                }
                profile[ts] /= minutesInTimeStep;
            }
            profileByKey.put(key, profile);
        }
    }

}

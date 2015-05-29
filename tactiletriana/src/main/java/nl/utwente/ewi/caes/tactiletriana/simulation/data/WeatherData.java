/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.data;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import static nl.utwente.ewi.caes.tactiletriana.Util.TOTAL_TICKS_IN_YEAR;

/**
 * Data provider for the weather profiles. Uses KNMI data from 2014.
 * 
 * @author Richard
 */
public final class WeatherData implements IWeatherDataProvider{
    public static final double LONGITUDE = 6.897;
    public static final double LATITUDE = 52.237;
    
    private static WeatherData instance;
    
    public static WeatherData getInstance() {
        if (instance == null) {
            instance = new WeatherData();
        }
        return instance;
    }
    
    private final double[] temperatureProfile;
    private final double[] radianceProfile;
    
    private WeatherData() {
        // Get data from KNMI
        double[] tempByHour = new double[365*24];
        double[] radianceByHour = new double[365*24];
        try (Stream<String> dataset = Files.lines(Paths.get("src/main/resources/datasets/KNMI_dataset.txt"))) {
            
            // Wrapper for hour, because Java's lambda functions are idiotic
            class Wrapper { int value = 0; }
            Wrapper hourOfYear = new Wrapper();
            dataset.filter(line -> !line.startsWith("#"))
                .forEachOrdered(line -> {
                    String[] tokens = line.split(",");
                    // tokens[1] = YYYYMMDD, tokens[2] = hour, tokens[3] = temperature, tokens[4] = radiance
                    float temperature = Float.valueOf(tokens[3].trim());
                    float radiance = Float.valueOf(tokens[4].trim());

                    // temperature is saved as 1/10th Celsius
                    tempByHour[hourOfYear.value] = temperature / 10f;
                    radianceByHour[hourOfYear.value] = radiance;
                    hourOfYear.value++;
                });
        } catch (Exception e) {
            throw new RuntimeException("Could not load KNMI dataset", e);
        }
        
        // Convert to profile with value per timestep   
        int tickMinutes = SimulationConfig.TICK_MINUTES;
        
        temperatureProfile = new double[TOTAL_TICKS_IN_YEAR];
        radianceProfile = new double[TOTAL_TICKS_IN_YEAR];
        
        for (int ts = 0; ts < TOTAL_TICKS_IN_YEAR; ts++) {
            int prevHour = ts * tickMinutes / 60;
            int nextHour = (prevHour + 1) % (365 * 24);
            float nextHourWeight = (((ts * tickMinutes) % 60) / 60f);
            temperatureProfile[ts] = (1f - nextHourWeight) * tempByHour[prevHour] + (nextHourWeight) * tempByHour[nextHour];
            radianceProfile[ts] = (1f - nextHourWeight) * radianceByHour[prevHour] + (nextHourWeight) * radianceByHour[nextHour];
        }
    }
    
    @Override
    public double getLatitude() {
        return LATITUDE;
    }

    @Override
    public double getLongitude() {
        return LONGITUDE;
    }

    @Override
    public double[] getTemperatureProfile() {
        return this.temperatureProfile;
    }

    @Override
    public double[] getRadianceProfile() {
        return this.radianceProfile;
    }
}

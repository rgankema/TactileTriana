/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.*;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.WeatherData;
import static nl.utwente.ewi.caes.tactiletriana.Util.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author niels
 */
public class SolarPanel extends DeviceBase {
    public final static String API_ORIENTATION = "orientation";
    public final static String API_EFFICIENCY = "efficiency";
    public final static String API_ELEVATION = "elevation";
    public final static String API_AREA = "area";
    public final static String API_PROFILE = "profile";
    
    private double[] profileSquareMeter;
    
    // The tick of the year where the profile starts
    private int profileTickOffset;
    
    //Efficiency degradation due to temperature increase of the solar panel. Percentage degradation for maximum power per degree celcius [percent]
    private static final double temperatureEfficiency = 0.3;

    /**
     * @param simulation the Simulation this SolarPanel belongs to
     */
    public SolarPanel(SimulationBase simulation) {
        super(simulation, "Solar Panel", "SolarPanel");
        
        // Register properties for API
        registerAPIProperty(API_AREA);
        registerAPIProperty(API_EFFICIENCY);
        registerAPIProperty(API_ELEVATION);
        registerAPIProperty(API_ORIENTATION);
        registerAPIProperty(API_PROFILE);
        
        // Register properties for prediction
        registerProperty(area);
        registerProperty(efficiency);
        registerProperty(elevation);
        registerProperty(orientation);
    }
    
    // PROPERTIES
    
    /**
     * The area of the solar panel. May not be negative.
     */
    private final DoubleProperty area = new SimpleDoubleProperty(25d) {
        @Override
        public void set(double value) {
            if (value < 0) {
                throw new IllegalArgumentException("Area may not be a negative value");
            }

            super.set(value);
        }
    };
    
    public DoubleProperty areaProperty() {
        return area;
    }

    public final double getArea() {
        return area.get();
    }

    public final void setArea(double consumption) {
        this.area.set(consumption);
    }
    
    /**
     * The elevation of the solar panel in degrees. Must be between 0 and 90.
     */
    private final DoubleProperty elevation = new SimpleDoubleProperty(45) {
        @Override
        public void set(double value) {
            if (value < 0) {
                throw new IllegalArgumentException("Elevation may not be below 0 degrees");
            } else if (value > 90) {
                throw new IllegalArgumentException("Elevation may not be higher than 90 degrees");
            }
            super.set(value);
        }
    };
    
    public DoubleProperty elevationProperty() {
        return elevation;
    }
    
    public final double getElevation() {
        return elevationProperty().get();
    }
    
    public final void setElevation(double elevation) {
        elevationProperty().set(elevation);
    }
    
    /**
     * The orientation (azimuth) of the solar panel. Can be set to any value, but
     * the end result will always be between 0 and 360. 0 degrees is south, 90
     * degrees is west, and so on.
     */
    private final DoubleProperty orientation = new SimpleDoubleProperty(0) {
        @Override
        public void set(double value) {
            value = value % 360;
            if (value < 0) {
                value += 360;
            }
            super.set(value);
        }
    };
    
    public DoubleProperty orientationProperty() {
        return orientation;
    }
    
    public final double getOrientation() {
        return orientationProperty().get();
    }
    
    public final void setOrientation(double orientation) {
        orientationProperty().set(orientation);
    }
    
    /**
     * The efficiency of the solar panel in percents. Must be between 0 and 100.
     */
    private final DoubleProperty efficiency = new SimpleDoubleProperty(21.5) {
        @Override
        public void set(double value) {
            if (value < 0) {
                throw new IllegalArgumentException("Efficiecny may not be below 0%");
            } else if (value > 100) {
                throw new IllegalArgumentException("Efficiecny may not be higher than 100%");
            }
            super.set(value);
        }
    };
    
    public DoubleProperty efficiencyProperty() {
        return efficiency;
    }
    
    public final double getEfficiency() {
        return efficiencyProperty().get();
    }
    
    public final void setEfficiency(double efficiency) {
        efficiencyProperty().set(efficiency);
    }
    
    /**
     * The profile of the solar panel from the current time until the next day
     */
    private final ObjectProperty<double[]> profile = new SimpleObjectProperty<>();

    public ObjectProperty<double[]> profileProperty() {
        return profile;
    }
    
    public double[] getProfile() {
        return profileProperty().get();
    }
    
    private void setProfile(double[] profile) {
        profileProperty().set(profile);
    }
    
    // METHODS
    
    @Override
    public void tick(boolean connected) {
        // Lots of double code, could be more elegant
        super.tick(connected);
        
        // Update profile
        WeatherData weather = WeatherData.getInstance();
        double[] radianceProfile = weather.getRadianceProfile();
        double[] tempProfile = weather.getTemperatureProfile();
        LocalDateTime time = getSimulation().getCurrentTime();
        int timeStepsInDay = (24 * 60) / SimulationConfig.TICK_MINUTES;
        
        if (profileSquareMeter == null) {
            // No profile yet, calculate the whole thing
            
            profileSquareMeter = new double[timeStepsInDay];
            
            profileTickOffset = toTimeStep(time);
            for (int ts = 0; ts < timeStepsInDay; ts++) {
                int timeStepInYear = (ts + profileTickOffset) % TOTAL_TICKS_IN_YEAR;
                double temp = tempProfile[timeStepInYear];
                double radiance = radianceProfile[timeStepInYear];
                profileSquareMeter[ts] = (float) -calculateProductionSquareMeter(temp, radiance, weather.getLongitude(), weather.getLatitude(), time);
                time = time.plusMinutes(SimulationConfig.TICK_MINUTES);
            }
        } else {
            int oldProfileTickOffset = profileTickOffset;
            profileTickOffset = toTimeStep(time);
            int deltaTimeSteps = profileTickOffset - oldProfileTickOffset;
            
            // If the new time is before the last one, just calculate the whole profile again
            if (deltaTimeSteps < 0) {
                for (int ts = 0; ts < profileSquareMeter.length; ts++) {
                    int timeStepInYear = (ts + profileTickOffset) % TOTAL_TICKS_IN_YEAR;
                    double temp = tempProfile[timeStepInYear];
                    double radiance = radianceProfile[timeStepInYear];
                    profileSquareMeter[ts] = (float) -calculateProductionSquareMeter(temp, radiance, weather.getLongitude(), weather.getLatitude(), time);
                    time = time.plusMinutes(SimulationConfig.TICK_MINUTES);
                }
            } else {
                // Shift the array
                int ts = 0;
                for (; ts < profileSquareMeter.length - deltaTimeSteps; ts++) {
                    profileSquareMeter[ts] = profileSquareMeter[ts + deltaTimeSteps];
                }
                // Calculate the timesteps we haven't done yet
                for (; ts < profileSquareMeter.length; ts++) {
                    int timeStepInYear = (ts + profileTickOffset) % TOTAL_TICKS_IN_YEAR;
                    double temp = tempProfile[timeStepInYear];
                    double radiance = radianceProfile[timeStepInYear];
                    profileSquareMeter[ts] = (float) -calculateProductionSquareMeter(temp, radiance, weather.getLongitude(), weather.getLatitude(), time);
                    time = time.plusMinutes(SimulationConfig.TICK_MINUTES);
                }
            }
        }
        
        double[] finalProfile = new double[timeStepsInDay];
        for (int i = 0; i < profileSquareMeter.length; i++) {
            finalProfile[i] = profileSquareMeter[i] * getArea();
        }
        
        setProfile(finalProfile);
        setCurrentConsumption(getProfile()[0]);
    }

    /**
     * Calculates the production per square meter of this solar panel given the
     * temperature, radiance, longitude and latitude and time.
     * 
     * @param temperature the temperature at the location of the panel
     * @param radiance the radiance at the location of the panel
     * @param longitude the longitude of the panel coordinates
     * @param latitude the latitude of the panel coordinates
     * @param time the time in the simulation
     * @return amount of watt that the device produces per square meter
     */
    private double calculateProductionSquareMeter(double temperature, double radiance, double longitude, double latitude, LocalDateTime time) {
        double PI = 3.14159265359;

        double longitudeRadian = longitude * (PI / 180);
        double latitudeRadian = latitude * (PI / 180);
        double elevationRadian = getElevation() * (PI / 180);	//Angle of the panel
        double azimuthRadian = getOrientation() * (PI / 180);

        double rho_gnd = 0.2; //constant

        //TIme calculations
        int day = time.getDayOfYear();
        double delta = (23.44 * Math.sin(2 * PI * (day + 284) / 365.24)) * (PI / 180);
        double N = 2 * PI * (day / 366); //one year
        double E_time = 229.2 * (0.0000075 + 0.001868 * Math.cos(N) - 0.032077 * Math.sin(N) - 0.014614 * Math.cos(2 * N) - 0.04089 * Math.sin(N));

        //Calcuate h: Heigth of the sun
        double local_std_time = time.getHour() * 60 + time.getMinute();
        double solar_time = (-4.0 * (longitudeRadian / (PI / 180))) + E_time + local_std_time;
        double omega = ((0.25 * solar_time) - 180) * (PI / 180);
        double h = Math.asin(Math.cos(latitudeRadian) * Math.cos(delta) * Math.cos(omega) + Math.sin(latitudeRadian) * Math.sin(delta));

        //Calculate diffuse light
        double I_0 = (1367 * 3600 / 10000) * Math.sin(h);
        double k_T = 0.0;
        if (I_0 >= 0.001) {
            k_T = radiance / I_0;
        }

        double I_d = radiance * 0.165;

        if (k_T <= 0.0) {
            I_d = 0.0;
        } else if (k_T <= 0.22) {
            I_d = radiance * (1.0 - 0.09 * k_T);
        } else if (k_T <= 0.8) {
            I_d = radiance * (0.9511 - 0.1604 * k_T + 4.388 * Math.pow(k_T, 2) - 16.638 * Math.pow(k_T, 3) + 12.336 * (Math.pow(k_T, 4)));
        }

        //Calculate the direct beam
        double I_b = radiance - I_d;
        double I = Math.min(I_0, I_b / Math.sin(h));
        if (h < 0.001) {
            I = 0.0;
        }

        //Obtain the ammount of energy
        double G_ds = I_d * (1 + Math.cos(elevationRadian)) / 2;
        double G_gnds = radiance * rho_gnd * (1 - Math.cos(elevationRadian)) / 2;
        double theta = Math.acos(Math.sin(delta) * Math.sin(latitudeRadian) * Math.cos(elevationRadian)
                - Math.sin(delta) * Math.cos(latitudeRadian) * Math.cos(azimuthRadian) * Math.sin(elevationRadian)
                + Math.cos(delta) * Math.cos(latitudeRadian) * Math.cos(omega) * Math.cos(elevationRadian)
                + Math.cos(delta) * Math.sin(latitudeRadian) * Math.cos(azimuthRadian) * Math.cos(omega) * Math.sin(elevationRadian)
                + Math.cos(delta) * Math.sin(azimuthRadian) * Math.sin(omega) * Math.sin(elevationRadian));
        double G_bs = Math.max(0.0, I * Math.cos(theta));
        double G_ts = G_ds + G_bs + G_gnds; //This is the joules in one hour for a square cm
        
        
        double powerSquareMeter = (G_ts * 10000) / 3600;

        //Guess for the PV temperature that affects the efficiency.
        double temperaturePV = temperature + (50 * powerSquareMeter / 1367); //Formula not based on anything or whatsover, this part can be improved
        double actualEfficiency = (getEfficiency() * (1 - ((temperaturePV - 25) * temperatureEfficiency) / 100)) / 100;
        
        double result = powerSquareMeter * actualEfficiency;
        
        //Return the production in W (coming from J/cm2 for a whole hour)
        return result; 
    }

    @Override
    protected JSONObject parametersToJSON() {
        JSONObject result = new JSONObject();
        result.put(API_AREA, getArea());
        result.put(API_EFFICIENCY, getEfficiency());
        result.put(API_ELEVATION, getElevation());
        result.put(API_ORIENTATION, getOrientation());
        JSONArray jsonProfile = new JSONArray();
        for (int i = 0; i < getProfile().length; i++) {
            jsonProfile.add(getProfile()[i]);
        }
        result.put(API_PROFILE, jsonProfile);
        return result;
    }

}

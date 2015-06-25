/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
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

    // The profile for the solar panel for an efficiency of 100% and an area of 1m²
    private double[] abstractProfile;

    // The tick of the year where the profile starts
    private int profileTickOffset;

    /**
     * Constructs a new SolarPanel.
     *
     * @param simulation the Simulation this SolarPanel belongs to
     */
    public SolarPanel(SimulationBase simulation) {
        super(simulation, "Solar Panel", "SolarPanel");

        // Register properties for API
        registerAPIParameter(API_AREA);
        registerAPIParameter(API_EFFICIENCY);
        registerAPIParameter(API_ELEVATION);
        registerAPIParameter(API_ORIENTATION);
        registerAPIParameter(API_PROFILE);

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
            setDirty(true);
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
            if (value != get()) {
                if (value < 0) {
                    throw new IllegalArgumentException("Elevation may not be below 0 degrees");
                } else if (value > 90) {
                    throw new IllegalArgumentException("Elevation may not be higher than 90 degrees");
                }
                // When elevation changes profile becomes invalid
                abstractProfile = null;
                super.set(value);
                setDirty(true);
            }
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
     * The orientation (azimuth) of the solar panel. Can be set to any value,
     * but the end result will always be between 0 and 360. 0 degrees is south,
     * 90 degrees is west, and so on.
     */
    private final DoubleProperty orientation = new SimpleDoubleProperty(0) {
        @Override
        public void set(double value) {
            value = value % 360;
            if (value < 0) {
                value += 360;
            }
            if (value != get()) {
                // When orientation changes the profile becomes invalid
                abstractProfile = null;
                super.set(value);
                setDirty(true);
            }
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
            if (value != get()) {
                if (value < 0) {
                    throw new IllegalArgumentException("Efficiency may not be below 0%");
                } else if (value > 100) {
                    throw new IllegalArgumentException("Efficiency may not be higher than 100%");
                }
                super.set(value);
                setDirty(true);
            }
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

    public ReadOnlyObjectProperty<double[]> profileProperty() {
        return profile;
    }

    public double[] getProfile() {
        return profileProperty().get();
    }

    private void setProfile(double[] profile) {
        this.profile.set(profile);
    }

    // METHODS
    @Override
    public void doTick(boolean connected) {

        // Update profile
        WeatherData weather = WeatherData.getInstance();
        double[] radianceProfile = weather.getRadianceProfile();
        double[] tempProfile = weather.getTemperatureProfile();
        LocalDateTime time = simulation.getCurrentTime();
        int timeStepsInDay = (24 * 60) / GlobalSettings.TICK_MINUTES;

        if (abstractProfile == null) {
            // No profile yet, calculate the whole thing

            abstractProfile = new double[timeStepsInDay];

            profileTickOffset = toTimeStep(time);
            for (int ts = 0; ts < timeStepsInDay; ts++) {
                int timeStepInYear = (ts + profileTickOffset) % getTotalTicksInYear();
                double temp = tempProfile[timeStepInYear];
                double radiance = radianceProfile[timeStepInYear];
                abstractProfile[ts] = -calculateProductionSquareMeter(temp, radiance, time);
                time = time.plusMinutes(GlobalSettings.TICK_MINUTES);
            }
        } else {
            int oldProfileTickOffset = profileTickOffset;
            profileTickOffset = toTimeStep(time);
            int deltaTimeSteps = profileTickOffset - oldProfileTickOffset;

            // If the new time is before the last one, just calculate the whole profile again
            if (deltaTimeSteps < 0) {
                for (int ts = 0; ts < abstractProfile.length; ts++) {
                    int timeStepInYear = (ts + profileTickOffset) % getTotalTicksInYear();
                    double temp = tempProfile[timeStepInYear];
                    double radiance = radianceProfile[timeStepInYear];
                    abstractProfile[ts] = -calculateProductionSquareMeter(temp, radiance, time);
                    time = time.plusMinutes(GlobalSettings.TICK_MINUTES);
                }
            } else {
                // Shift the array
                int ts = 0;
                for (; ts < abstractProfile.length - deltaTimeSteps; ts++) {
                    abstractProfile[ts] = abstractProfile[ts + deltaTimeSteps];
                }
                // Calculate the timesteps we haven't done yet
                for (; ts < abstractProfile.length; ts++) {
                    int timeStepInYear = (ts + profileTickOffset) % getTotalTicksInYear();
                    double temp = tempProfile[timeStepInYear];
                    double radiance = radianceProfile[timeStepInYear];
                    abstractProfile[ts] = -calculateProductionSquareMeter(temp, radiance, time);
                    time = time.plusMinutes(GlobalSettings.TICK_MINUTES);
                }
            }
        }

        double[] finalProfile = new double[timeStepsInDay];
        double efficiency = getEfficiency() / 100;
        for (int i = 0; i < abstractProfile.length; i++) {
            finalProfile[i] = abstractProfile[i] * getArea() * efficiency;
        }

        setProfile(finalProfile);
        setCurrentConsumption(getProfile()[0]);
    }

    // Below are constants that are used a lot and shouldn't be calculated every time again
    private static final double PI = 3.14159265359;
    private static final double PI_DIV_180 = PI / 180;
    private static final double RHO_GND = 0.2;

    private static final double longitude = WeatherData.getInstance().getLongitude();
    private static final double latitude = WeatherData.getInstance().getLatitude();
    private static final double latitudeRadian = latitude * PI_DIV_180;
    private static final double cosLatitudeRadian = Math.cos(latitudeRadian);
    private static final double sinLatitudeRadian = Math.sin(latitudeRadian);
    // Delta and sines and cosines of delta for each day of the year
    private static final double deltas[] = new double[366];
    private static final double cosDeltas[] = new double[366];
    private static final double sinDeltas[] = new double[366];

    /**
     * Calculates the production per square meter of this solar panel given the
     * temperature, radiance, longitude and latitude and time, and an efficiency
     * of 100%.
     *
     * @param temperature the temperature at the location of the panel
     * @param radiance the radiance at the location of the panel
     * @param time the time in the simulation
     * @return amount of watt that the device produces per square meter
     */
    private double calculateProductionSquareMeter(double temperature, double radiance, LocalDateTime time) {

        double elevationRadian = getElevation() * PI_DIV_180;
        double cosElevationRadian = Math.cos(elevationRadian);
        double sinElevationRadian = Math.sin(elevationRadian);
        double azimuthRadian = getOrientation() * PI_DIV_180;
        double cosAzimuthRadian = Math.cos(azimuthRadian);
        double sinAzimuthRadian = Math.sin(azimuthRadian);

        // Time calculations
        int day = time.getDayOfYear();
        if (deltas[day] == 0) {
            deltas[day] = (23.44 * Math.sin(2 * PI * (day + 284) / 365.24)) * PI_DIV_180;
            cosDeltas[day] = Math.cos(deltas[day]);
            sinDeltas[day] = Math.sin(deltas[day]);
        }

        double cosDelta = cosDeltas[day];
        double sinDelta = sinDeltas[day];
        double N = 2 * PI * (day / 366);
        double E_time = 229.2 * (0.0000075 + 0.001868 * Math.cos(N) - 0.032077 * Math.sin(N) - 0.014614 * Math.cos(2 * N) - 0.04089 * Math.sin(N));

        // Calculate h: height of sun
        double local_std_time = time.getHour() * 60 + time.getMinute();
        double solar_time = (-4.0 * longitude) + E_time + local_std_time;
        double omega = ((0.25 * solar_time) - 180) * PI_DIV_180;
        double cosOmega = Math.cos(omega);
        double sinOmega = Math.sin(omega);
        double h = Math.asin(cosLatitudeRadian * cosDelta * cosOmega + sinLatitudeRadian * sinDelta);

        // Calculate diffuse light
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
        double G_ds = I_d * (1 + cosElevationRadian) / 2;
        double G_gnds = radiance * RHO_GND * (1 - cosElevationRadian) / 2;
        double theta = Math.acos(sinDelta * sinLatitudeRadian * cosElevationRadian
                - sinDelta * cosLatitudeRadian * cosAzimuthRadian * sinElevationRadian
                + cosDelta * cosLatitudeRadian * cosOmega * cosElevationRadian
                + cosDelta * sinLatitudeRadian * cosAzimuthRadian * cosOmega * sinElevationRadian
                + cosDelta * sinAzimuthRadian * sinOmega * sinElevationRadian);
        double G_bs = Math.max(0.0, I * Math.cos(theta));
        double G_ts = G_ds + G_bs + G_gnds; //This is the joules in one hour for a square cm

        double powerSquareMeter = (G_ts * 10000) / 3600;

        //Guess for the PV temperature that affects the efficiency.
        double temperaturePV = temperature + (50 * powerSquareMeter / 1367);
        double temperatureEfficiency = (1 - ((temperaturePV - 25) * 0.3) / 100);

        return powerSquareMeter * temperatureEfficiency;
    }

    // API METHODS
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

    @Override
    public void updateParameter(String parameter, Object value) {
        double v = (double) value;
        if (parameter.equals(API_AREA)) {
            setArea(v);
        } else if (parameter.equals(API_EFFICIENCY)) {
            setEfficiency(v);
        } else if (parameter.equals(API_ELEVATION)) {
            setElevation(v);
        } else if (parameter.equals(API_ORIENTATION)) {
            setOrientation(v);
        } else {
            super.updateParameter(parameter, value);
        }
    }

}

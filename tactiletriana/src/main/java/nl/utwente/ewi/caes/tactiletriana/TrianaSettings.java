/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario.TimeSpan;

/**
 *
 * @author Mick
 */
public final class TrianaSettings {
    // CONSTANTS
    public static final String DEFAULT_FILE = "tactiletriana.config";
    
    private static final String STR_TICK_MINUTES = "TICK_MINUTES";
    private static final String STR_SYSTEM_TICK_TIME = "SYSTEM_TICK_TIME";
    private static final String STR_HOUSE_FUSE_MAX_CURRENT = "HOUSE_FUSE_MAX_CURRENT";
    private static final String STR_TIME_SCENARIO = "TIME_SCENARIO";
    private static final String STR_EXTENDED_PARAMETERS = "EXTENDED_PARAMETERS";
    private static final String STR_TOUCH_SCREEN_ID = "TOUCH_SCREEN_ID";
    private static final String STR_DETAIL_SCREEN_ID = "DETAIL_SCREEN_ID";
    private static final String STR_FULLSCREEN = "FULLSCREEN";
    private static final String STR_UNCONTROLLABLE_LOAD_ENABLED = "UNCONTROLLABLE_LOAD_ENABLED";
    
    // VARIABLES
    private static final List<Runnable> callbacks = new ArrayList<>();
    private static Properties properties;
    
    public static int TICK_MINUTES = -1;                        // time in minutes that passes in the simulation with each tick
    public static int SYSTEM_TICK_TIME = -1;                    // time between ticks in ms
    public static boolean UNCONTROLLABLE_LOAD_ENABLED = true;   // staat de uncontrolable load aan?
    public static int HOUSE_FUSE_MAX_CURRENT = -1;              // max current before a fuse breaks
    public static boolean EXTENDED_PARAMETERS = false;          // whether the users have access to the full range of device parameters
    public static TimeScenario TIME_SCENARIO = null;            // time scenario used by the simulation
    public static int TOUCH_SCREEN_ID = 1;
    public static int DETAIL_SCREEN_ID = 1;
    public static boolean FULLSCREEN = true;
    
    /**
     * Loads the settings from a given file path.
     * 
     * @param filePath the path to the settings file
     */
    public static void load(String filePath) {
        properties = new Properties();
        
        try (FileInputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.err.println("Warning: Could not read the config file, using default values instead.");
            return;
        }
        
        try {
            TICK_MINUTES = Integer.parseInt(loadProperty(STR_TICK_MINUTES));
            SYSTEM_TICK_TIME = Integer.parseInt(loadProperty(STR_SYSTEM_TICK_TIME));
            HOUSE_FUSE_MAX_CURRENT = Integer.parseInt(loadProperty(STR_HOUSE_FUSE_MAX_CURRENT));
            EXTENDED_PARAMETERS = Boolean.parseBoolean(loadProperty(STR_EXTENDED_PARAMETERS));
            TIME_SCENARIO = TimeScenario.parse(loadProperty(STR_TIME_SCENARIO));
            FULLSCREEN = Boolean.parseBoolean(loadProperty(STR_FULLSCREEN));
            TOUCH_SCREEN_ID = Integer.parseInt(loadProperty(STR_TOUCH_SCREEN_ID));
            DETAIL_SCREEN_ID = Integer.parseInt(loadProperty(STR_DETAIL_SCREEN_ID));
            UNCONTROLLABLE_LOAD_ENABLED = Boolean.parseBoolean(loadProperty(STR_UNCONTROLLABLE_LOAD_ENABLED));
        } catch (NumberFormatException | NullPointerException e) {
            System.err.println("Warning: Error while parsing one of the properties, using default values for remaining properties.");
        } finally {
            for (Runnable callback : callbacks) {
                callback.run();
            }
        }
        
        validate();
    }

    private static String loadProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Saves the settings to a given file path.
     * 
     * @param configFile the path to the settings file
     */
    public static void save(String configFile) {
        saveProperty(STR_TICK_MINUTES, TICK_MINUTES);
        saveProperty(STR_SYSTEM_TICK_TIME, SYSTEM_TICK_TIME);
        saveProperty(STR_HOUSE_FUSE_MAX_CURRENT, HOUSE_FUSE_MAX_CURRENT);
        saveProperty(STR_EXTENDED_PARAMETERS, EXTENDED_PARAMETERS);
        saveProperty(STR_TIME_SCENARIO, TIME_SCENARIO);
        saveProperty(STR_FULLSCREEN, FULLSCREEN);
        saveProperty(STR_TOUCH_SCREEN_ID, TOUCH_SCREEN_ID);
        saveProperty(STR_DETAIL_SCREEN_ID, DETAIL_SCREEN_ID);
        saveProperty(STR_UNCONTROLLABLE_LOAD_ENABLED, UNCONTROLLABLE_LOAD_ENABLED);
        
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            System.out.println("Warning: Configuration file could not be saved.");
        }
    }

    private static void saveProperty(String key, Object value) {
        properties.setProperty(key, value.toString());
    }
    
    // Checks if values are within acceptable ranges, and resets them if need be.
    private static void validate() {
        if (!inRange(TICK_MINUTES, 1, 60)) {
            TICK_MINUTES = 5;
        }
        if (!inRange(HOUSE_FUSE_MAX_CURRENT, 1, Integer.MAX_VALUE)) {
            HOUSE_FUSE_MAX_CURRENT = 3 * 35;
        }
        if (!inRange(SYSTEM_TICK_TIME, 1, Integer.MAX_VALUE)) {
            SYSTEM_TICK_TIME = 200;
        }
        for (TimeSpan timeSpan : TIME_SCENARIO.getTimeSpans()) {
            if (timeSpan.getStart().isBefore(LocalDate.of(2014, 1, 1)) || timeSpan.getEnd().isAfter(LocalDate.of(2014, 12, 31))) {
                TIME_SCENARIO = new TimeScenario(new TimeSpan(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 12, 31)));
                break;
            }
        }
        
        // Cannot validate screen ID's yet because the StageController needs to initalize first, which
        // is dependent of TrianaSettings being initialized.
    }
    
    // Returns if value is within range, and prints warning if it's not
    private static boolean inRange(int current, int min, int max) {
        if (current < min || current > max) {
            return false;
        }
        return true;
    }
    
    /**
     * Adds a function that will be called whenever the settings change.
     * 
     * @param handler a function that takes no arguments
     */
    public static void addSettingsChangedHandler(Runnable handler) {
        callbacks.add(handler);
    }
    
    /**
     * Removes a settings change handler.
     * 
     * @param handler the function to remove
     */
    public static void removeSettingsChangedHandler(Runnable handler) {
        callbacks.remove(handler);
    }
    
    public static String settingsToString() {
        StringBuilder sb = new StringBuilder();
        for (String key : properties.stringPropertyNames()) {
            sb.append(key);
            sb.append("=");
            sb.append(properties.getProperty(key));
            sb.append('\n');
        }
        return sb.toString();
    }
}

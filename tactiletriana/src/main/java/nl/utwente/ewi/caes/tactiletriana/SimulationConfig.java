/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario.ScenarioVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario.TimeSpan;

/**
 *
 * @author Mick
 */
public final class SimulationConfig {

    public static Properties properties = new Properties();
    public static final String CONFIG_FILE = "tactiletriana.config";

    public static int TICK_MINUTES;                     // time in minutes that passes in the simulation with each tick
    public static int SYSTEM_TICK_TIME;                 // time between ticks in ms
    public static boolean UNCONTROLLABLE_LOAD_ENABLED;  // staat de uncontrolable load aan?
    public static int HOUSE_FUSE_MAX_CURRENT;           // max current before a fuse breaks
    public static boolean EXTENDED_PARAMETERS;          // whether the users have access to the full range of device parameters

    private static final String STR_TICK_MINUTES = "TICK_MINUTES";
    private static final String STR_SYSTEM_TICK_TIME = "SYSTEM_TICK_TIME";
    private static final String STR_UNCONTROLLABLE_LOAD_ENABLED = "UNCONTROLLABLE_LOAD_ENABLED";
    private static final String STR_HOUSE_FUSE_MAX_CURRENT = "HOUSE_FUSE_MAX_CURRENT";
    private static final String STR_TIME_SCENARIOS = "TIME_SCENARIO";
    private static final String STR_EXTENDED_PARAMETERS = "EXTENDED_PARAMETERS";
    private static final String STR_TOUCH_SCREEN_ID = "TOUCH_SCREEN_ID";
    private static final String STR_DETAIL_SCREEN_ID = "DETAIL_SCREEN_ID";
    private static final String STR_FULLSCREEN = "FULLSCREEN";

    public static void LoadProperties() {
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
            TICK_MINUTES = Integer.parseInt(LoadProperty(STR_TICK_MINUTES));
            SYSTEM_TICK_TIME = Integer.parseInt(LoadProperty(STR_SYSTEM_TICK_TIME));
            UNCONTROLLABLE_LOAD_ENABLED = Boolean.parseBoolean(LoadProperty(STR_UNCONTROLLABLE_LOAD_ENABLED));
            HOUSE_FUSE_MAX_CURRENT = Integer.parseInt(LoadProperty(STR_HOUSE_FUSE_MAX_CURRENT));
            EXTENDED_PARAMETERS = Boolean.parseBoolean(LoadProperty(STR_EXTENDED_PARAMETERS));
        } catch (Exception e) {
            System.out.println("Warning: Could not read the config file.");
            TICK_MINUTES = 5;
            SYSTEM_TICK_TIME = 200;
            UNCONTROLLABLE_LOAD_ENABLED = true;
            HOUSE_FUSE_MAX_CURRENT = 3 * 35;
            EXTENDED_PARAMETERS = false;
        }
    }

    public static String LoadProperty(String title) throws IOException {
        return properties.getProperty(title);
    }

    public static void SaveProperties() {
        SaveProperty(STR_TICK_MINUTES, String.valueOf(TICK_MINUTES));
        SaveProperty(STR_SYSTEM_TICK_TIME, String.valueOf(SYSTEM_TICK_TIME));
        SaveProperty(STR_UNCONTROLLABLE_LOAD_ENABLED, String.valueOf(UNCONTROLLABLE_LOAD_ENABLED));
        SaveProperty(STR_HOUSE_FUSE_MAX_CURRENT, String.valueOf(HOUSE_FUSE_MAX_CURRENT));
        SaveProperty(STR_EXTENDED_PARAMETERS, String.valueOf(EXTENDED_PARAMETERS));
    }

    public static void SaveProperty(String title, String value) {
        try {
            properties.setProperty(title, value);
            properties.store(new FileOutputStream(CONFIG_FILE), null);
        } catch (IOException e) {
            System.out.println("Error: config file could not be saved.");
        }
    }

    public static TimeScenario LoadTimeScenario() throws IOException {
        TimeScenario ts;
        try {
            ts = TimeScenario.parse(LoadProperty(STR_TIME_SCENARIOS));
        } catch (IOException e) {
            return new TimeScenario(new TimeSpan(ScenarioVM.MIN_DATE, ScenarioVM.MAX_DATE));
        }
        // checks uitvoeren!
        TimeSpan prev = null;
        for (TimeSpan span : ts.getTimeSpans()) {
            if (prev != null) {
                if ((span.getStart().isAfter(prev.getEnd()) || span.getStart().isEqual(prev.getEnd()))
                        && (span.getStart().isAfter(ScenarioVM.MIN_DATE) || span.getStart().isEqual(ScenarioVM.MIN_DATE))
                        && (span.getEnd().isBefore(ScenarioVM.MAX_DATE) || span.getEnd().isEqual(ScenarioVM.MAX_DATE))
                        && (span.getStart().isBefore(span.getEnd()) || span.getStart().isEqual(span.getEnd()))) {
                    prev = span;
                } else {
                    return new TimeScenario(new TimeSpan(ScenarioVM.MIN_DATE, ScenarioVM.MAX_DATE));

                }

            }
        }
        return ts;
    }

    public static void SaveTimeScenario(TimeScenario ts) {
        SaveProperty(STR_TIME_SCENARIOS, ts.toString());
    }

}

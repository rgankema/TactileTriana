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
import java.util.Properties;

/**
 *
 * @author Mick
 */
public final class SimulationConfig {

    public static Properties properties = new Properties();
    public static final String CONFIG_FILE = "tactiletriana.config";

    //public static final int SIMULATION_NUMBER_OF_HOUSES = 6;   // number of houses
    public static int TICK_MINUTES;   // time in minutes that passes in the simulation with each tick
    public static int SYSTEM_TICK_TIME;       // time between ticks in ms
    public static boolean SIMULATION_UNCONTROLABLE_LOAD_ENABLED; // staat de uncontrolable load aan?
    public static int HOUSE_MAX_FUSE_CURRENT;
    
    public static LocalDate MIN_DATE;
    public static LocalDate MAX_DATE; 

    public static String LoadProperty(String title) throws IOException {
        // load the property
        properties.load(new FileInputStream(CONFIG_FILE));
        return properties.getProperty(title);

    }

    public static void LoadProperties() {
        try{
            TICK_MINUTES = Integer.parseInt(LoadProperty("TICK_MINUTES"));
            SYSTEM_TICK_TIME = Integer.parseInt(LoadProperty("SYSTEM_TICK_TIME"));
            SIMULATION_UNCONTROLABLE_LOAD_ENABLED = Boolean.parseBoolean(LoadProperty("SIMULATION_UNCONTROLABLE_LOAD_ENABLED"));
            HOUSE_MAX_FUSE_CURRENT = Integer.parseInt(LoadProperty("HOUSE_MAX_FUSE_CURRENT"));
            MAX_DATE = LocalDate.parse(LoadProperty("MAX_DATE"));
            MIN_DATE = LocalDate.parse(LoadProperty("MIN_DATE"));
        }
        catch (Exception e){
            System.out.println("Error: Could not read the config file.");
            TICK_MINUTES = 5;
            SYSTEM_TICK_TIME = 200;
            SIMULATION_UNCONTROLABLE_LOAD_ENABLED = true;
            HOUSE_MAX_FUSE_CURRENT = 3 * 35;
            MAX_DATE = LocalDate.of(2014, 12, 31);
            MIN_DATE = LocalDate.of(2014, 1, 1);
        }        
    }
    public static void SaveProperties(){
        SaveProperty("TICK_MINUTES", String.valueOf(TICK_MINUTES));
        SaveProperty("SYSTEM_TICK_TIME", String.valueOf(SYSTEM_TICK_TIME));
        SaveProperty("SIMULATION_UNCONTROLABLE_LOAD_ENABLED", String.valueOf(SIMULATION_UNCONTROLABLE_LOAD_ENABLED));
        SaveProperty("HOUSE_MAX_FUSE_CURRENT", String.valueOf(HOUSE_MAX_FUSE_CURRENT));
        SaveProperty("MAX_DATE", MAX_DATE.toString());
        SaveProperty("MIN_DATE", MIN_DATE.toString());
    }

    public static void SaveProperty(String title, String value) {
        try {
            properties.setProperty(title, value);
            properties.store(new FileOutputStream(CONFIG_FILE), null);
        } catch (IOException e) {
            System.out.println("Error config file could not be written");
        }
    }

}

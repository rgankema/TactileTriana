/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import org.apache.commons.csv.*;
/**
 *
 * @author jd
 */
public class UncontrollableDevices extends DeviceBase {
    
    //Power consumption per minute for a complete year(365 days)
    private ArrayList<Double> profile = new ArrayList<Double>();
    
    //TODO improve error handling
    public UncontrollableDevices() {
        super();
        //Load the profile data into an array from the CSV file containing power consumptions for 6 houses. 
        //For each new instance one of the 6 houses is randomly selected as the source for the data.
        try {
            File csvData = new File("src/main/resources/datasets/watt_house_profiles_year.csv");
            CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.DEFAULT);
            Random rand = new Random();
            int randint = rand.nextInt(6);
            for (CSVRecord csvRecord : parser) {
                
                profile.add(Double.parseDouble(csvRecord.get(0).split(";")[randint]));
            }
        } catch (IOException e) {
            //profile = new int[5256000];
            throw new RuntimeException();
        }
    }
    
    /**
     * 
     * @param profilenr - A number between 0 and 5 (inclusive) which selects the profile data on which this instance is based 
     */
    public UncontrollableDevices(int profilenr) {
        super();
        //Load the profile data into an array from the CSV file containing power consumptions for 6 houses. 
        //For each new instance one of the 6 houses is randomly selected as the source for the data.
        try {
            File csvData = new File("src/main/resources/datasets/watt_house_profiles_year.csv");
            CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), CSVFormat.DEFAULT);
            //Make sure the profile number is between 0 and 5 
            profilenr = Math.abs(profilenr % 5);
            for (CSVRecord csvRecord : parser) {
                
                profile.add(Double.parseDouble(csvRecord.get(0).split(";")[profilenr]));
            }
        } catch (IOException e) {
            //profile = new int[5256000];
            throw new RuntimeException();
        }
    }
    
    //Uncontrollable devices, so load is determined by time.
    @Override
    public void tick(Simulation simulation, boolean connected) {
        super.tick(simulation, connected);setCurrentConsumption(profile.get(simulation.getCurrentTime().getDayOfYear() * 24 * 60));
        int dayOfYear = simulation.getCurrentTime().getDayOfYear();
        if(dayOfYear > 365) {
            dayOfYear = 365;
        }
        setCurrentConsumption(profile.get(dayOfYear * 24 * 60));
    }
    
    public ArrayList<Double> getProfile() {
        return profile;
    }
    
    
    
    
}

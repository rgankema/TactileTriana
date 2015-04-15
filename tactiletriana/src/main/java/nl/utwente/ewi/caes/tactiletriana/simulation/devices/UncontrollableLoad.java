/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import org.apache.commons.csv.*;
/**
 *
 * @author jd
 */
public class UncontrollableLoad extends DeviceBase {
    
    //Power consumption per minute for a complete year(365 days)
    private ArrayList<Double> profile = new ArrayList<>();
    
    /**
     * 
     * @param profileNumber - A number between 0 and 5 (inclusive) which selects the profile data on which this instance is based 
     */
    public UncontrollableLoad(int profileNumber, Simulation simulation) {
        super("Uncontrollable Load", simulation);
        
        if (profileNumber < 0 || profileNumber > 5) throw new IllegalArgumentException("profileNumber must be in the range of 0 to 5");
        
        //Load the profile data into an array from the CSV file containing power consumptions for 6 houses. 
        //For each new instance one of the 6 houses is randomly selected as the source for the data.
        try {
            File csvData = new File("src/main/resources/datasets/watt_house_profiles_year.csv");
            CSVFormat format = CSVFormat.DEFAULT.withDelimiter(';');
            // Jan Harm: je kan gewoon een format aanmaken :)
            CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), format);
            // Een record is een rij.
            
            
            for (CSVRecord csvRecord : parser) {
                profile.add(Double.parseDouble(csvRecord.get(profileNumber)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while parsing house profile dataset", e);
        }
    }

    
    //Uncontrollable devices, so load is determined by time.
    @Override
    public void tick(Simulation simulation, boolean connected) {
        super.tick(simulation, connected);
        LocalDateTime t = simulation.getCurrentTime();
        int minuteOfYear = t.getDayOfYear() * 24 *60 + t.getHour() * 60 + t.getMinute();
        setCurrentConsumption(profile.get(minuteOfYear));
    }
}

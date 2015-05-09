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
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import org.apache.commons.csv.*;

/**
 *
 * @author jd
 */
public class UncontrollableLoad extends DeviceBase {

    //Power consumption per minute for a complete year(365 days)
    private static double[][] profile;
    private final int profileNumber;
    
    /**
     *
     * @param profileNumber - A number between 0 and 5 (inclusive) which selects
     * the profile data on which this instance is based
     */
    public UncontrollableLoad(int profileNumber, Simulation simulation) {
        super(simulation, "Uncontrollable Load");

        if (profileNumber < 0 || profileNumber > 5) {
            throw new IllegalArgumentException("profileNumber must be in the range of 0 to 5");
        }

        this.profileNumber = profileNumber;
        
        //Load the profile data into an array from the CSV file containing power consumptions for 6 houses.
        if (profile == null) {
            profile = new double[6][525608];
            try {
                File csvData = new File("src/main/resources/datasets/house_profiles.csv");
                CSVFormat format = CSVFormat.DEFAULT.withDelimiter(';');
                CSVParser parser = CSVParser.parse(csvData, Charset.defaultCharset(), format);
                for (CSVRecord csvRecord : parser) {
                    for (int p = 0; p < 6; p++) {
                        profile[p][(int)parser.getRecordNumber()] = Double.parseDouble(csvRecord.get(p));
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while parsing house profile dataset", e);
            }
        }
    }

    //Uncontrollable devices, so load is determined by time.
    @Override
    public void tick(double timePassed, boolean connected) {
        super.tick(timePassed, connected);
        LocalDateTime t = simulation.getCurrentTime();
        int minuteOfYear = t.getDayOfYear() * 24 * 60 + t.getHour() * 60 + t.getMinute();
        setCurrentConsumption(profile[profileNumber][minuteOfYear]);
    }
}

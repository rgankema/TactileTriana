/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.util.ArrayList;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jd
 */
public class UncontrollableDevicesTest {
    
    /**
     * Tests the creation of and UncontrollableDevices device. Especially the parsing of the CSV file containing profile data.
     */
    @Test
    public void UncontrollableDevicesTest() {
        
        UncontrollableDevices uc = new UncontrollableDevices();
       
        ArrayList<Double> profile = uc.getProfile();
        assertEquals(profile.get(0), 168d, 0.01);
            
    }
    
    public void testUncontrollableDevices() {
        
    }    
    
}

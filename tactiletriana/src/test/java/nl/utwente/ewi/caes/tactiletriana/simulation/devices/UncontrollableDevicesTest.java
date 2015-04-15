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
        
        UncontrollableLoad uc = new UncontrollableLoad();
       
        ArrayList<Double> profile = uc.getProfile();
        double value = profile.get(0);
        assertTrue(value ==  168d || value ==  146d || value ==  240d || value ==  161d || value ==  230d || value ==  224d);
        
        UncontrollableLoad uc2 = new UncontrollableLoad(3);
        double value2 = uc2.getProfile().get(0);
        assertEquals(161d, value2, 0.01);
        
    }
    
    public void testUncontrollableDevices() {
        
    }    
    
}

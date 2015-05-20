/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.io.IOException;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.TimeShiftableBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.WashingMachine;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 *
 * @author jd
 */
public class UpdateParameterTest {
    
    /**
     * Test the updateParameters() method for TimeShiftableBase
     * This test is heavily depended on the API specification and the adherence to the specification by this test method and the updateParameters method.
     * 
     */
    @Test
    public void testTimeShiftableUpdateParameters() {
        TimeShiftableBase washingmachine = new WashingMachine(new Simulation());
        JSONParser parser = new JSONParser();
        JSONObject profile = null;
        try {
            profile = (JSONObject) parser.parse("{\"static_profile\" : [30.0, 40.0]}");
        } catch (ParseException e) {
            fail();
        }
        
        assertTrue(washingmachine.updateParameter("static_profile", profile));
        assertEquals(30.0, washingmachine.getStaticProfile()[0]);
        assertEquals(40.0, washingmachine.getStaticProfile()[0]);
        
        
        
    }
}

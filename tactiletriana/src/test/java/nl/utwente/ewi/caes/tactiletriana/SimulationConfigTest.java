/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.io.IOException;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;

/**
 *
 * @author mickvdv
 */
public class SimulationConfigTest {
    @Test
    public void testLoadConfigFile(){
        assertEquals(SimulationConfig.HOUSE_FUSE_MAX_CURRENT, 0);
        SimulationConfig.LoadProperties();
        assertThat(SimulationConfig.HOUSE_FUSE_MAX_CURRENT, is(not(0)));
    }
    
    @Test
    public void testSaveConfigFile(){
        
        // save the old value
        int oldValue = SimulationConfig.HOUSE_FUSE_MAX_CURRENT;
        
        // change value
        SimulationConfig.HOUSE_FUSE_MAX_CURRENT = 1337;
        SimulationConfig.SaveProperties();
        SimulationConfig.HOUSE_FUSE_MAX_CURRENT = 12345;
        SimulationConfig.LoadProperties();
        assertEquals(SimulationConfig.HOUSE_FUSE_MAX_CURRENT, 1337);
        
        // resave the oldvalue
        SimulationConfig.HOUSE_FUSE_MAX_CURRENT = oldValue;
        SimulationConfig.SaveProperties();
        SimulationConfig.HOUSE_FUSE_MAX_CURRENT = 12345;
        SimulationConfig.LoadProperties();
        assertEquals(SimulationConfig.HOUSE_FUSE_MAX_CURRENT, oldValue);
    }
    
}

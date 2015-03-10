/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mickvdv
 */
public class MockupDeviceTest {
    
    public MockupDeviceTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of currentConsumptionProperty method, of class MockupDevice.
     */

    /**
     * Test of getCurrentConsumption method, of class MockupDevice.
     */
    @Test
    public void testGetCurrentConsumption() {
        System.out.println("getCurrentConsumption");
        MockupDevice instance = new MockupDevice();
        
        instance.tick(5);
        
        double expResult = 300.0;
        double result = instance.getCurrentConsumption();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of tick method, of class MockupDevice.
     */
    @Test
    public void testTick() {
        System.out.println("tick");
        double time = 0.0;
        MockupDevice instance = new MockupDevice();
        instance.tick(time);
    }
    
}

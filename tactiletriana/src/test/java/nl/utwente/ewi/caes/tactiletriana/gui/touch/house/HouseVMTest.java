/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.house;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class HouseVMTest {
    
    private House mockedHouse;
    private DoubleProperty houseCurrentConsumption;
    private DoubleProperty houseMaxConsumption;
    private BooleanProperty fuseBlown;
    
    public HouseVMTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        mockedHouse = mock(House.class);
        
        houseCurrentConsumption = new SimpleDoubleProperty(10);
        houseMaxConsumption = new SimpleDoubleProperty(100);
        fuseBlown = new SimpleBooleanProperty(false);
        
        when(mockedHouse.currentConsumptionProperty()).thenReturn(houseCurrentConsumption);
        when(mockedHouse.maximumConsumptionProperty()).thenReturn(houseMaxConsumption);
        when(mockedHouse.fuseBlownProperty()).thenReturn(fuseBlown);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of loadProperty method, of class HouseVM.
     */
    @Test
    public void testLoadPropertyPositiveConsumption() {
        System.out.println("loadPropertyPositiveConsumption");
        HouseVM instance = new HouseVM(mockedHouse);
        
        houseCurrentConsumption.set(5);
        houseMaxConsumption.set(10);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }
    
    /**
     * Test of loadProperty method, of class HouseVM.
     */
    @Test
    public void testLoadPropertyNegativeConsumption() {
        System.out.println("loadPropertyNegativeConsumption");
        HouseVM instance = new HouseVM(mockedHouse);
        
        houseCurrentConsumption.set(-5);
        houseMaxConsumption.set(10);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }
    
    /**
     * Test of loadProperty method, of class HouseVM.
     */
    @Test
    public void testLoadPropertyConsumptionOverMax() {
        System.out.println("loadPropertyNegativeConsumption");
        HouseVM instance = new HouseVM(mockedHouse);
        
        houseCurrentConsumption.set(15);
        houseMaxConsumption.set(10);
        
        assertEquals(1.0, instance.getLoad(), 0.01);
    }
    
    /**
     * Test of loadProperty method, of class HouseVM.
     */
    @Test
    public void testLoadPropertyNoConsumption() {
        System.out.println("loadPropertyNoConsumption");
        HouseVM instance = new HouseVM(mockedHouse);
        
        houseCurrentConsumption.set(0);
        houseMaxConsumption.set(10);
        
        assertEquals(0.0, instance.getLoad(), 0.01);
    }

    /**
     * Test of getLoad method, of class HouseVM.
     */
    @Test
    public void testGetLoad() {
        System.out.println("getLoad");
        HouseVM instance = new HouseVM(mockedHouse);
        
        double expResult = instance.loadProperty().get();
        double result = instance.getLoad();
        
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of fuseBlownProperty method, of class HouseVM.
     */
    @Test
    public void testFuseBlownPropertyTrue() {
        System.out.println("fuseBlownProperty");
        HouseVM instance = new HouseVM(mockedHouse);
        
        fuseBlown.set(true);
        
        assertEquals(true, instance.isFuseBlown());
    }
    
        /**
     * Test of fuseBlownProperty method, of class HouseVM.
     */
    @Test
    public void testFuseBlownPropertyFalse() {
        System.out.println("fuseBlownProperty");
        HouseVM instance = new HouseVM(mockedHouse);
        
        fuseBlown.set(false);
        
        assertEquals(false, instance.isFuseBlown());
    }

    /**
     * Test of isFuseBlown method, of class HouseVM.
     */
    @Test
    public void testIsFuseBlown() {
        System.out.println("isFuseBlown");
        HouseVM instance = new HouseVM(mockedHouse);
        
        boolean expResult = instance.fuseBlownProperty().get();
        boolean result = instance.isFuseBlown();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of repairFuse method, of class HouseVM.
     */
    @Test
    public void testRepairFuse() {
        System.out.println("repairFuse");
        HouseVM instance = new HouseVM(mockedHouse);
        
        instance.repairFuse();
        
        verify(mockedHouse).repairFuse();
    }
    
}

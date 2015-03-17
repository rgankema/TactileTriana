/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.deviceconfig.DeviceConfigVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
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
public class DeviceVMTest {
    private DeviceVM instance;
    private DeviceBase mockedDevice;
    private DoubleProperty deviceConsumption;
    private ObjectProperty<DeviceBase.State> deviceState;
    
    public DeviceVMTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        mockedDevice = mock(DeviceBase.class);
        deviceConsumption = new SimpleDoubleProperty(500d);
        deviceState = new SimpleObjectProperty(DeviceBase.State.CONNECTED);
        
        when(mockedDevice.currentConsumptionProperty()).thenReturn(deviceConsumption);
        when(mockedDevice.stateProperty()).thenReturn(deviceState);
        
        instance = new DeviceVM(mockedDevice);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of loadProperty method, of class DeviceVM.
     */
    @Test
    public void testLoadPropertyNoConsumption() {
        System.out.println("loadPropertyNoConsumption");
        
        deviceConsumption.set(0d);
        
        assertEquals(0d, instance.getLoad(), 0.0001);
    }

    /**
     * Test of loadProperty method, of class DeviceVM.
     */
    @Test
    public void testLoadPropertyHalfConsumption() {
        System.out.println("loadPropertyHalfConsumption");
        
        deviceConsumption.set(3700d/2d);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }

    /**
     * Test of loadProperty method, of class DeviceVM.
     */
    @Test
    public void testLoadPropertyHalfProduction() {
        System.out.println("loadPropertyHalfProduction");
        
        deviceConsumption.set(-3700d/2d);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }

    /**
     * Test of loadProperty method, of class DeviceVM.
     */
    @Test
    public void testLoadPropertyOverMaxProduction() {
        System.out.println("loadPropertyHalfProduction");
        
        deviceConsumption.set(4000d);
        
        assertEquals(1.0, instance.getLoad(), 0.01);
    }

    /**
     * Test of getLoad method, of class DeviceVM.
     */
    @Test
    public void testGetLoad() {
        System.out.println("getLoad");
        
        double expResult = instance.loadProperty().get();
        double result = instance.getLoad();
        
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of stateProperty method, of class DeviceVM.
     */
    @Test
    public void testStatePropertyConsumption() {
        System.out.println("statePropertyConsumption");
        
        deviceConsumption.set(5d);
        
        assertEquals(DeviceVM.State.CONSUMING, instance.getState());
    }

    /**
     * Test of stateProperty method, of class DeviceVM.
     */
    @Test
    public void testStatePropertyNoConsumption() {
        System.out.println("statePropertyConsumption");
        
        deviceConsumption.set(0d);
        
        assertEquals(DeviceVM.State.CONSUMING, instance.getState());
    }

    /**
     * Test of stateProperty method, of class DeviceVM.
     */
    @Test
    public void testStatePropertyProduction() {
        System.out.println("statePropertyConsumption");
        
        deviceConsumption.set(-5d);
        
        assertEquals(DeviceVM.State.PRODUCING, instance.getState());
    }
    
    /**
     * Test of stateProperty method, of class DeviceVM.
     */
    @Test
    public void testStatePropertyDisconnectedNotInHouse() {
        System.out.println("statePropertyConsumption");
        
        deviceState.set(DeviceBase.State.NOT_IN_HOUSE);
        deviceConsumption.set(-5d);
        
        assertEquals(DeviceVM.State.DISCONNECTED, instance.getState());
    }
    
        /**
     * Test of stateProperty method, of class DeviceVM.
     */
    @Test
    public void testStatePropertyDisconnectedNoPower() {
        System.out.println("statePropertyConsumption");
        
        deviceState.set(DeviceBase.State.DISCONNECTED);
        deviceConsumption.set(-5d);
        
        assertEquals(DeviceVM.State.DISCONNECTED, instance.getState());
    }

    /**
     * Test of getState method, of class DeviceVM.
     */
    @Test
    public void testGetState() {
        System.out.println("getState");
        
        DeviceVM.State expResult = instance.stateProperty().get();
        DeviceVM.State result = instance.getState();
        
        assertEquals(expResult, result);
    }
    
}

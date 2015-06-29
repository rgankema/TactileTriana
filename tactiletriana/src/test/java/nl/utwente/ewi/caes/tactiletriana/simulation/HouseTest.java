/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class HouseTest {
    
    private House instance;
    private Simulation mockedSimulation;
    
    @Before
    public void setUp() {
        mockedSimulation = mock(Simulation.class);
        when(mockedSimulation.currentTimeProperty()).thenReturn(new SimpleObjectProperty<>(LocalDateTime.of(2014, 1, 1, 0, 0)));
        
        instance = new House(mockedSimulation);
    }
    
    // CURRENT CONSUMPTION
    
    @Test
    public void testCurrentConsumptionProperty_NoDevices() {
        ReadOnlyDoubleProperty result = instance.currentConsumptionProperty();
        
        assertEquals(0.0, result.get(), 0.01);
    }
    
    @Test
    public void testCurrentConsumptionProperty_MultipleDevices() {
        int nDevices = 5;
        double deviceConsumption = instance.getMaximumConsumption()/5;
        for (int i = 0; i < nDevices; i++) {
            DeviceBase mockedDevice = new MockDevice(mockedSimulation, deviceConsumption);
            instance.getDevices().add(mockedDevice);
        }
        
        instance.tick(true);
        ReadOnlyDoubleProperty result = instance.currentConsumptionProperty();
        
        assertEquals(nDevices * deviceConsumption, result.get(), 0.01);
    }
    
    // FUSE PROPERTY
    
    @Test
    public void testConsumptionProperty_SetLessThanMax_FuseOkay() {
        DeviceBase mockedDevice = new MockDevice(mockedSimulation, instance.getMaximumConsumption()-1);
        instance.getDevices().add(mockedDevice);
        
        instance.tick(true);
        
        assertFalse(instance.isFuseBlown());
    }
    
    @Test
    public void testConsumptionProperty_SetHigherThanMax_FuseBlown() {
        DeviceBase mockedDevice = new MockDevice(mockedSimulation, instance.getMaximumConsumption()+1);
        instance.getDevices().add(mockedDevice);
        
        instance.tick(true);
        
        assertTrue(instance.isFuseBlown());
        assertEquals(0, instance.getCurrentConsumption(), 0.01);
    }
    
    @Test
    public void testRepairFuse_FuseBlownIsFalse() {
        instance.setFuseBlown(true);
        
        instance.repairFuse();
        
        assertFalse(instance.isFuseBlown());
    }
    
    
    @Test
    public void testBrokenProperty_SetTrue_DisconnectsChildren() {
        instance.setFuseBlown(true);
        MockDevice mockedDevice = new MockDevice(mockedSimulation, 0);
        instance.getDevices().add(mockedDevice);
        
        instance.tick(true);
        
        assert(mockedDevice.tickCalled());
    }
    
    // DEVICES
    
    @Test
    public void testDevices_Add_DeviceParentHouseEqualsHouse() {
        MockDevice mockedDevice = new MockDevice(mockedSimulation, 5);
        
        instance.getDevices().add(mockedDevice);
        
        assertEquals(mockedDevice.getParentHouse(), instance);
    }
    
    @Test
    public void testDevices_Remove_DeviceParentHouseIsNull() {
        MockDevice mockedDevice = new MockDevice(mockedSimulation, 5);
        
        instance.getDevices().add(mockedDevice);
        instance.getDevices().remove(mockedDevice);
        
        assertNull(mockedDevice.getParentHouse());
    }
}

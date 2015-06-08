/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
    
    /**
     * Test of currentConsumptionProperty method, of class House.
     */
    @Test
    public void testCurrentConsumptionPropertyNoDevices() {
        System.out.println("currentConsumptionPropertyNoDevices");
        
        ReadOnlyDoubleProperty result = instance.currentConsumptionProperty();
        
        assertEquals(0.0, result.get(), 0.01);
    }
    
    /**
     * Test of currentConsumptionProperty method, of class House.
     */
    @Test
    public void testCurrentConsumptionPropertyMultipleDevices() {
        System.out.println("currentConsumptionPropertyMultipleDevices");
        
        int nDevices = 5;
        double deviceConsumption = instance.getMaximumConsumption()/5;
        for (int i = 0; i < nDevices; i++) {
            // Mock devices
            DeviceBase device = mock(DeviceBase.class);
            when(device.currentConsumptionProperty()).thenReturn(new SimpleDoubleProperty(deviceConsumption));
            instance.getDevices().add(device);
        }
        
        instance.tick(true);
        ReadOnlyDoubleProperty result = instance.currentConsumptionProperty();
        
        assertEquals(nDevices * deviceConsumption, result.get(), 0.01);
    }
    
    @Test
    public void testFuseOkayWhenConsumptionIsLessThanMax() {
        System.out.println("fuseOkayWhenConsumptionIsLessThanMax");
        
        // Mock device
        DeviceBase device = mock(DeviceBase.class);
        when(device.currentConsumptionProperty()).thenReturn(new SimpleDoubleProperty(instance.getMaximumConsumption()-1));
        instance.getDevices().add(device);
        
        instance.tick(true);
        
        assertFalse(instance.isFuseBlown());
    }
    
    @Test
    public void testFuseBlowsWhenConsumptionIsMoreThanMax() {
        System.out.println("fuseBlowsWhenConsumptionIsMoreThanMax");
        
        // Mock device
        DeviceBase device = mock(DeviceBase.class);
        when(device.currentConsumptionProperty()).thenReturn(new SimpleDoubleProperty(instance.getMaximumConsumption()+1));
        when(device.stateProperty()).thenReturn(new SimpleObjectProperty<>(DeviceBase.State.CONNECTED));
        instance.getDevices().add(device);
        
        instance.tick(true);
        
        assertTrue(instance.isFuseBlown());
        assertEquals(0, instance.getCurrentConsumption(), 0.01);
    }
    
    @Test
    public void testRepairFuse() {
        System.out.println("repairFuse");
        
        instance.setFuseBlown(true);
        
        instance.repairFuse();
        
        assertFalse(instance.isFuseBlown());
    }
    
    @Test
    public void testBrokenFuseDisconnectsChildren() {
        System.out.println("brokenFuseDisconnectsChildren");
        
        instance.setFuseBlown(true);
        DeviceBase mockedDevice = mock(DeviceBase.class);
        when(mockedDevice.stateProperty()).thenReturn(new SimpleObjectProperty<>(DeviceBase.State.CONNECTED));
        when(mockedDevice.currentConsumptionProperty()).thenReturn(new SimpleDoubleProperty(0d));
        instance.getDevices().add(mockedDevice);
        
        instance.tick(true);
        
        verify(mockedDevice).doTick(false);
    }
}

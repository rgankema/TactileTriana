/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.MockDevice;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import org.junit.Before;
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
    private ObjectProperty<House> deviceParentHouse;
    private House house;
    
    @Before
    public void setUp() {
        house = new House(mock(Simulation.class));
        
        mockedDevice = mock(DeviceBase.class);
        deviceConsumption = new SimpleDoubleProperty(500d);
        deviceState = new SimpleObjectProperty(DeviceBase.State.CONNECTED);
        deviceParentHouse = new SimpleObjectProperty(house);
        
        when(mockedDevice.currentConsumptionProperty()).thenReturn(deviceConsumption);
        when(mockedDevice.stateProperty()).thenReturn(deviceState);
        when(mockedDevice.parentHouseProperty()).thenReturn(new SimpleObjectProperty<>(house));
        
        instance = new DeviceVM(mockedDevice);
    }

    // HEADER PROPERTY
    
    @Test
    public void testHeaderProperty_EqualsModelDisplayName() {
        MockDevice mockDevice = new MockDevice(mock(Simulation.class), 0);
        DeviceVM instance = new DeviceVM(mockDevice);
        
        String header = instance.getHeader();
        
        assertEquals(mockDevice.getDisplayName(), header);
    }
    
    // LOAD PROPERTY
    
    @Test
    public void testLoadProperty_NoConsumption_NoLoad() {
        deviceConsumption.set(0d);
        
        assertEquals(0d, instance.getLoad(), 0.0001);
    }

    @Test
    public void testLoadProperty_HalfConsumption_HalfLoad() {
        deviceConsumption.set(3700d/2d);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }

    @Test
    public void testLoadProperty_HalfProduction_HalfLoad() {
        deviceConsumption.set(-3700d/2d);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }

    @Test
    public void testLoadProperty_OverMaxProduction_CappedAt1() {
        deviceConsumption.set(4000d);
        
        assertEquals(1.0, instance.getLoad(), 0.01);
    }

    // STATE PROPERTY
    
    @Test
    public void testStateProperty_PositiveConsumption_StateIsConsuming() {
        deviceConsumption.set(5d);
        
        assertEquals(DeviceVM.State.CONSUMING, instance.getState());
    }

    @Test
    public void testStateProperty_NegativeConsumption_StateIsProducing() {
        deviceConsumption.set(-5d);
        
        assertEquals(DeviceVM.State.PRODUCING, instance.getState());
    }
    
    @Test
    public void testStateProperty_NotInHouse_StateIsDisconnected() {
        deviceState.set(DeviceBase.State.NOT_IN_HOUSE);
        deviceConsumption.set(-5d);
        
        assertEquals(DeviceVM.State.DISCONNECTED, instance.getState());
    }
    
    @Test
    public void testStateProperty_NoPower_StateIsDisconnected() {
        deviceState.set(DeviceBase.State.DISCONNECTED);
        deviceConsumption.set(-5d);
        
        assertEquals(DeviceVM.State.DISCONNECTED, instance.getState());
    }
    
    // DROPPED ON HOUSE
    
    @Test
    public void testDroppedOnHouse_NullParameter_RemovesModelFromHouse() {
        instance.droppedOnHouse(null);
        
        assertFalse(house.getDevices().contains(mockedDevice));
    }
    
    @Test
    public void testDroppedOnHouse_NotNullParameter_AddsModelToHouse() {
        House otherHouse = new House(mock(Simulation.class));
        HouseVM mockedHouseVM = mock(HouseVM.class);
        when(mockedHouseVM.getModel()).thenReturn(otherHouse);
        
        instance.droppedOnHouse(mockedHouseVM);
        
        assertTrue(otherHouse.getDevices().contains(mockedDevice));
    }
    
    // CONFIG ICON PRESSED
    
    @Test
    public void testConfigIconPressed_TogglesConfigPanelShown() {
        boolean wasConfigPanelShown = instance.isConfigPanelShown();
        
        instance.configIconPressed();
        
        assert(instance.isConfigPanelShown() != wasConfigPanelShown);
        
        instance.configIconPressed();
        
        assert(instance.isConfigPanelShown() == wasConfigPanelShown);
    }
}

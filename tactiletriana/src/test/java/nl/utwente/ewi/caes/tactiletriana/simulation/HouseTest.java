/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class HouseTest {

    /**
     * Test of toString method, of class House.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        House instance = new House();
        
        String expResult = "(House:P="+instance.getCurrentConsumption()+")";
        String result = instance.toString();
        
        assertEquals(expResult, result);
    }

    /**
     * Test of currentConsumptionProperty method, of class House.
     */
    @Test
    public void testCurrentConsumptionPropertyNoDevices() {
        System.out.println("currentConsumptionPropertyNoDevices");
        House instance = new House();
        
        ReadOnlyDoubleProperty result = instance.currentConsumptionProperty();
        
        assertEquals(0.0, result.get(), 0.01);
    }
    
    /**
     * Test of currentConsumptionProperty method, of class House.
     */
    @Test
    public void testCurrentConsumptionPropertyMultipleDevices() {
        System.out.println("currentConsumptionPropertyMultipleDevices");
        House instance = new House();
        int nDevices = 5;
        double deviceConsumption = 50.0;
        for (int i = 0; i < nDevices; i++) {
            DeviceBase device = mock(DeviceBase.class);
            when(device.currentConsumptionProperty()).thenReturn(new SimpleDoubleProperty(deviceConsumption));
            instance.getDevices().add(device);
        }
        
        ReadOnlyDoubleProperty result = instance.currentConsumptionProperty();
        
        assertEquals(nDevices * deviceConsumption, result.get(), 0.01);
    }
    
}

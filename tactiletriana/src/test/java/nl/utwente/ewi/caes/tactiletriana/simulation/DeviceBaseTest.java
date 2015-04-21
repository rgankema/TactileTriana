/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 *
 * @author niels
 */
public class DeviceBaseTest {
    
    
    /**
     * Test of addParameter method, of class DeviceBase.
     */
    @Test
    public void testAddParameter() {
        System.out.println("addParameter");
        DeviceBase.Parameter parameter = null;
        DeviceBase instance = new DeviceBaseImpl();
        instance.addParameter(parameter);
        assertEquals(0,instance.getParameters().size());
        instance.addParameter(new DeviceBase.Parameter(null,null,0,0));
        assertEquals(1,instance.getParameters().size());
    }

    /**
     * Test of tick method, of class DeviceBase.
     */
    @Test
    public void testTick() {
        System.out.println("tick");
        Simulation simulation = mock(Simulation.class);
        boolean connected = false;
        DeviceBase instance = new DeviceBaseImpl();
        instance.tick(simulation, connected);
        assertEquals(DeviceBase.State.DISCONNECTED,instance.getState());
        instance.tick(simulation, true);
        assertEquals(DeviceBase.State.CONNECTED,instance.getState());
        
        
    }

    public class DeviceBaseImpl extends DeviceBase {

        public DeviceBaseImpl() {
            super("", mock(Simulation.class));
        }
    }
    
}

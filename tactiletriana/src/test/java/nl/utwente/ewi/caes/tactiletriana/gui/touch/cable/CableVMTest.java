/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class CableVMTest {
    private Cable mockedCable;
    private DoubleProperty cableCurrent;
    private DoubleProperty cableMaxCurrent;
    private BooleanProperty cableBroken;
    
    @Before
    public void setUp() {
        mockedCable = mock(Cable.class);
        cableCurrent = new SimpleDoubleProperty();
        cableMaxCurrent = new SimpleDoubleProperty();
        cableBroken = new SimpleBooleanProperty();
        when(mockedCable.currentProperty()).thenReturn(cableCurrent);
        when(mockedCable.maximumCurrentProperty()).thenReturn(cableMaxCurrent);
        when(mockedCable.brokenProperty()).thenReturn(cableBroken);
    }

    // LOAD PROPERTY
    
    @Test
    public void testLoadProperty_PositiveCurrent() {
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(2);
        cableMaxCurrent.set(10);
        
        assertEquals(0.2, instance.getLoad(), 0.01);
    }
    
    @Test
    public void testLoadProperty_NegativeCurrent() {
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(-5);
        cableMaxCurrent.set(10);
        
        assertEquals(0.5, instance.getLoad(), 0.01);
    }
    
    @Test
    public void testLoadProperty_SetMoreThanMax_LoadCappedAt1() {
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(15);
        cableMaxCurrent.set(10);
        
        assertEquals(1.0, instance.getLoad(), 0.01);
    }

    // DIRECTION PROPERTY
    
    @Test
    public void testDirectionProperty_PositiveCurrent_DirectionIsEnd() {
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(5);
        
        assertEquals(CableVM.Direction.END, instance.getDirection());
    }
    
    @Test
    public void testDirectionProperty_NegativeCurrent_DirectionIsStart() {
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(-5);
        
        assertEquals(CableVM.Direction.START, instance.getDirection());
    }
    
    @Test
    public void testDirectionProperty_NoCurrent_DirectionIsNone() {
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(0);
        
        assertEquals(CableVM.Direction.NONE, instance.getDirection());
    }
    
    @Test
    public void testDirectionProperty_CableBroken_DirectionIsNone() {
        System.out.println("directionPropertyNoCurrent");
        CableVM instance = new CableVM(mockedCable);
        cableCurrent.set(5);
        cableBroken.set(true);
        
        assertEquals(CableVM.Direction.NONE, instance.getDirection());
    }
    
}

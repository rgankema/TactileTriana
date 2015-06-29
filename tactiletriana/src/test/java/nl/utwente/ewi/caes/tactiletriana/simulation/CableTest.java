/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import javafx.beans.property.SimpleObjectProperty;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import static org.mockito.Mockito.*;

/**
 *
 * @author niels
 */
public class CableTest {
    private static final double MAX_CURRENT = 100;
    private static final double LENGTH = 10;
    
    private Cable instance;
    private Simulation mockedSimulation;
    private Node mockedChildNode;
    
    @Before
    public void setUp() {
        mockedSimulation = mock(Simulation.class);
        when(mockedSimulation.currentTimeProperty()).thenReturn(new SimpleObjectProperty<>(LocalDateTime.of(2014, 1, 1, 1, 1)));
        mockedChildNode = mock(Node.class);
        instance = new Cable(mockedChildNode, MAX_CURRENT, LENGTH, mockedSimulation);
    }
    
    // LENGTH PROPERTY
    
    @Test
    public void testLengthProperty_SetPositive() {
        instance.lengthProperty().set(5);
        
        assertEquals(instance.lengthProperty().get(), 5, 0.01);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testLengthProperty_SetNegative_ThrowsException() {
        instance.lengthProperty().set(-5);
    }
    
    // CURRENT PROPERTY
    
    @Test
    public void testCurrentProperty_SetWithinRange() {
        instance.setCurrent(MAX_CURRENT * 0.5);
        instance.setCurrent(MAX_CURRENT * -0.5);
        
        assertFalse(instance.isBroken());
    }
    
    @Test
    public void testCurrentProperty_SetAboveMax_BreaksCable() {
        instance.setCurrent(MAX_CURRENT * 2);
        
        assert(instance.isBroken());
    }
    
    @Test
    public void testCurrentProperty_SetBelowAbsMax_BreaksCable() {
        instance.setCurrent(MAX_CURRENT * -2);
        
        assert(instance.isBroken());
    }
    
    // BROKEN
    
    @Test
    public void testBrokenProperty_SetTrue_NoCurrent() {
        instance.setCurrent(10);
        
        instance.setBroken(true);
        
        assertEquals(instance.getCurrent(), 0, 0.01);
    }

    @Test
    public void testRepair_BrokenIsFalse() {
        instance.setBroken(true);
        
        instance.repair();
        
        assertFalse(instance.isBroken());
    }

    // TICK
    
    @Test
    public void testTick_TrueConnected_CallsChildNodeTickTrue() {
        instance.tick(true);
        
        verify(instance.getChildNode()).tick(true);
    }
    
    @Test
    public void testTick_FalseConnected_CallsChildNodeTickFalse() {
        instance.setBroken(true);
        instance.tick(true);
        
        verify(instance.getChildNode()).tick(false);
    }
    
    @Test
    public void testPrepareForwardBackwardSweep_CallsChildNodePrepareForwardBackwardSweep() {
        instance.prepareForwardBackwardSweep();
        
        verify(mockedChildNode).prepareForwardBackwardSweep();
    }
    
    @Test
    public void testDoForwardBackwardSweep_CallsChildNodeDoForwardBackwardSweep() {
        instance.doForwardBackwardSweep(0);
        
        verify(mockedChildNode).doForwardBackwardSweep(0);
    }
    
    @Test
    public void testFinishForwardBackwardSweep_CallsChildNodeFinishForwardBackwardSweep() {
        instance.finishForwardBackwardSweep();
        
        verify(mockedChildNode).finishForwardBackwardSweep();
    }
    
}

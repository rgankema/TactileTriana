/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

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
        mockedChildNode = mock(Node.class);
        instance = new Cable(mockedChildNode, 100, 10, mockedSimulation);
    }

    /**
     * Test of lengthProperty method, of class Cable.
     */
    @Test
    public void testLengthProperty() {
        System.out.println("lengthProperty");
        
        assertNotNull(instance.lengthProperty());
        assertEquals(instance.lengthProperty().get(), LENGTH, 0.01);
    }
    
    @Test
    public void testLengthPropertyPositiveValue() {
        System.out.println("lengthPropertyPositiveValue");
        
        instance.lengthProperty().set(5);
        
        assertEquals(instance.lengthProperty().get(), 5, 0.01);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testLengthPropertyNotNegative() {
        System.out.println("lengthPropertyNotNegative");
        
        instance.lengthProperty().set(-5);
    }

    /**
     * Test of currentProperty method, of class Cable.
     */
    @Test
    public void testCurrentProperty() {
        System.out.println("currentProperty");
        
        assertNotNull(instance.currentProperty());
    }

    /**
     * Test of maximumCurrentProperty method, of class Cable.
     */
    @Test
    public void testMaximumCurrentProperty() {
        System.out.println("maximumCurrentProperty");
        
        assertNotNull(instance.maximumCurrentProperty());
        assertEquals(instance.maximumCurrentProperty().get(), MAX_CURRENT, 0.01);
    }
    
    @Test
    public void testCurrentAboveMaxBreaksCable() {
        System.out.println("currentAboveMaxBreaksCable");
        
        instance.setCurrent(MAX_CURRENT * 2);
        
        assert(instance.isBroken());
    }
    
    @Test
    public void testCurrentBelowAbsMaxBreaksCable() {
        System.out.println("currentBelowAbsMaxsBreaksCable");
        
        instance.setCurrent(MAX_CURRENT * -2);
        
        assert(instance.isBroken());
    }
    
    @Test
    public void testCurrentWithinRangeIsOkay() {
        System.out.println("currentWithinRangeIsOkay");
        
        instance.setCurrent(MAX_CURRENT * 0.5);
        instance.setCurrent(MAX_CURRENT * -0.5);
        
        assertFalse(instance.isBroken());
    }

    /**
     * Test of brokenProperty method, of class Cable.
     */
    @Test
    public void testBrokenProperty() {
        System.out.println("brokenProperty");
        
        assertNotNull(instance.brokenProperty());
    }
    
    @Test
    public void testNoCurrentWhenBroken() {
        System.out.println("noCurrentWhenBroken");
        
        instance.setCurrent(10);
        instance.setBroken(true);
        
        assertEquals(instance.getCurrent(), 0, 0.01);
    }

    /**
     * Test of repair method, of class Cable.
     */
    @Test
    public void testRepair() {
        System.out.println("repair");
        
        //break cable
        instance.setBroken(true);
        
        instance.repair();
        
        assertFalse(instance.isBroken());
    }

    
    @Test
    public void testTick() {
        System.out.println("tick");
        
        instance.tick(5, true);
        
        verify(instance.getChildNode()).tick(5, true);
    }
    
    @Test
    public void testTickBroken() {
        System.out.println("tickBroken");
        
        instance.setBroken(true);
        instance.tick(5, true);
        
        verify(instance.getChildNode()).tick(5, false);
    }
    
    /**
     * Test of doForwardBackwardSweep method, of class Cable.
     */
    @Test
    public void testDoForwardBackwardSweep() {
        System.out.println("doForwardBackwardSweep");
        double v = 200;
        Node childnode = mock(Node.class);
        when(childnode.doForwardBackwardSweep(200)).thenReturn(100.0);
        Cable instance = new Cable(childnode,100, 10, mock(Simulation.class));
        double expResult = 100;
        double result = instance.doForwardBackwardSweep(v);
        assertEquals(expResult, result, 0.01);
    }
    
}

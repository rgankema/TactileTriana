/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author niels
 */
public class CableTest {
    
    public CableTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of lengthProperty method, of class Cable.
     */
    @Test
    public void testLengthProperty() {
        System.out.println("lengthProperty");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        DoubleProperty result = instance.lengthProperty();
        assertNotNull(result);
    }

    /**
     * Test of getLength method, of class Cable.
     */
    @Test
    public void testGetLength() {
        System.out.println("getLength");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        double expResult = 10.0;
        double result = instance.getLength();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of setLength method, of class Cable.
     */
    @Test
    public void testSetLength() {
        System.out.println("setLength");
        double length = 15.0;
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        instance.setLength(length);
        assertEquals(length,instance.getLength(),0.0);
    }

    /**
     * Test of currentProperty method, of class Cable.
     */
    @Test
    public void testCurrentProperty() {
        System.out.println("currentProperty");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        ReadOnlyDoubleProperty result = instance.currentProperty();
        assertNotNull(result);
    }

    /**
     * Test of getCurrent method, of class Cable.
     */
    @Test
    public void testGetCurrent() {
        System.out.println("getCurrent");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        double expResult = 0.0;
        double result = instance.getCurrent();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of maximumCurrentProperty method, of class Cable.
     */
    @Test
    public void testMaximumCurrentProperty() {
        System.out.println("maximumCurrentProperty");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        double expResult = 100;
        ReadOnlyDoubleProperty result = instance.maximumCurrentProperty();
        assertEquals(expResult, result.get(), 0.0);
    }

    /**
     * Test of getMaximumCurrent method, of class Cable.
     */
    @Test
    public void testGetMaximumCurrent() {
        System.out.println("getMaximumCurrent");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        double expResult = 100;
        double result = instance.getMaximumCurrent();
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of brokenProperty method, of class Cable.
     */
    @Test
    public void testBrokenProperty() {
        System.out.println("brokenProperty");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        ReadOnlyBooleanProperty result = instance.brokenProperty();
        assertNotNull(result);
    }

    /**
     * Test of isBroken method, of class Cable.
     */
    @Test
    public void testIsBroken() {
        System.out.println("isBroken");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        boolean expResult = false;
        boolean result = instance.isBroken();
        assertEquals(expResult, result);
    }

    /**
     * Test of getChildNode method, of class Cable.
     */
    @Test
    public void testGetChildNode() {
        System.out.println("getChildNode");
        Node node = mock(Node.class);
        Cable instance = new Cable(node,100,mock(Simulation.class));
        Node expResult = node;
        Node result = instance.getChildNode();
        assertEquals(expResult, result);
    }

    /**
     * Test of tick method, of class Cable.
     */
    @Test
    public void testTick() {
        System.out.println("tick");
        Simulation simulation = mock(Simulation.class);
        boolean connected = false;
        Cable instance = new Cable(mock(Node.class),100,simulation);
        instance.tick(simulation, connected);
    }

    /**
     * Test of repair method, of class Cable.
     */
    @Test
    public void testRepair() {
        System.out.println("repair");
        Cable instance = new Cable(mock(Node.class),100,mock(Simulation.class));
        instance.repair();
        assertEquals(false,instance.isBroken());
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
        Cable instance = new Cable(childnode,100,mock(Simulation.class));
        double expResult = 100;
        double result = instance.doForwardBackwardSweep(v);
        assertEquals(expResult, result, 0.0);
    }
    
}

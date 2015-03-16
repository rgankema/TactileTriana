/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.node;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class NodeVMTest {
    private Node mockedNode;
    private DoubleProperty nodeVoltage;
    
    @Before
    public void setUp() {
        mockedNode = mock(Node.class);
        nodeVoltage = new SimpleDoubleProperty();
        when(mockedNode.voltageProperty()).thenReturn(nodeVoltage);
    }

    /**
     * Test of voltageErrorProperty method, of class NodeVM.
     */
    @Test
    public void testVoltageErrorPropertyPerfectVoltage() {
        System.out.println("voltageErrorPropertyPerfectVoltage");
        NodeVM instance = new NodeVM(mockedNode);
        
        nodeVoltage.set(230d);
        
        assertEquals(instance.getVoltageError(), 0.0, 0.01);
    }
    
    /**
     * Test of voltageErrorProperty method, of class NodeVM.
     */
    @Test
    public void testVoltageErrorPropertyVoltageOffBy5Percent() {
        System.out.println("voltageErrorPropertyOffBy5Percent");
        NodeVM instance = new NodeVM(mockedNode);
        
        nodeVoltage.set(230d + 23d/2d);
        
        assertEquals(instance.getVoltageError(), 0.5, 0.01);
    }
    
    /**
     * Test of voltageErrorProperty method, of class NodeVM.
     */
    @Test
    public void testVoltageErrorPropertyVoltageOffBy10Percent() {
        System.out.println("voltageErrorPropertyOffBy10Percent");
        NodeVM instance = new NodeVM(mockedNode);
        
        nodeVoltage.set(230d + 23d);
        
        assertEquals(instance.getVoltageError(), 1.0, 0.01);
    }
    
    /**
     * Test of voltageErrorProperty method, of class NodeVM.
     */
    @Test
    public void testVoltageErrorPropertyVoltageOffBy20Percent() {
        System.out.println("voltageErrorPropertyOffBy20Percent");
        NodeVM instance = new NodeVM(mockedNode);
        
        nodeVoltage.set(230d + 46d);
        
        assertEquals(instance.getVoltageError(), 1.0, 0.01);
    }
    
    /**
     * Test of voltageErrorProperty method, of class NodeVM.
     */
    @Test
    public void testVoltageErrorPropertyVoltageOffBy5PercentNegative() {
        System.out.println("voltageErrorPropertyOffBy5PercentNegative");
        NodeVM instance = new NodeVM(mockedNode);
        
        nodeVoltage.set(230d - 23d/2d);
        
        assertEquals(instance.getVoltageError(), 0.5, 0.01);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.List;
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
public class NodeTest {
    

    /**
     * Test of doForwardBackwardSweep method, of class Node.
     */
    @Test
    public void testDoForwardBackwardSweep() {
        System.out.println("doForwardBackwardSweep");
        double voltage = 230;
        Simulation sim = mock(Simulation.class);
        House house = new House(sim);
        Node instance = new Node(house,sim);
        double expResult = house.getCurrentConsumption()/instance.getVoltage();
        //test when there are no cables
        double result = instance.doForwardBackwardSweep(voltage);
        assertEquals(expResult, result, 0.0);
        
        //add two mocked cables and test again
        Cable c1 = mock(Cable.class);
        Cable c2 = mock(Cable.class);
        double testCurrent = 100.0;
        when(c1.doForwardBackwardSweep(voltage)).thenReturn(testCurrent);
        when(c2.doForwardBackwardSweep(voltage)).thenReturn(testCurrent);
        instance.getCables().add(c1);
        instance.getCables().add(c2);
        
        expResult = testCurrent*2 + house.getCurrentConsumption()/instance.getVoltage();
        result = instance.doForwardBackwardSweep(voltage);
        
        assertEquals(expResult, result, 0.0);
        
        
    }

    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Richard
 */
public class SimulationTest {
    
    public SimulationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getTransformer method, of class Simulation.
     */
    @Test
    public void testNetworkInitialization() {
        System.out.println("networkInitialization");
        Transformer transformer = Simulation.getInstance().getTransformer();
        
        // There's a transformer with one outgoing cable
        assertNotNull(transformer);
        assertEquals(1, transformer.getCables().size());
        
        // Loop over all internal nodes but the last
        NodeBase internalNode = transformer.getCables().stream().findAny().get().getChildNode();
        for (int i = 0; i < Simulation.NUMBER_OF_HOUSES - 1; i++) {
            List<CableBase> cables = internalNode.getCables();
            // Every internal node except the last has two outgoing cables
            assertEquals(2, cables.size());
            
            // This node has a cable going to node that has a house and no cables...
            assertTrue(cables.stream().anyMatch(c -> c.getChildNode().getHouse() != null && c.getChildNode().getCables().isEmpty()));
            // ... and it has a node that has no house but at least one outgoing cable
            assertTrue(cables.stream().anyMatch(c -> c.getChildNode().getCables().size() > 0));
            for (CableBase c: cables) {
                if (c.getChildNode().getCables().size() > 0) {
                    assertNull(c.getChildNode().getHouse());
                    internalNode = c.getChildNode();
                }
            }
        }
        
        // The last internal node has one outgoing cable, which leads to a node with a house and no cables
        assertEquals(1, internalNode.getCables().size());
        assertTrue(internalNode.getCables().stream().findAny().get().getChildNode().getHouse() != null);
        assertTrue(internalNode.getCables().stream().findAny().get().getChildNode().getCables().isEmpty());
    }
    
}

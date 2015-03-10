/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author niels
 */
public class SimulationTest {
    
    public SimulationTest() {
        instance = Simulation.getInstance();
    }
    
    public Simulation instance;
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of getInstance method, of class Simulation.
     */
    @Test
    public void testGetInstance() {
        // Singleton functie hoeft niet getest worden.
    }

    /**
     * Test of getTransformer method, of class Simulation.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Simulation instance = Simulation.getInstance();
        Transformer result = instance.getTransformer();
        System.out.println(result.toString());

    }

    /**
     * Test of start method, of class Simulation.
     */
    @Test
    public void testStart() {
        System.out.println("start");
        
    }

    /**
     * Test of stop method, of class Simulation.
     */
    @Test
    public void testStop() {
        System.out.println("stop");
        
    }
    
}

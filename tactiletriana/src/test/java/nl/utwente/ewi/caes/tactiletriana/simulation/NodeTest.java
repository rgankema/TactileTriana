/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Richard
 */
public class NodeTest {
    private List<Cable> mockedCables;
    private House mockedHouse;
    private Simulation mockedSimulation;
    
    @Before
    public void setUp() {
        mockedCables = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Cable mockedCable = mock(Cable.class);
            mockedCables.add(mockedCable);
        }
        mockedHouse = mock(House.class);
        mockedSimulation = mock(Simulation.class);
    }

    // TICK

    @Test
    public void testTick_TrueConnected_CallsChildrenTickTrue() {
        Node instance = new Node(mockedHouse, mockedSimulation);
        instance.getCables().addAll(mockedCables);
        
        instance.tick(true);
        
        for (Cable mockedCable : mockedCables) {
            verify(mockedCable).tick(true);
        }
        verify(mockedHouse).tick(true);
    }
    
    @Test
    public void testTick_FalseConnected_CallsChildrenTickFalse() {
        Node instance = new Node(mockedHouse, mockedSimulation);
        instance.getCables().addAll(mockedCables);
        
        instance.tick(false);
        
        for (Cable mockedCable : mockedCables) {
            verify(mockedCable).tick(false);
        }
        verify(mockedHouse).tick(false);
    }
    
    // PREPARE FORWARD BACKWARD SWEEP

    @Test
    public void testPrepareForwardBackwardSweep_CallsCables() {
        Node instance = new Node(null, mockedSimulation);
        instance.getCables().addAll(mockedCables);
        
        instance.prepareForwardBackwardSweep();
        
        for (Cable mockedCable : mockedCables) {
            verify(mockedCable).prepareForwardBackwardSweep();
        }
    }
    
    @Test
    public void testPrepareForwardBackwardSweep_TempVoltageTo230() {
        Node instance = new Node(null, mockedSimulation);
        
        instance.prepareForwardBackwardSweep();
        
        assertEquals(instance.tempVoltage, 230, 0.01);
    }

    // DO FORWARD BACKWARD SWEEP
    
    @Test
    public void testDoForwardBackwardSweep_CallsCables() {
        Node instance = new Node(null, mockedSimulation);
        instance.getCables().addAll(mockedCables);
        
        instance.doForwardBackwardSweep(230);
        
        for (Cable mockedCable : mockedCables) {
            verify(mockedCable).doForwardBackwardSweep(230);
        }
    }
    
    @Test
    public void testDoForwardBackwardSweep_SetsTempVoltage() {
        Node instance = new Node(null, mockedSimulation);
        instance.getCables().addAll(mockedCables);
        
        instance.doForwardBackwardSweep(240);
        
        assertEquals(instance.tempVoltage, 240, 0.01);
    }

    // FINISH FORWARD BACKWARD SWEEP
    
    @Test
    public void testFinishForwardBackwardSweep_SetsVoltageToTempVoltage() {
        Node instance = new Node(null, mockedSimulation);
        instance.getCables().addAll(mockedCables);
        
        // Assert nothing changed yet
        assertEquals(instance.getVoltage(), 230, 0.01);
        
        instance.doForwardBackwardSweep(240);
        
        assertEquals(instance.tempVoltage, 240, 0.01);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.Buffer;
import nl.utwente.ewi.caes.tactiletriana.simulation.prediction.SimulationPrediction;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author mickvdv
 */
public class BufferTest {
    
    Simulation sim;
    
    public void initializeSimulation(){
        GlobalSettings.load(GlobalSettings.DEFAULT_FILE);
        sim = new Simulation();
    }
    
    @Test
    public void Buffer_ConsumptionIsNull(){
        initializeSimulation();
        Buffer b = new Buffer(sim);
        sim.houses[0].getDevices().add(b);
        sim.tick();
        assertEquals(0, b.getCurrentConsumption(), 0);
    }
    
    @Test
    public void Buffer_Charge(){
        initializeSimulation();
        Buffer b = new Buffer(sim);
        sim.houses[0].getDevices().add(b);
        sim.tick();
        b.setCurrentConsumption(100d);
        sim.tick();
        assertEquals(100d * GlobalSettings.TICK_MINUTES / 60d, b.getStateOfCharge(), 0.0000001);
    }
    
}

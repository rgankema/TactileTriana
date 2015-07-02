/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.List;
import javafx.scene.chart.XYChart.Data;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import static nl.utwente.ewi.caes.tactiletriana.Util.toMinuteOfYear;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.ElectricVehicle;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.SolarPanel;
import nl.utwente.ewi.caes.tactiletriana.simulation.prediction.SimulationPrediction;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author mickvdv
 */
public class SimulationPredictionTest {
    final int NUMBER_OF_HOURS_TO_TEST = 1;
    Simulation sim;
    SimulationPrediction pred;
    
    public void initializeSimulation(){
        GlobalSettings.load(GlobalSettings.DEFAULT_FILE);
        sim = new Simulation();
        pred = new SimulationPrediction(sim);
    }
    
    @Ignore // Haal weg als je wil testen, standaard ignoren omdat test lang duurt
    @Test
    public void EmptySimulationTest() throws InterruptedException{
        initializeSimulation();
        checkPredictionCorrect();
    }
    
    @Ignore // Haal weg als je wil testen, standaard ignoren omdat test lang duurt
    @Test
    public void SolarPanelTest() throws InterruptedException{
        initializeSimulation();
        SolarPanel solar = new SolarPanel(sim);
        sim.houses[0].getDevices().add(solar);
        
        // check of het solarpanel er in zit
        assertTrue(sim.houses[0].getDevices().contains(solar));
        
        checkPredictionCorrect();
    }
    
    @Ignore // Haal weg als je wil testen, standaard ignoren omdat test lang duurt
    @Test
    public void ElectricVehicleTest() throws InterruptedException{
        initializeSimulation();
        
        ElectricVehicle ev = new ElectricVehicle(sim);
        sim.houses[0].getDevices().add(ev);
        
        checkPredictionCorrect();
        
        ev.setModel(ElectricVehicle.Model.TESLA_MODEL_S);
        checkPredictionCorrect();
        
        ev.setModel(ElectricVehicle.Model.BMW_I3);
        checkPredictionCorrect();
        
        ev.setModel(ElectricVehicle.Model.TESLA_MODEL_S);
        // add a lot of cars so the fuse will blow
        ElectricVehicle ev2 = new ElectricVehicle(sim);
        sim.houses[0].getDevices().add(ev2);
        
        ElectricVehicle ev3 = new ElectricVehicle(sim);
        sim.houses[0].getDevices().add(ev2);
        checkPredictionCorrect();
        
        ev2.setModel(ElectricVehicle.Model.TESLA_MODEL_S);
        ev3.setModel(ElectricVehicle.Model.TESLA_MODEL_S);
        checkPredictionCorrect();
        
        assertTrue(sim.houses[0].isFuseBlown());
        
        sim.houses[0].getDevices().remove(ev2);
        sim.houses[0].getDevices().remove(ev3);
        checkPredictionCorrect();
        
        repairFuses();
        checkPredictionCorrect();
        
    }
    
    private void repairFuses(){
        for (House h : sim.houses){
            h.repairFuse();
        }
        for (Cable c : sim.houseCables){
            c.repair();
        }
        for (Cable c : sim.internalCables){
            c.repair();
        }
    }
    
    private void checkPredictionCorrect() throws InterruptedException {
        
        // loop X uur vooruit
        int numberOfTicks = NUMBER_OF_HOURS_TO_TEST * 60 * 60 / GlobalSettings.TICK_MINUTES;
       
        
        for(int i = 0; i < numberOfTicks; i++){
            sim.tick();
            
            // let the parallel test do its work
            Thread.sleep(10);
            
            Float simValue = findValueForTime(sim.getLog(), sim.getCurrentTime().minusMinutes(GlobalSettings.TICK_MINUTES));
            Float predValue = findValueForTime(pred.getLog(), sim.getCurrentTime().minusMinutes(GlobalSettings.TICK_MINUTES));
            assertEquals(simValue, predValue);
       }
    }
    
    private Float findValueForTime(List<Data<Integer,Float>> log, LocalDateTime time){
        Integer value = toMinuteOfYear(time);
        for (int i = 0; i < log.size(); i++){
            Data<Integer,Float> datapoint = log.get(i);
            if (datapoint.getXValue().equals(value)){
                return datapoint.getYValue();
            }
        }
        
        return null;
    }
}

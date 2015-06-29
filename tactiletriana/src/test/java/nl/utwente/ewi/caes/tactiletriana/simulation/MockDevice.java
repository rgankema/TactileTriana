/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import org.json.simple.JSONObject;

/**
 * MockDevice used for unit tests. Mocking DeviceBase using Mockito is troublesome
 * because for some reason final fields in abstract classes do not get initialised
 * when mocking them with Mockito.
 * 
 * @author Richard
 */
public class MockDevice extends DeviceBase {
    private final double consumption;
    private boolean tickCalled = false;
    
    public MockDevice(SimulationBase simulation, double consumption) {
        super(simulation, "mock", "mock");
        
        this.consumption = consumption;
    }

    public boolean tickCalled() {
        return tickCalled;
    }
    
    @Override
    protected void doTick(boolean connected) {
        setCurrentConsumption(consumption);
        tickCalled = true;
    }

    @Override
    protected JSONObject parametersToJSON() {
        return new JSONObject();
    }
    
}

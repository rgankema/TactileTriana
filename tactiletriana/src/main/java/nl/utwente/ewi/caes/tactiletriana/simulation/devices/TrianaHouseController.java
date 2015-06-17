/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import org.json.simple.JSONObject;

/**
 *
 * @author Richard
 */
public class TrianaHouseController extends DeviceBase {

    public TrianaHouseController(SimulationBase simulation) {
        super(simulation, "House Controller", "HouseController");
    }

    @Override
    protected void doTick(boolean connected) {
        // Dummy device, do nothing
    }

    @Override
    protected JSONObject parametersToJSON() {
        // No parameters, so return empty object
        return new JSONObject();
    }
    
}

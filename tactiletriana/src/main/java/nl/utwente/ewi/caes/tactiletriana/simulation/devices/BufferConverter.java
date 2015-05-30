/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import org.json.simple.JSONObject;

/**
 *
 * @author niels
 */
public class BufferConverter extends BufferBase {
    
    public static final String API_COP = "COP";
        
    public BufferConverter(SimulationBase simulation, String displayName, String apiDeviceType) {
        super(simulation, displayName, "BufferConverter");
        
        registerProperty(COP);
        
        registerAPIParameter(API_COP);
        
    }
    
    /**
     * Coefficient of performance of this BufferConverter
     */
    private final DoubleProperty COP = new SimpleDoubleProperty();
    
    public DoubleProperty COPProperty() {
        return COP;
    }
    
    public double getCOP() {
        return COP.get();
    }

    public void setCOP(double power) {
        this.COP.set(power);
    }
    
    @Override
    protected JSONObject parametersToJSON() {
        JSONObject result = super.parametersToJSON();
                
        result.put(API_COP, getCOP());
        
        return result;
    }
    
    @Override
    public void updateParameter(String parameter, Object value){
        if(parameter.equals(API_COP)){
            setCOP((double) value);
        } else {                      
            super.updateParameter(parameter, value);
        }        
    }
    
    @Override
    protected void doTick(boolean connected) {
        // TODO implement this
    }
    
}

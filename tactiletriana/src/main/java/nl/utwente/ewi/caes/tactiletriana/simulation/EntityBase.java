/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

/**
 *
 * @author mickvdv
 */
public class EntityBase {
    public EntityBase(){
        characteristicMap = new HashMap<LocalDateTime, Double>();
    }
    
    public static enum CharacteristicType {
        Current, Consumption, Voltage
    }
    
    protected double characteristicAbsMax = Double.POSITIVE_INFINITY;
    public double getCharacteristicAbsMax(){
        return this.characteristicAbsMax;
    }
    
    protected CharacteristicType characteristic;
    public CharacteristicType getCharacteristic(){
        return this.characteristic;
    }
    
    protected Map<LocalDateTime, Double> characteristicMap;
    
    public Map<LocalDateTime, Double> getCharacteristicMap(){
        return this.characteristicMap;
    }
}

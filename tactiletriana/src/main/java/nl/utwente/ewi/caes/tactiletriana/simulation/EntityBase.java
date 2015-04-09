/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedMap;

/**
 *
 * @author mickvdv
 */
public abstract class EntityBase {
    public static enum CharacteristicType {
        Current, Consumption, Voltage
    }
    
    protected CharacteristicType characteristic;
    public CharacteristicType getCharacteristic(){
        return this.characteristic;
    }
    
    SortedMap<LocalDateTime, Double> characteristicMap;
    
    public Map<LocalDateTime, Double> getCharacteristicMap(){
        return this.characteristicMap;
    }
}

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
public class Entity {
    protected double characteristicAbsMax = Double.POSITIVE_INFINITY;
    protected CharacteristicType characteristic;
    
    public Entity(){
        characteristicMap = new HashMap<>();
    }
    
    public static enum CharacteristicType {
        CURRENT, POWER, VOLTAGE
    }
    
    public double getCharacteristicAbsMax(){
        return this.characteristicAbsMax;
    }
    
    public CharacteristicType getCharacteristic(){
        return this.characteristic;
    }
    
    protected Map<LocalDateTime, Double> characteristicMap;
    
    public Map<LocalDateTime, Double> getCharacteristicMap(){
        return this.characteristicMap;
    }
}

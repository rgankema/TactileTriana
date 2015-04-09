/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDateTime;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 *
 * @author mickvdv
 */
public class Entity {
    private final CharacteristicType type;
    private final ObservableMap<LocalDateTime, Double> characteristicMap;
    
    protected double characteristicAbsMax = Double.POSITIVE_INFINITY;
    
    public Entity(CharacteristicType type){
        this.type = type;
        
        characteristicMap = FXCollections.observableMap(new HashMap<>());
    }
    
    // PROPERTIES
    
    public double getCharacteristicAbsMax(){
        return this.characteristicAbsMax;
    }
    
    public CharacteristicType getCharacteristic(){
        return this.type;
    }
    
    public ObservableMap<LocalDateTime, Double> getCharacteristicMap(){
        return this.characteristicMap;
    }
    
    // ENUMS
    
    public static enum CharacteristicType {
        CURRENT, POWER, VOLTAGE
    }
}

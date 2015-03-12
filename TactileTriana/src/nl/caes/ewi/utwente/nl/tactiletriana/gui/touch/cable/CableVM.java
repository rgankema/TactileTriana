/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable;

import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import nl.caes.ewi.utwente.nl.tactiletriana.simulation.CableBase;

/**
 *
 * @author Richard
 */
public class CableVM {
    private CableBase model;
    
    public CableVM(CableBase model) {
        this.model = model;
        
        load.bind(Bindings.createDoubleBinding(() -> {
            return Math.min(1.0, Math.abs(model.getCurrent()) / model.getMaximumCurrent()); 
        }, model.currentProperty(), model.maximumCurrentProperty()));
        
        direction.bind(Bindings.createObjectBinding(() -> { 
            if (!model.isBroken()) {
               if (model.getCurrent() < 0) return Direction.START;
               if (model.getCurrent() > 0) return Direction.END;
            }
            return Direction.NONE;
        }, model.brokenProperty(), model.currentProperty()));
    }
    
    /**
     * The load on the cable on a scale from 0 to 1.
     */
    private final ReadOnlyDoubleWrapper load = new ReadOnlyDoubleWrapper(0);
    
    public double getLoad() {
        return load.get();
    }
    
    public ReadOnlyDoubleProperty loadProperty() {
        return load.getReadOnlyProperty();
    }
    
    public boolean isBroken() {
        return brokenProperty().get();
    }
    
    public ReadOnlyBooleanProperty brokenProperty() {
        return this.model.brokenProperty();
    }
    
    /**
     * The direction of the current
     */
    private final ReadOnlyObjectWrapper<Direction> direction = new ReadOnlyObjectWrapper<>(Direction.NONE);
    
    public Direction getDirection() {
        return direction.get();
    }
    
    public ReadOnlyObjectProperty<Direction> directionProperty() {
        return direction.getReadOnlyProperty();
    }
    
    /**
     * Describes the direction of current in a cable
     */
    public enum Direction { 
        /**
         * The current goes towards the start node
         */
        START,
        /**
         * The current goes towards the end node
         */
        END, 
        /**
         * There's no current flowing
         */
        NONE 
    };      
        
        
}

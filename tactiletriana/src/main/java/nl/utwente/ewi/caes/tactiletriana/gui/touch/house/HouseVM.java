/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.house;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.HouseBase;

/**
 *
 * @author Richard
 */
public class HouseVM {
    private HouseBase model;
    
    public HouseVM(HouseBase model) {
        this.model = model;
        this.load.bind(Bindings.createDoubleBinding(() -> {
            return Math.min(1.0, Math.abs(model.getCurrentConsumption()) / model.getMaximumConsumption());
        }, model.currentConsumptionProperty(), model.maximumConsumptionProperty()));
    }
    
    /**
     * The load of the house on a scale of 0 to 1. The load is the absolute amount
     * of consumption of the house, divided by the maximum consumption. When this
     * is higher than 1, load will still return 1.
     */
    private final ReadOnlyDoubleWrapper load = new ReadOnlyDoubleWrapper(0.0);
    
    public ReadOnlyDoubleProperty loadProperty() {
        return load;
    }
    
    public double getLoad() {
        return loadProperty().get();
    }
    
    /**
     * @return Whether the house's fuse is blown or not.
     */
    public ReadOnlyBooleanProperty fuseBlownProperty() {
        return model.fuseBlownProperty();
    }
    
    public boolean isFuseBlown() {
        return fuseBlownProperty().get();
    }
    
    /**
     * Repairs a blown fuse. If the fuse is not blown, nothing happens
     */
    public void repairFuse() {
        this.model.repairFuse();
    }
}

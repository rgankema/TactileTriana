/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.test.mock;

import javafx.animation.AnimationTimer;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.CableBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.NodeBase;

/**
 *
 * @author Richard
 */
public class SimpleMockCable extends CableBase {
    public SimpleMockCable() {
    }
    
    DoubleProperty current = new SimpleDoubleProperty(5);
    
    @Override
    public ReadOnlyDoubleProperty currentProperty() {
        return current;
    }

    @Override
    public ReadOnlyDoubleProperty maximumCurrentProperty() {
        return new SimpleDoubleProperty(10.0);
    }

    BooleanProperty broken = new SimpleBooleanProperty(false);
    
    @Override
    public ReadOnlyBooleanProperty brokenProperty() {
        return broken;
    }

    @Override
    public NodeBase getChildNode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double doForwardBackwardSweep(double v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetEntity(double voltage, double current) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.node;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import nl.utwente.ewi.caes.tactiletriana.App;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;

/**
 *
 * @author Richard
 */
public class NodeVM {

    private Node model;

    public NodeVM(Node model) {
        this.model = model;

        voltageErrorProperty.bind(Bindings.createDoubleBinding(() -> {
            double difference = Math.abs(230 - this.model.getVoltage());
            return Math.min(1.0, difference / 23.0);
        }, model.voltageProperty()));

        if (App.DEBUG) {
            debugString.bind(Bindings.createStringBinding(() -> {
                return String.format("V: %.3f", model.getVoltage());
            }, model.voltageProperty()));
        }
    }
    
    public Node getModel() {
        return model;
    }
    

    /**
     * Defines how far off the voltage is in the node on a scale of 0 to 1. 0
     * means the voltage is at 230, 1 means it's off with at least 10%.
     */
    private final ReadOnlyDoubleWrapper voltageErrorProperty = new ReadOnlyDoubleWrapper(0);

    public double getVoltageError() {
        return voltageErrorProperty.get();
    }

    public ReadOnlyDoubleProperty voltageErrorProperty() {
        return voltageErrorProperty.getReadOnlyProperty();
    }

    /**
     * Debug string
     */
    private final ReadOnlyStringWrapper debugString = new ReadOnlyStringWrapper();

    public ReadOnlyStringProperty debugStringProperty() {
        return debugString.getReadOnlyProperty();
    }
}

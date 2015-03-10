/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node;

<<<<<<< HEAD
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
=======
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
>>>>>>> Eerste poging tot View + ViewModel + Controller
import nl.caes.ewi.utwente.nl.tactiletriana.simulation.INode;

/**
 *
 * @author Richard
 */
public class NodeVM {
    private INode model;
    
    public NodeVM(INode model) {
        this.model = model;
        this.model.voltageProperty().addListener(x -> {
            double difference = Math.abs(230 - this.model.voltageProperty().get());
            setVoltageError(Math.min(1.0, difference/23.0)); 
        });
    }
    
    /**
     * Defines how far off the voltage is in the node from a scale of 0.0 to 1.0.
     * 0.0 means the voltage is at 230, 1.0 means it's off with at least 10%.
     */    
    private final DoubleProperty voltageErrorProperty = new SimpleDoubleProperty(0);
    
    public double getVoltageError() {
        return voltageErrorProperty.get();
    }
    

    private void setVoltageError(double value) {
        voltageErrorProperty.set(value);
    }
    
    public DoubleProperty voltageErrorProperty() {
        return voltageErrorProperty;
    }
}

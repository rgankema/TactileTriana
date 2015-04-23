/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

/**
 *
 * @author Richard
 */
public class Node extends LoggingEntityBase implements IFWBWSEntity {

    private final List<Cable> cables;
    private House house;

    public Node(House house, Simulation simulation) {
        super(QuantityType.VOLTAGE, simulation, EntityType.NODE);

        this.cables = new ArrayList<>();
        this.house = house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
    /**
     * The voltage measured on this node
     */
    private final ReadOnlyDoubleWrapper voltage = new ReadOnlyDoubleWrapper(230.0) {
        @Override
        public void set(double value) {
            log(value);
            super.set(value);
        }
    };

    public ReadOnlyDoubleProperty voltageProperty() {
        return voltage.getReadOnlyProperty();
    }

    public final double getVoltage() {
        return voltageProperty().get();
    }

    protected void setVoltage(double voltage) {
        this.voltage.set(voltage);
    }

    /**
     *
     * @return a list of cables that connect to this node
     */
    public List<Cable> getCables() {
        return this.cables;
    }

    /**
     *
     * @return the house this Node is connected to, may be null
     */
    public House getHouse() {
        return this.house;
    }

    public void tick(Simulation simulation, boolean connected) {
        if (getHouse() != null) {
            getHouse().tick(simulation, connected);
        }
        for (Cable cable : getCables()) {
            cable.tick(simulation, connected);
        }
    }

    // FORWARD BACKWARD SWEEP METHODS
    private double tempVoltage;
    
    @Override
    public void prepareForwardBackwardSweep() {
        tempVoltage = 230d;

        for (Cable c : cables) {
            c.prepareForwardBackwardSweep();
        }
    }
    
    @Override
    public double doForwardBackwardSweep(double voltage) {
        double current = 0.0;

        //Forward sweep, update the voltages
        tempVoltage = voltage;

        for (Cable c : cables) {
            current += c.doForwardBackwardSweep(this.getVoltage());
        }
        if (house != null) {
            if (this.getVoltage() != 0) {
                current += (house.getCurrentConsumption() / this.getVoltage()); //I = P/U //Apparently this one is inversed?
            }
        }
        return current;
    }

    @Override
    public void finishForwardBackwardSweep() {
        setVoltage(tempVoltage);
        for (Cable c : cables) {
            c.finishForwardBackwardSweep();
        }
    }

    public String toString(int indentation) {
        String output = "";
        for (int i = 0; i < indentation; i++) {
            output += "\t";
        }
        output += "|-";

        if (getHouse() != null) {
            output = "(Node:U=" + getVoltage() + ")\n" + getHouse().toString(indentation + 1) + "\n";
        } else {
            output = "(Node:U=" + getVoltage() + ") -> " + "\n";
        }
        for (Cable c : this.getCables()) {
            output += c.toString(indentation + 1);
        }
        return output;
    }
}

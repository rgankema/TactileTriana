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
public class Node extends LoggingEntityBase {

    private final List<Cable> cables;
    private House house;

    protected SimulationBase simulation;

    public Node(House house, SimulationBase simulation) {
        super("Node", QuantityType.VOLTAGE);

        this.simulation = simulation;
        this.cables = new ArrayList<>();
        this.house = house;
    }

    public void setHouse(House house) {
        this.house = house;
    }
    /**
     * The voltage measured on this node
     */
    private final ReadOnlyDoubleWrapper voltage = new ReadOnlyDoubleWrapper(230.0);

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

    /**
     *
     * @param connected whether this Node is connected to the root of the
     * network
     */
    public void tick(boolean connected) {
        if (getHouse() != null) {
            getHouse().tick(connected);
        }
        for (Cable cable : getCables()) {
            cable.tick(connected);
        }
    }

    // FORWARD BACKWARD SWEEP METHODS
    double tempVoltage;

    public void prepareForwardBackwardSweep() {
        tempVoltage = 230d;

        for (Cable c : cables) {
            c.prepareForwardBackwardSweep();
        }
    }

    public double doForwardBackwardSweep(double voltage) {
        double current = 0.0;

        //Forward sweep, update the voltages
        tempVoltage = voltage;

        for (Cable c : cables) {
            current += c.doForwardBackwardSweep(tempVoltage);
        }
        if (house != null) {
            if (this.getVoltage() != 0) {
                current += (house.getCurrentConsumption() / tempVoltage); //I = P/U //Apparently this one is inversed?
            }
        }
        return current;
    }

    public void finishForwardBackwardSweep() {
        setVoltage(tempVoltage);
        log(simulation.getCurrentTime(), getVoltage());
        for (Cable c : cables) {
            c.finishForwardBackwardSweep();
        }
    }
}

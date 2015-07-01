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
 * Node in the Simulation. Can be connected to a House and / or Cables
 */
public class Node extends LoggingEntityBase {

    private final List<Cable> cables;
    private House house;

    protected SimulationBase simulation;

    /**
     * Creates a Node object connected to a Houses.
     * @param house House attached to this Node
     * @param simulation the Simulation this Node is simulated in
     */
    public Node(House house, SimulationBase simulation) {
        super("Node", UnitOfMeasurement.VOLTAGE);

        this.simulation = simulation;
        this.cables = new ArrayList<>();
        this.house = house;
    }

    /**
     * Create a Node
     * @param simulation the Simulation this Node is simulated in
     */
    public Node(SimulationBase simulation) {
        super("Node", UnitOfMeasurement.VOLTAGE);

        this.simulation = simulation;
        this.cables = new ArrayList<>();
    }
    
    /**
     * Sets the house of this node
     * @param house house of this node
     */

    public void setHouse(House house) {
        this.house = house;
    }
    /**
     * The voltage measured on this node
     */
    private final ReadOnlyDoubleWrapper voltage = new ReadOnlyDoubleWrapper(230.0);

    /**
     * 
     * @return voltage property on this Node (in V)
     */
    public ReadOnlyDoubleProperty voltageProperty() {
        return voltage.getReadOnlyProperty();
    }

    /**
     * 
     * @return voltage on this Node (in V)
     */
    public final double getVoltage() {
        return voltageProperty().get();
    }

    /**
     * Sets voltage on this Node
     * @param voltage voltage on this Node (in V)
     */
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
     * Tick 
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

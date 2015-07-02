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
 * Represents a Node in the energy grid. A Node has cables, and optionally a 
 * House.
 * 
 * @author Richard
 */
public class Node extends LoggingEntityBase {

    private final List<Cable> cables;
    private House house;

    protected SimulationBase simulation;

    /**
     * Constructs a new Node with a House and a SimulationBase. House may actually
     * be {@code null}, in which case the Node is not connected to a house.
     * 
     * @param house         The House this Node connects to, may be {@code null}
     * @param simulation    The SimulationBase that the Node is part of
     */
    public Node(House house, SimulationBase simulation) {
        super("Node", UnitOfMeasurement.VOLTAGE);

        if (simulation == null) throw new NullPointerException("Simulation may not be null");
        
        this.simulation = simulation;
        this.cables = new ArrayList<>();
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
     * A list of all cables that connect to this node, except the cable that
     * comes from the direction of the transformer.
     * 
     * @return a list of {@code Cable}s
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

    /**
     * Prepares this Node and all children cables and optional house for a new
     * forwardBackwardSweep by setting voltages to 230 and current to 0.
     */
    public void prepareForwardBackwardSweep() {
        tempVoltage = 230d;

        for (Cable c : cables) {
            c.prepareForwardBackwardSweep();
        }
    }

    /**
     * Performs an iteration of the forward backward sweep algorithm. Its own 
     * voltage is set to the given parameter, and the current of its house and
     * cables is returned. Note that the {@link voltageProperty voltageProperty} 
     * won't actually be set until {@link finishForwardBackwardSweep finishForwardBackwardSweep}
     * is called.
     * 
     * @param voltage   the voltage for this Node
     * @return the current of its cables and optional house
     */
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

    /**
     * Sets {@link voltageProperty voltageProperty} to the voltage given in the
     * last call of {@link doForwardBackwardSweep doForwardBackwardSweep}.
     */
    public void finishForwardBackwardSweep() {
        setVoltage(tempVoltage);
        log(simulation.getCurrentTime(), getVoltage());
        for (Cable c : cables) {
            c.finishForwardBackwardSweep();
        }
    }
}

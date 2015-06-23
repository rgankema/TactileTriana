/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * A connection between to nodes
 */
public class Cable extends LoggingEntityBase {

    private final Node childNode;
    private final double resistance;
    private final SimulationBase simulation;

    /**
     * Instantiates a new cable connected to two nodes
     *
     * @param childNode The node away from the transformer
     * @param maxCurrent The maximum current that can flow through the cable
     * @param length The length of the cable
     * @param simulation The Simulation this cable belongs to
     */
    public Cable(Node childNode, double maxCurrent, double length, SimulationBase simulation) {
        super("Cable", QuantityType.CURRENT);
        this.childNode = childNode;
        this.resistance = 0.0005;
        this.simulation = simulation;
        setLength(length);
        setMaximumCurrent(maxCurrent);
    }

    // PROPERTIES
    /**
     * The length of the cable
     */
    private final DoubleProperty length = new SimpleDoubleProperty(10) {
        @Override
        public void set(double value) {
            if (value <= 0) {
                throw new IllegalArgumentException("Length must be a positive value");
            }

            super.set(value);
        }
    };

    public DoubleProperty lengthProperty() {
        return length;
    }

    public final double getLength() {
        return length.get();
    }

    public final void setLength(double length) {
        this.length.set(length);
    }

    /**
     * The current that flows through the current measured in ampere.
     */
    private final ReadOnlyDoubleWrapper current = new ReadOnlyDoubleWrapper(0.0) {
        @Override
        public void set(double value) {
            if (Math.abs(value) > getMaximumCurrent()) {
                setBroken(true);
            }
            super.set(value);
        }
    };

    public ReadOnlyDoubleProperty currentProperty() {
        return current.getReadOnlyProperty();
    }

    public final double getCurrent() {
        return currentProperty().get();
    }

    protected final void setCurrent(double value) {
        current.set(value);
    }

    /**
     * The absolute maximum current that can flow through the cable before it
     * breaks;
     */
    private final ReadOnlyDoubleWrapper maximumCurrent = new ReadOnlyDoubleWrapper(100d);

    public ReadOnlyDoubleProperty maximumCurrentProperty() {
        return maximumCurrent.getReadOnlyProperty();
    }

    public final double getMaximumCurrent() {
        return maximumCurrentProperty().get();
    }

    protected final void setMaximumCurrent(double maximumCurrent) {
        this.maximumCurrent.set(maximumCurrent);
    }

    /**
     * Whether the cable is broken or not
     */
    private final ReadOnlyBooleanWrapper broken = new ReadOnlyBooleanWrapper(false) {
        @Override
        public void set(boolean value) {
            if (value) {
                setCurrent(0);
            }
            super.set(value);
        }
    };

    public ReadOnlyBooleanProperty brokenProperty() {
        return broken;
    }

    public final boolean isBroken() {
        return brokenProperty().get();
    }

    protected final void setBroken(boolean value) {
        broken.set(value);
    }

    /**
     *
     * @return the node that is the child
     */
    public Node getChildNode() {
        return this.childNode;
    }

    // METHODS
    public void tick(boolean connected) {
        if (isBroken()) {
            connected = false;
        }

        getChildNode().tick(connected);
    }

    /**
     * Repairs a broken cable. Does nothing if the cable is not broken.
     */
    public void repair() {
        setBroken(false);
    }

    // FORWARD BACKWARD SWEEP
    private double tempCurrent;

    public void prepareForwardBackwardSweep() {
        tempCurrent = 0;
        getChildNode().prepareForwardBackwardSweep();
        
    }

    public double doForwardBackwardSweep(double voltage) {
        //update the voltages in the forward sweep
        voltage = voltage - (tempCurrent * (resistance * getLength()));

        tempCurrent = getChildNode().doForwardBackwardSweep(voltage);

        return tempCurrent;
    }

    public void finishForwardBackwardSweep() {
        setCurrent(tempCurrent);
        log(simulation.getCurrentTime(), getCurrent());
        childNode.finishForwardBackwardSweep();
    }
}

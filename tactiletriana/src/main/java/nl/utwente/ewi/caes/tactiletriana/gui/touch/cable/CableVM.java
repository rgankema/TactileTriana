/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;

/**
 *
 * @author Richard
 */
public class CableVM {

    private Cable model;

    public CableVM(Cable model) {
        this.model = model;

        load.bind(Bindings.createDoubleBinding(() -> {
            return Math.min(1.0, Math.abs(model.getCurrent()) / model.getMaximumCurrent());
        }, model.currentProperty(), model.maximumCurrentProperty(), model.brokenProperty()));

        direction.bind(Bindings.createObjectBinding(() -> {
            if (!model.isBroken()) {
                if (model.getCurrent() < 0) {
                    return Direction.START;
                }
                if (model.getCurrent() > 0) {
                    return Direction.END;
                }
            }
            return Direction.NONE;
        }, model.brokenProperty(), model.currentProperty()));
    }

    public Cable getModel() {
        return model;
    }
    
    // PROPERTIES
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
     * The maximum current the cable can handle
     */
    public ReadOnlyDoubleProperty maximumCurrentProperty() {
        return model.maximumCurrentProperty();
    }

    public double getMaximumCurrent() {
        return model.getMaximumCurrent();
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

    // METHODS
    public void cablePressed() {
        model.repair();
    }

    /**
     * To be used by the CableView to bind the model's cable length to some
     * double binding
     *
     * @param length A double binding that the cable model will bind to
     */
    public void bindCableLength(DoubleBinding length) {
        model.lengthProperty().bind(length);
    }

    // ENUMS
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

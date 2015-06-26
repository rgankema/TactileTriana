/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.LoggingEntityVMBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;

/**
 *
 * @author Richard
 */
public class CableVM extends LoggingEntityVMBase {

    private Cable model;

    public CableVM(Cable model) {
        this.model = model;

        load.bind(Bindings.createDoubleBinding(() -> {
            return Math.min(1.0, Math.abs(model.getCurrent()) / model.getMaximumCurrent());
        }, model.currentProperty(), model.maximumCurrentProperty(), model.brokenProperty()));

        model.brokenProperty().addListener(obs -> {
            direction.set(Direction.NONE);
        });

        model.currentProperty().addListener(obs -> {
            if (model.getCurrent() > 0) {
                direction.set(Direction.END);
            } else if (model.getCurrent() < 0) {
                direction.set(Direction.START);
            } else {
                direction.set(Direction.NONE);
            }
        });
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

    // EVENT HANDLING
    public void pressed() {
        model.repair();
    }

    public void longPressed() {
        StageController.getInstance().showOnChart(this, model);
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

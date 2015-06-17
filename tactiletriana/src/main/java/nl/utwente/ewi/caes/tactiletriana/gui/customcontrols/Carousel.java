/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.customcontrols;

import java.util.function.Function;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 * Carousel control specifically designed for selecting values for a
 * CategoryParameter. Can only be used with touch.
 *
 * @author Richard
 */
public class Carousel extends BorderPane {

    @FXML Label valueLabel;
    @FXML Node leftArrow;
    @FXML Node rightArrow;

    private ObjectProperty property;
    private Function<Object, String> objectToString;
    private Object[] possibleValues;
    private int index = -1;

    /**
     * Constructs a new Carousel control
     *
     * @param property The property that is affected by the Carousel
     * @param objectToString Mapping from selected object to string
     * @param possibleValues Array of values that the user should be able to
     * choose from
     */
    public Carousel(ObjectProperty property, Function<Object, String> objectToString, Object... possibleValues) {
        ViewLoader.load(this);

        this.property = property;
        this.objectToString = objectToString;
        this.possibleValues = possibleValues;

        update();

        leftArrow.setOnTouchPressed(e -> {
            index--;
            if (index < 0) {
                index = possibleValues.length - 1;
            }
            property.set(possibleValues[index]);
            e.consume();
        });

        rightArrow.setOnTouchPressed(e -> {
            index++;
            if (index >= possibleValues.length) {
                index = 0;
            }
            property.set(possibleValues[index]);
            e.consume();
        });

        property.addListener(i -> update());
    }

    /**
     * Updates the index and value label
     */
    private void update() {
        for (int i = 0; i < possibleValues.length; i++) {
            if (possibleValues[i].equals(property.get())) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalStateException("Current value of property not among possible values");
        }

        valueLabel.setText(objectToString.apply(possibleValues[index]));
    }
}

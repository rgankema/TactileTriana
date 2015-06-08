/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device.deviceconfig;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.customcontrols.Carousel;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.deviceconfig.DeviceConfigVM.CategoryRow;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.deviceconfig.DeviceConfigVM.DoubleRow;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.device.deviceconfig.DeviceConfigVM.Row;

/**
 * A panel for configuring device parameters. Has two columns, with labels on
 * the left side and sliders on the right side.
 *
 * @author Richard
 */
public class DeviceConfigView extends GridPane {

    @FXML
    private Label header;

    private int currentRow = 1;

    public DeviceConfigView(DeviceConfigVM viewModel) {
        ViewLoader.load(this);

        this.header.textProperty().bind(viewModel.headerTextProperty());

        for (Row row : viewModel.getRows()) {
            if (row instanceof DoubleRow) {
                DoubleRow dRow = (DoubleRow) row;
                Slider slider = buildSlider(dRow.getMin(), dRow.getMax(), dRow.getValueProperty());
                addControl(dRow.getLabel(), slider, dRow.getValueStringBinding());
            } else if (row instanceof CategoryRow) {
                CategoryRow cRow = (CategoryRow) row;
                Carousel carousel = new Carousel(cRow.getValueProperty(), x -> cRow.getValueStringBinding().getValue(), cRow.getPossibleValues());
                addControl(cRow.getLabel(), carousel, null);
            }
        }
    }

    /**
     * Convenience method that returns a Slider control with its value bound to
     * a given property.
     *
     * @param min the minimum value of the slider
     * @param max the maximum value of the slider
     * @param property the property to bind to
     * @return a Slider control
     */
    private Slider buildSlider(double min, double max, Property<Number> property) {
        Slider result = buildSlider(min, max);
        result.valueProperty().bindBidirectional(property);
        return result;
    }

    /**
     * Convenience method that returns a Slider control.
     *
     * @param min the minimum value of the slider
     * @param max the maximum value of the slider
     * @return a Slider control
     */
    private Slider buildSlider(double min, double max) {
        Slider result = new Slider(min, max, min);
        // Consume touch events so that the deviceview won't be dragged while using the slider
        result.addEventFilter(TouchEvent.ANY, e -> e.consume());
        return result;
    }

    /**
     * Adds a control to a new row.
     *
     * @param label the name for the type of value that can be configured
     * @param control the control to add
     * @param valueString a binding to the value of the control in a specific
     * string format, may be null
     */
    private void addControl(String label, Node control, StringBinding valueString) {
        this.add(new Label(label), 0, currentRow);
        this.add(control, 1, currentRow);
        if (valueString != null) {
            Label valueLabel = new Label();
            valueLabel.textProperty().bind(valueString);
            this.add(valueLabel, 2, currentRow);
        }
        currentRow++;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.Parameter;

/**
 * A panel for configuring device parameters. Has two columns, with labels on
 * the left side and sliders on the right side.
 *
 * @author Richard
 */
class DeviceConfigView extends GridPane {

    public DeviceConfigView(List<Parameter> parameters) {
        int row = 0;
        for (Parameter p : parameters) {
            this.add(new Label(p.displayName), 0, row);

            Slider s = new Slider(p.minValue, p.maxValue, p.property.get());

            // consume touch events so that the deviceview can't be dragged while using the slider
            // todo: fix TactilePane so that this isn't necessary anymore
            s.addEventFilter(TouchEvent.ANY, e -> e.consume());

            p.property.bindBidirectional(s.valueProperty());
            this.add(s, 1, row);
            row++;
        }
        
        
    }
}

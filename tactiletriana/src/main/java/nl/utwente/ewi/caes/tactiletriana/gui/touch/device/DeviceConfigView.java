/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.customcontrols.Carousel;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.CategoryParameter;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.DoubleParameter;
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
            this.add(new Label(p.getDisplayName()), 0, row);
            
            if (p instanceof DoubleParameter) {
                DoubleParameter dp = (DoubleParameter)p;
                Slider s = new Slider(dp.getMin(), dp.getMax(), dp.getProperty().get());

                // consume touch events so that the deviceview can't be dragged while using the slider
                // todo: fix TactilePane so that this isn't necessary anymore
                s.addEventFilter(TouchEvent.ANY, e -> e.consume());

                p.getProperty().bindBidirectional(s.valueProperty());
                this.add(s, 1, row);
            } else { // p instanceof CategoryParameter
                CategoryParameter cp = (CategoryParameter)p;
                Carousel c = new Carousel(cp.getProperty(), o -> cp.getCurrentValueDisplayName(), cp.getPossibleValues());
                
                this.add(c, 1, row);
            }
            row++;
        }
        
        
    }
}

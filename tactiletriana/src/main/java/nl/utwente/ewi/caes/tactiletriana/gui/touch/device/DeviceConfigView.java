/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import java.util.List;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.Parameter;

/**
 * A panel for configuring device parameters. Has two columns, with labels on
 * the left side and sliders on the right side.
 * @author Richard
 */
class DeviceConfigView extends GridPane {
    
    public DeviceConfigView(List<Parameter> parameters) {
        int row = 0;
        for (Parameter p : parameters) {
            this.add(new Label(p.displayName), 0, row);
            System.out.println(p.property);
            Slider s = new Slider(p.minValue, p.maxValue, p.property.get());
            p.property.bindBidirectional(s.valueProperty());
            this.add(s, 1, row);
            row++;
        }
    }
}

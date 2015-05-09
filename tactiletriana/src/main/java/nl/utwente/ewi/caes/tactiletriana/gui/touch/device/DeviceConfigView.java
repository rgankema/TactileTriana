/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.customcontrols.Carousel;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.ConfigurableCategory;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.ConfigurableDouble;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.Configurable;

/**
 * A panel for configuring device parameters. Has two columns, with labels on
 * the left side and sliders on the right side.
 *
 * @author Richard
 */
class DeviceConfigView extends GridPane {
    @FXML private Label header;
    
    public DeviceConfigView(String header, List<Configurable> parameters) {
        ViewLoader.load(this);
        
        this.header.setText(header);
        
        int row = 1;
        for (Configurable p : parameters) {
            this.add(new Label(p.getDisplayName()), 0, row);
            
            if (p instanceof ConfigurableDouble) {
                ConfigurableDouble dp = (ConfigurableDouble)p;
                Slider s = new Slider(dp.getMin(), dp.getMax(), dp.getProperty().get());

                // consume touch events so that the deviceview can't be dragged while using the slider
                // todo: fix TactilePane so that this isn't necessary anymore
                s.addEventFilter(TouchEvent.ANY, e -> e.consume());

                p.getProperty().bindBidirectional(s.valueProperty());
                this.add(s, 1, row);
            } else { // p instanceof CategoryParameter
                ConfigurableCategory cp = (ConfigurableCategory)p;
                Carousel c = new Carousel(cp.getProperty(), o -> cp.getCurrentValueDisplayName(), cp.getPossibleValues());
                
                this.add(c, 1, row);
            }
            row++;
        }
        
        
    }
}

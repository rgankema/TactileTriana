/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.deviceconfig;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase.Parameter;

/**
 *
 * @author Richard
 */
public class DeviceConfigView extends GridPane {
    private DeviceConfigVM viewModel;
    
    public DeviceConfigView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(DeviceConfigVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel already set");
        
        this.viewModel = viewModel;
        
        int row = 0;
        for (Parameter p : viewModel.getParameters()) {
            this.add(new Label(p.displayName), 0, row);
            System.out.println(p.property);
            Slider s = new Slider(p.minValue, p.maxValue, p.property.get());
            p.property.bindBidirectional(s.valueProperty());
            this.add(s, 1, row);
            row++;
        }
    }
}

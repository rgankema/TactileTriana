/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.customcontrols.Carousel;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.Buffer;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.ElectricVehicle;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.SolarPanel;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.TimeShiftableBase;

/**
 * A panel for configuring device parameters. Has two columns, with labels on
 * the left side and sliders on the right side.
 *
 * @author Richard
 */
class DeviceConfigView extends GridPane {
    @FXML private Label header;
    
    public DeviceConfigView(DeviceBase device) {
        ViewLoader.load(this);
        
        this.header.setText(device.getDisplayName());
        
        if (device instanceof Buffer) {     // Buffer
            Buffer buffer = (Buffer) device;
            Slider capacity = buildSlider(5000, 20000, buffer.capacityProperty());
            Slider maxPower = buildSlider(500, 3700, buffer.maxPowerProperty());
            addControl("Capacity", capacity);
            addControl("Max Power", maxPower);
        } else if (device instanceof TimeShiftableBase) {   // WashingMachine and Dishwasher
            TimeShiftableBase timeShiftable = (TimeShiftableBase) device;
            Slider startTime = buildSlider(0, 24*60 - 1, timeShiftable.startTimeProperty());
            Slider delay = new Slider();
            delay.setMin(0);
            delay.setMax(24*60 - timeShiftable.getStaticProfile().length);
            delay.valueProperty().addListener(obs -> { 
                timeShiftable.setEndTime((int) ((timeShiftable.getStartTime() + delay.getValue()) % (24 * 60)));
            });
            // consume touch events so that the deviceview can't be dragged while using the slider
            delay.addEventFilter(TouchEvent.ANY, e -> e.consume());
            addControl("Start Time", startTime);
            addControl("Delay", delay);
        } else if (device instanceof SolarPanel) {  // SolarPanel
            SolarPanel solarPanel = (SolarPanel) device;
            Slider area = buildSlider(1, 50, solarPanel.areaProperty());
            addControl("Area", area);
        } else if (device instanceof ElectricVehicle) { // EV
            ElectricVehicle electricVehicle = (ElectricVehicle) device;
            Carousel model = new Carousel(electricVehicle.modelProperty(), ev -> electricVehicle.getModelName(), ElectricVehicle.Model.values());
            addControl("Model", model);
        }
    }
    
    private Slider buildSlider(double min, double max, Property<Number> property) {
        Slider result = new Slider();
        result.setMin(min);
        result.setMax(max);
        result.valueProperty().bindBidirectional(property);
        
        // consume touch events so that the deviceview can't be dragged while using the slider
        // todo: fix TactilePane so that this isn't necessary anymore
        result.addEventFilter(TouchEvent.ANY, e -> e.consume());
        
        return result;
    }
    
    private int row = 1;
    private void addControl(String label, Node control) {
        this.add(new Label(label), 0, row);
        this.add(control, 1, row);
        row++;
    }
}

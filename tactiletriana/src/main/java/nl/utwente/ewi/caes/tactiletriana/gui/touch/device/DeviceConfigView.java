/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.Util;
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
    
    private int row = 1;
    
    // TODO clean up this spaghetti code
    public DeviceConfigView(DeviceBase device) {
        ViewLoader.load(this);
        
        this.header.setText(device.getDisplayName());
        
        if (device instanceof Buffer) {     // Buffer
            Buffer buffer = (Buffer) device;
            Slider capacity = buildSlider(5000, 20000, buffer.capacityProperty());
            Slider maxPower = buildSlider(500, 3700, buffer.maxPowerProperty());
            addControl("Capacity", capacity, capacity.valueProperty().asString("%.0f Wh"));
            addControl("Max Power", maxPower, maxPower.valueProperty().asString("%.0f W"));
        } else if (device instanceof TimeShiftableBase) {   // WashingMachine and Dishwasher
            TimeShiftableBase timeShiftable = (TimeShiftableBase) device;
            Slider startTime = buildSlider(0, 24 * 60 - 1);
            Slider delay = buildSlider(0, 24*60 - timeShiftable.getStaticProfile().length);
            
            // These controls are a little less straightforward since they are not entirely
            // the same in the simulation as they are in the GUI
            startTime.valueProperty().addListener(obs -> { 
                double d = delay.getValue();
                timeShiftable.setStartTime((int) startTime.getValue());
                timeShiftable.setEndTime((int) (startTime.getValue() + d) % (24 * 60));
            });
            delay.valueProperty().addListener(obs -> { 
                timeShiftable.setEndTime((int) ((timeShiftable.getStartTime() + delay.getValue()) % (24 * 60)));
            });
            timeShiftable.startTimeProperty().addListener(obs -> {
                startTime.setValue(timeShiftable.getStartTime());
                delay.setValue((timeShiftable.getEndTime() - timeShiftable.getStartTime() + 24 * 60) % (24 * 60));
            });
            timeShiftable.endTimeProperty().addListener(obs -> { 
                delay.setValue((timeShiftable.getEndTime() - timeShiftable.getStartTime() + 24 * 60) % (24 * 60));
            });
            addControl("Start Time", startTime, Bindings.createStringBinding(() -> { 
                return Util.minutesToTimeString((int) startTime.getValue());
            }, startTime.valueProperty()));
            addControl("Delay", delay , Bindings.createStringBinding(() -> {
                return Util.minutesToTimeString((int) delay.getValue());
            }, delay.valueProperty()));
            
        } else if (device instanceof SolarPanel) {  // SolarPanel
            SolarPanel solarPanel = (SolarPanel) device;
            Slider area = buildSlider(1, 50, solarPanel.areaProperty());
            addControl("Area", area, area.valueProperty().asString("%.0f m²"));
        } else if (device instanceof ElectricVehicle) { // EV
            ElectricVehicle electricVehicle = (ElectricVehicle) device;
            Carousel model = new Carousel(electricVehicle.modelProperty(), ev -> electricVehicle.getModelName(), (Object[]) ElectricVehicle.Model.values());
            addControl("Model", model, null);
        }
    }
    
    /**
     * Convenience method that returns a Slider control with its value bound to 
     * a given property.
     * 
     * @param min       the minimum value of the slider
     * @param max       the maximum value of the slider
     * @param property  the property to bind to
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
     * @param min       the minimum value of the slider       
     * @param max       the maximum value of the slider
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
     * @param label         the name for the type of value that can be configured
     * @param control       the control to add
     * @param valueString   a binding to the value of the control in a specific string format, may be null
     */
    private void addControl(String label, Node control, StringBinding valueString) {
        this.add(new Label(label), 0, row);
        this.add(control, 1, row);
        if (valueString != null) {
            Label valueLabel = new Label();
            valueLabel.textProperty().bind(valueString);
            this.add(valueLabel, 2, row);
        }
        row++;
    }
}

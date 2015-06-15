/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.device.deviceconfig;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableObjectValue;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import nl.utwente.ewi.caes.tactiletriana.Util;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.Buffer;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.BufferConverter;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.ElectricVehicle;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.ElectricVehicle.Model;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.SolarPanel;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.TimeShiftableBase;

/**
 * The ViewModel for the device configuration panel
 *
 * @author Richard
 */
public class DeviceConfigVM {

    private final List<Row> rows = new ArrayList<>();

    // CONSTRUCTOR
    
    public DeviceConfigVM(DeviceBase device) {

        headerText.set(device.getDisplayName());

        if (device instanceof Buffer) {
            initBuffer((Buffer) device);
        } else if (device instanceof TimeShiftableBase) {
            initTimeShiftable((TimeShiftableBase) device);
        } else if (device instanceof SolarPanel) {
            initSolarPanel((SolarPanel) device);
        } else if (device instanceof ElectricVehicle) {
            initElectricVehicle((ElectricVehicle) device);
        } else if (device instanceof BufferConverter) {
            initBufferConverter((BufferConverter) device);
        } // else no parameters available
    }

    private void initBuffer(Buffer buffer) {
        DoubleProperty capacity = buffer.capacityProperty();
        DoubleProperty maxPower = buffer.maxPowerProperty();

        rows.add(new DoubleRow("Capacity", capacity, 5000, 20000, capacity.asString("%.0f Wh")));
        rows.add(new DoubleRow("Max Power", maxPower, 500, 3700, maxPower.asString("%.0f W")));
    }

    private void initTimeShiftable(TimeShiftableBase ts) {
        DoubleProperty startTime = new SimpleDoubleProperty(ts.getStartTime());
        DoubleProperty delay = new SimpleDoubleProperty((ts.getEndTime() - ts.getStartTime() + (24 * 60)) % (24 * 60));

        ts.startTimeProperty().addListener(obs -> {
            startTime.set(ts.getStartTime());
            delay.set((ts.getEndTime() - ts.getStartTime() + (24 * 60)) % (24 * 60));
        });

        ts.endTimeProperty().addListener(obs -> {
            delay.set((ts.getEndTime() - ts.getStartTime() + (24 * 60)) % (24 * 60));
        });

        startTime.addListener(obs -> {
            int d = (ts.getEndTime() - ts.getStartTime() + (24 * 60)) % (24 * 60);
            ts.setStartTime((int) startTime.get());
            ts.setEndTime((int) (startTime.get() + d) % (24 * 60));
        });

        delay.addListener(obs -> {
            ts.setEndTime((int) (startTime.get() + delay.get()) % (24 * 60));
        });

        rows.add(new DoubleRow("Start Time", startTime, 0, 24 * 60, Bindings.createStringBinding(() -> {
            return Util.minutesToTimeString((int) startTime.get());
        }, startTime)));
        rows.add(new DoubleRow("Delay", delay, 0, 24 * 60 - ts.getStaticProfile().length, Bindings.createStringBinding(() -> {
            return Util.minutesToTimeString((int) delay.get());
        }, delay)));
    }

    private void initSolarPanel(SolarPanel solarPanel) {
        DoubleProperty area = solarPanel.areaProperty();

        rows.add(new DoubleRow("Area", area, 1, 50, area.asString("%.0f m²")));
        
        if (GlobalSettings.EXTENDED_PARAMETERS) {
            DoubleProperty efficiency = solarPanel.efficiencyProperty();
            DoubleProperty orientation = solarPanel.orientationProperty();
            DoubleProperty elevation = solarPanel.elevationProperty();
            
            rows.add(new DoubleRow("Efficiency", efficiency, 0, 100, efficiency.asString("%.0f%%")));
            rows.add(new DoubleRow("Orientation", orientation, 0, 360, orientation.asString("%.0f°")));
            rows.add(new DoubleRow("Elevation", elevation, 0, 90, elevation.asString("%.0f°")));
        }
    }

    private void initElectricVehicle(ElectricVehicle ev) {
        ObjectProperty<Model> model = ev.modelProperty();

        rows.add(new CategoryRow("Model", model, Bindings.createStringBinding(() -> ev.getModelName(), model), Model.values()));
    }

    private void initBufferConverter(BufferConverter bc) {
        if (GlobalSettings.EXTENDED_PARAMETERS) {
            DoubleProperty efficiency = bc.COPProperty();
            
            rows.add(new DoubleRow("Performance Coefficient", efficiency, 0.5, 10, efficiency.asString("%.1f")));
        }
    }
    
    // PROPERTIES
    
    /**
     * The text that should be shown in the header.
     */
    private final StringProperty headerText = new SimpleStringProperty();

    public ReadOnlyStringProperty headerTextProperty() {
        return headerText;
    }

    /**
     * @return a list of rows in the panel
     */
    public List<Row> getRows() {
        return rows;
    }

    // NESTED CLASSES
    
    /**
     * Represents a row in the configuration view.
     */
    public abstract class Row {

        private final String label;
        private final Property valueProperty;
        private final ObservableObjectValue<String> valueStringBinding;

        public Row(String label, Property valueProperty, ObservableObjectValue<String> valueStringBinding) {
            this.label = label;
            this.valueProperty = valueProperty;
            this.valueStringBinding = valueStringBinding;
        }

        public String getLabel() {
            return label;
        }

        public Property getValueProperty() {
            return valueProperty;
        }

        public ObservableObjectValue<String> getValueStringBinding() {
            return valueStringBinding;
        }
    }

    /**
     * Represents a row in the configuration view where a certain double value
     * will be configured. This value has a minimum and maximum value.
     */
    public final class DoubleRow extends Row {

        private final double min;
        private final double max;

        public DoubleRow(String label, DoubleProperty valueProperty, double min, double max, ObservableObjectValue<String> valueStringBinding) {
            super(label, valueProperty, valueStringBinding);

            this.min = min;
            this.max = max;
        }

        @Override
        public DoubleProperty getValueProperty() {
            return (DoubleProperty) super.getValueProperty();
        }

        public double getMin() {
            return min;
        }

        public double getMax() {
            return max;
        }
    }

    /**
     * Represents a row in the configuration view where a certain category value
     * will be configured. A category value is a value that is not numeric.
     */
    public final class CategoryRow extends Row {

        private final Object[] possibleValues;

        public CategoryRow(String label, Property valueProperty, ObservableObjectValue<String> valueStringBinding, Object[] possibleValues) {
            super(label, valueProperty, valueStringBinding);

            this.possibleValues = possibleValues;
        }

        @Override
        public ObjectProperty getValueProperty() {
            return (ObjectProperty) super.getValueProperty();
        }

        public Object[] getPossibleValues() {
            return possibleValues;
        }
    }
}

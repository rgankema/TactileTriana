package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;

import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;

/**
 *
 * @author mickvdv
 */
public class MockDevice extends DeviceBase {
    private static final double MIN_CONSUMPTION = -3700d;
    private static final double MAX_CONSUMPTION = 3700d;
    private final Parameter[] PARAMETERS = new Parameter[] {
        new Parameter("Consumption", consumptionProperty(), MIN_CONSUMPTION, MAX_CONSUMPTION)
    };
    
    /**
     * The amount of power the device will consume when turned on
     */
    private final DoubleProperty consumption = new SimpleDoubleProperty() {
        @Override
        public void set(double value) {
            if (get() == value) return;
            if (value < MIN_CONSUMPTION) value = MIN_CONSUMPTION;
            if (value > MAX_CONSUMPTION) value = MAX_CONSUMPTION;
            
            super.set(value);
        }
    };
    
    public double getConsumption() {
        return consumption.get();
    }
    
    public void setConsumption(double consumption) {
        this.consumption.set(consumption);
    }
    
    public DoubleProperty consumptionProperty() {
        return consumption;
    }
    
    /**
     * The amount of power the device currently consumes
     */
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(0.0);
    
    @Override
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption;
    }
    
    @Override
    public Parameter[] getParameters() {
        return PARAMETERS;
    }

    @Override
    public void tick(double time) {
        currentConsumption.set(getConsumption());
    }
}

package nl.utwente.ewi.caes.tactiletriana.simulation.devices;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;

/**
 *
 * @author mickvdv
 */
public class MockDevice extends DeviceBase {
    private static final double MIN_CONSUMPTION = -3700d;
    private static final double MAX_CONSUMPTION = 3700d;
    private final Parameter[] PARAMETERS;
    
    public MockDevice() {
        PARAMETERS = new Parameter[] {
            new Parameter("Consumption", mockDeviceUsage, MIN_CONSUMPTION, MAX_CONSUMPTION)
        };
    }
    
    /**
     * The amount of power the device will consume when turned on
     */
    private final DoubleProperty mockDeviceUsage = new SimpleDoubleProperty(1000d) {
        @Override
        public void set(double value) {
            if (get() == value) return;
            if (value < MIN_CONSUMPTION) value = MIN_CONSUMPTION;
            if (value > MAX_CONSUMPTION) value = MAX_CONSUMPTION;
            
            super.set(value);
        }
    };
    
    public double getMockDeviceUsage() {
        return mockDeviceUsage.get();
    }
    
    public void setMockDeviceUsage(double consumption) {
        this.mockDeviceUsage.set(consumption);
    }
    
    public DoubleProperty mockDeviceUsageProperty() {
        return mockDeviceUsage;
    }
    
    @Override
    public Parameter[] getParameters() {
        return PARAMETERS;
    }

    @Override
    public void tick(double time, boolean connected) {
        super.tick(time, connected);
        
        setCurrentConsumption(getMockDeviceUsage());
    }
}

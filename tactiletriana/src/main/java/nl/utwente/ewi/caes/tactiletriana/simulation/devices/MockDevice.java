package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;

/**
 *
 * @author mickvdv
 */
public class MockDevice extends DeviceBase {
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(500);
    
    @Override
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption;
    }

    @Override
    public void tick(double time) {
        // do nothing
    }
}

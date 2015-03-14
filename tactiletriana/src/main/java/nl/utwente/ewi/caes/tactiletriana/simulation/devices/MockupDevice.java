package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import java.util.Random;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;

/**
 *
 * @author mickvdv
 */
public class MockupDevice extends DeviceBase {
    // TODO: wat moet de beginwaarde zijn.
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(250);
    
    @Override
    public ReadOnlyDoubleProperty currentConsumptionProperty() {
        return currentConsumption;
    }

    @Override
    public void tick(double time) {
        Random r = new Random();
        currentConsumption.set(r.nextDouble()*100 + 200);
    }
    
}

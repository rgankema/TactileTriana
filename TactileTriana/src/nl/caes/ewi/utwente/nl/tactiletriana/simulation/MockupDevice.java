package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;

import java.util.Random;

/**
 *
 * @author mickvdv
 */
public class MockupDevice extends DeviceBase {
    private final ReadOnlyDoubleWrapper currentConsumption = new ReadOnlyDoubleWrapper(Double.MAX_VALUE);
    
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

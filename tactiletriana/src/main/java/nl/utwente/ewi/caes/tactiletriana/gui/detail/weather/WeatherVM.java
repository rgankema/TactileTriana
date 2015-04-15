/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.weather;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class WeatherVM {

    public WeatherVM(Simulation simulation) {
        simulation.currentTimeProperty().addListener(i -> {
            radianceLabel.set(String.format("%3.1f", simulation.getRadiance()));
            temperatureLabel.set(String.format("%3.1f", simulation.getTemperature()));
        });
    }

    private ReadOnlyStringWrapper radianceLabel = new ReadOnlyStringWrapper();

    public ReadOnlyStringProperty radianceLabelProperty() {
        return radianceLabel.getReadOnlyProperty();
    }

    private ReadOnlyStringWrapper temperatureLabel = new ReadOnlyStringWrapper();

    public ReadOnlyStringProperty temperatureLabelProperty() {
        return temperatureLabel.getReadOnlyProperty();
    }
}

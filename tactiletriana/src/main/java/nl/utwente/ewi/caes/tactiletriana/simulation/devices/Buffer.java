/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author mickvdv
 */
public class Buffer extends DeviceBase {

    House deviceHouse;
    int timestep;

    public Buffer(Simulation simulation, String displayName) {
        super(simulation, displayName);
        timestep = SimulationConfig.SIMULATION_TICK_TIME;

        // find the house for this device
        for (House h : this.getSimulation().getHouses()) {
            if (h.getDevices().contains(this)) {
                deviceHouse = h;
                break;
            }
        }
    }

    // Capacity (double)
    // max_power (double)
    // SOC (double)
    private final DoubleProperty capacityProperty = new SimpleDoubleProperty(1000d) {
        @Override
        public void set(double value) {
            if (get() == value) {
                return;
            }
            if (value < 0) {
                value = 0;
            }
            super.set(value);
        }
    };

    public double getCapacity() {
        return capacityProperty.get();
    }

    public void setCapacity(double capacity) {
        this.capacityProperty.set(capacity);
    }

    public DoubleProperty capacityPropety() {
        return capacityProperty;
    }

    private final DoubleProperty maxPowerProperty = new SimpleDoubleProperty(1000d) {
        @Override
        public void set(double value) {
            if (get() == value) {
                return;
            }
            if (value < 0) {
                value = 0;
            }
            super.set(value);
        }
    };

    public double getMaxPower() {
        return maxPowerProperty.get();
    }

    public void setMaxPowerProperty(double power) {
        this.maxPowerProperty.set(power);
    }

    public DoubleProperty maxPowerPorperty() {
        return maxPowerProperty;
    }

    /**
     * SOC is the state of charge,
     */
    private final ReadOnlyDoubleWrapper soc = new ReadOnlyDoubleWrapper(230.0);

    public ReadOnlyDoubleProperty socProperty() {
        return soc.getReadOnlyProperty();
    }

    public final double getSoc() {
        return socProperty().get();
    }

    protected void setSoc(double soc) {
        if (soc > getCapacity()) {
            soc = getCapacity();
        } else if (soc < 0) {
            soc = 0;
        }

        this.soc.set(soc);
    }

    @Override
    public void tick(Simulation simulation, boolean connected) {
        super.tick(simulation, connected);
        // twee cases. Het huis heeft stroom over -> opladen
        double consumption = -deviceHouse.getCurrentConsumption();

        if (deviceHouse.getCurrentConsumption() < 0) {

            if (consumption > this.getMaxPower()) {
                consumption = this.getMaxPower();
            }

            // als hij vol is gebruik niks
            if (isCharged()) {
                consumption = 0;
            } // als er meer gegenereerd wordt dan er er over is. Zorg dat er maximaal gepakt kan worden wat er over is.
            else if ((consumption * timestep) > (this.getCapacity() - this.getSoc())) {
                consumption = (this.getCapacity() - this.getSoc()) / timestep;
            }
            
        }
        // het huis heeft stroom nodig
        else if (deviceHouse.getCurrentConsumption() > 0) {
            if (consumption > -this.getMaxPower()){
                consumption = - this.getMaxPower();
            }
            
            if (this.getSoc() == 0){
                consumption = 0;
            }
            else if ( (this.getSoc() - (consumption * timestep)) < 0){
                consumption = this.getSoc() / timestep;
            }
        }
        
        this.setSoc(this.getSoc() + (consumption * timestep));
        this.setCurrentConsumption(consumption);

    }

    boolean isCharged() {
        return this.getSoc() == this.getCapacity();
    }
}

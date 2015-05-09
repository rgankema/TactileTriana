/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author mickvdv
 */
public class Buffer extends DeviceBase {
    private long prevSeconds = -1;
    
    public Buffer(Simulation simulation) {
        super(simulation, "Buffer");
    }

    /**
     * Capacitiy of Buffer in Wh
     */
    private final DoubleProperty capacity = new SimpleDoubleProperty(1000d) {
        @Override
        public void set(double value) {
            if (value < 0) {
                value = 0;
            }
            super.set(value);
        }
    };
    
    public DoubleProperty capacityProperty() {
        return capacity;
    }

    public double getCapacity() {
        return capacity.get();
    }

    public void setCapacity(double capacity) {
        this.capacity.set(capacity);
    }

    /**
     * The maximum power the Buffer can produce or consume
     */
    private final DoubleProperty maxPower = new SimpleDoubleProperty(1000d) {
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

    public DoubleProperty maxPowerProperty() {
        return maxPower;
    }
    
    public double getMaxPower() {
        return maxPower.get();
    }

    public void setMaxPower(double power) {
        this.maxPower.set(power);
    }

    /**
     * The state of charge in Wh
     */
    private final ReadOnlyDoubleWrapper stateOfCharge = new ReadOnlyDoubleWrapper(230.0) {
        @Override
        public void set(double value) {
            if (value < 0) {
                value = 0;
            }
            super.set(value);
        }
    };

    public ReadOnlyDoubleProperty stateOfChargeProperty() {
        return stateOfCharge.getReadOnlyProperty();
    }

    public final double getStateOfCharge() {
        return stateOfChargeProperty().get();
    }

    protected void setStateOfCharge(double soc) {
        this.stateOfCharge.set(soc);
    }

    @Override
    public void tick(double timePassed, boolean connected) {
        super.tick(timePassed, connected);
        
        // Calculate state of charge change based on previous consumption
        if (prevSeconds != -1) {
            double deltaHours = timePassed / 360d;
            double deltaSOC = getCurrentConsumption() * deltaHours;
            setStateOfCharge(getStateOfCharge() - deltaSOC);
        }
        
        LocalDateTime currentTime = getSimulation().getCurrentTime();
        
        double consumption;
        // If no planning available, help out parent house
        if (simulation.getController() == null || simulation.getController().getPlannedConsumption(this, currentTime) == null) {
            // Likely to change, tick time is probably going to be variable
            int timestep = SimulationConfig.SIMULATION_TICK_TIME;

            consumption = -getParentHouse().getCurrentConsumption();
            // The house is producing energy, so consume
            if (consumption > 0) {
                if (consumption > this.getMaxPower()) {
                    consumption = this.getMaxPower();
                }
                // Don't charge if already at max capacity
                if (getStateOfCharge() == getCapacity()) {
                    consumption = 0;
                }
            }
            // The house is consuming energy, so produce
            else if (consumption < 0) {
                if (consumption < -this.getMaxPower()){
                    consumption = -this.getMaxPower();
                }
                // Don't produce more energy than available
                if ( (getStateOfCharge() - (consumption * timestep)) < 0){
                    consumption = -this.getStateOfCharge() / timestep;
                }
            }
        } else {
            consumption = simulation.getController().getPlannedConsumption(this, currentTime);
        }
        this.setCurrentConsumption(consumption);
    }
}

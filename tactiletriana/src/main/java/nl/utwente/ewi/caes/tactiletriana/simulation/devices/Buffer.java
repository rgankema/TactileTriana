/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import nl.utwente.ewi.caes.tactiletriana.TrianaSettings;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;

/**
 *
 * @author mickvdv
 */
public class Buffer extends BufferBase {

    public Buffer(SimulationBase simulation) {
        super(simulation, "Buffer", "Buffer");

        // initialize values
        this.setMaxPower(1000d);
        this.setCapacity(10000d);
        this.setStateOfCharge(0d);
    }

    // METHODS
    @Override
    protected void doTick(boolean connected) {
        int timestep = TrianaSettings.TICK_MINUTES;

        // Calculate state of charge change based on previous nextConsumption
        double deltaHours = TrianaSettings.TICK_MINUTES / 60d;
        double deltaSOC = getCurrentConsumption() * deltaHours;
        setStateOfCharge(getStateOfCharge() + deltaSOC);

        LocalDateTime currentTime = simulation.getCurrentTime();

        double nextConsumption;

        // If no planning available, help out parent house
        if (getPlanning() == null || getPlanning().get(currentTime) == null) {

            // TODO: dit aan Gerwin vragen, dit is echt rare shit!
            // dit is om het probleem met meerdere buffers eruit te halen.
            // remove the influence of other batterys
            double bufferConsumption = 0;
            for (DeviceBase d : this.getParentHouse().getDevices()) {
                if (d instanceof Buffer) {
                    bufferConsumption += d.getCurrentConsumption();
                }
            }

            // the next consumption is the currentConsupmtion of the house minus the influence of other buffers
            nextConsumption = -(this.getParentHouse().getCurrentConsumption() - bufferConsumption);

            //System.out.println(this.getParentHouse().getCurrentConsumption());
            // The house is producing energy, so start charging
            if (nextConsumption > 0) {
                if (nextConsumption > this.getMaxPower()) {
                    nextConsumption = this.getMaxPower();
                }
                // Don't charge if already at max capacity
                if (getStateOfCharge() + (nextConsumption * timestep) >= getCapacity()) {
                    nextConsumption = (getCapacity() - getStateOfCharge()) / timestep;
                }
            } // The house is consuming energy, so produce
            else if (nextConsumption < 0) {
                // Don't produce more energy than available
                if ((getStateOfCharge() - (-nextConsumption * timestep)) < 0) {
                    nextConsumption = -(this.getStateOfCharge() / timestep);
                }

                // dont produce more than the maximal Power
                if (nextConsumption < -this.getMaxPower()) {
                    nextConsumption = -this.getMaxPower();
                }

                // test:
            }
        } else {
            nextConsumption = getPlanning().get(currentTime);
        }

        if (nextConsumption * timestep > this.getStateOfCharge()) {
            //System.out.println("Error next consumption > this.getStateOfCharge())");
        }
        this.setCurrentConsumption(nextConsumption);
    }

}

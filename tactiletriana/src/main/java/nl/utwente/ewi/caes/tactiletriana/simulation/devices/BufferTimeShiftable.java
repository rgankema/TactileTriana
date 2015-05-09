/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author niels
 */
public class BufferTimeShiftable extends DeviceBase {
    
    /**
     * Constructs a BufferTimeShiftable device (electric vechicle). The model is determined by the model parameter.
     * @param simulation The simulation object of the current simulation.
     * @param model The model of the EV
     */
    public BufferTimeShiftable(Simulation simulation, Model model) {
        super(simulation,"Electric Vehicle");
        setModel(model);
    }
    
    /**
     * Constructs a BufferTimeShiftable of a random model
     * @param simulation 
     */
    public BufferTimeShiftable(Simulation simulation) {
        this(simulation, Model.values()[(int) (Model.values().length * Math.random())]);
    }
    
    // PROPERTIES
    
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
    
    /**
     * The model of the EV
     */
    private final ObjectProperty<Model> model = new SimpleObjectProperty<Model>() {
        @Override 
        public void set(Model value) {
            if (value == null) throw new NullPointerException("Model may not be null");
            
            setStateOfCharge(0);
            switch (value) {
                case TESLA_MODEL_S:
                    setModelName("Tesla Model S");
                    setCapacity(85000);
                    setMaxPower(20000);
                    break;
                case AUDI_A3_E_TRON:
                    setModelName("Audi A3 E-tron");
                    setCapacity(8800);
                    setMaxPower(3700);
                    break;
                case FORD_C_MAX:
                    setModelName("Ford C-Max");
                    setCapacity(7500);
                    setMaxPower(3700);
                    break;
                case VOLKSWAGEN_E_GOLF:
                    setModelName("Volkswagen e-Golf");
                    setCapacity(24000);
                    setMaxPower(3700);
                    break;
                case BMW_I3:
                    setModelName("BMW i3");
                    setCapacity(125000);
                    setMaxPower(7400);
                    break;
            }
        }
    };
    
    public ObjectProperty<Model> modelProperty() {
        return model;
    }
    
    public Model getModel() {
        return model.get();
    }
    
    public void setModel(Model model) {
        this.model.set(model);
    }
    
    /**
     * The name of the EV's model
     */
    private final ReadOnlyStringWrapper modelName = new ReadOnlyStringWrapper();
    
    public ReadOnlyStringProperty modelNameProperty() {
        return modelName.getReadOnlyProperty();
    }
    
    public String getModelName() {
        return modelName.get();
    }
    
    private void setModelName(String modelName) {
        this.modelName.set(modelName);
    }
    
    /**
     * @return whether the battery is charged or not
     */
    public boolean isCharged(){
        return getStateOfCharge() == getCapacity();
    }
    
    // METHODS
    
    @Override
    public void tick(Simulation simulation, boolean connected) {
        super.tick(simulation, connected);

        LocalDateTime time = simulation.getCurrentTime();
        int h = time.getHour();
        
        //Only charge on non-work hours
        //FIXME change implementation for profiles with triana and stuff
        if (((0 <= h && h <= 8) || ( 18 <= h && h <= 23)) && !isCharged()){
            setCurrentConsumption(getMaxPower());
            // FIXME the state of charge should be changed on the next tick, not now already!
            chargeBuffer(getCurrentConsumption(), SimulationConfig.SIMULATION_TICK_TIME);
        //Drain battery during day
        } else if (!(0 <= h && h <= 8) || ( 18 <= h && h <= 23)){
            //FIXME do something else instead of draining with 10KW
            setCurrentConsumption(0);
            chargeBuffer(-10000, SimulationConfig.SIMULATION_TICK_TIME);
        }
    }
    
    //Charge the buffer with an amount of W, can also be negative (draining the battery)
    private void chargeBuffer(double W, double timestep){
        if (!isCharged()){
            setStateOfCharge(getStateOfCharge() + W*(timestep/60));
        }
        //Make sure charge doesn't exceed capacity and doesn't get below zero
        if (getStateOfCharge() > getCapacity()){
            setStateOfCharge(getCapacity());
        } else if (getStateOfCharge() < 0){
            setStateOfCharge(0);
        }
    }
    
    // NESTED ENUMS
    
    /**
     * Describes models of EVs
     */
    public static enum Model {
        TESLA_MODEL_S,
        AUDI_A3_E_TRON,
        FORD_C_MAX,
        VOLKSWAGEN_E_GOLF,
        BMW_I3
    }
}

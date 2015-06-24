/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import static nl.utwente.ewi.caes.tactiletriana.Util.isWeekend;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;

/**
 * Class that simulates an electric vehicle. 
 * @author Richard
 */
public class ElectricVehicle extends BufferTimeShiftableBase {

    //The amount of kilomters that can be driven with 1 kWh, used for calculation of battery drainage
    private double kilometersPerkWh;

    /**
     * Constructs a BufferTimeShiftable device (electric vehicle). The model is
     * determined by the model parameter. On constructing the leaveTime and returnTime
     * variables are set for random work hours.
     *
     * @param simulation The simulation object of the current simulation.
     * @param model The model of the EV
     */
    public ElectricVehicle(SimulationBase simulation, Model model) {
        super(simulation, "Electric Vehicle");
        setModel(model);

        //set the leave time somewhere between 5:30am - 8:30am
        setLeaveTime(Math.random() * 3 + 5.5);
        //set the return time somewhere between 4:00pm and 8:00pm
        setReturnTime(Math.random() * 4 + 16);
        //Initial desired charge is 100%
        setDesiredCharge(getCapacity());

        registerProperty(leaveTime);
        registerProperty(returnTime);
        registerProperty(kilometersToWork);
        registerProperty(this.model);
    }
    
    /**
     * Constructor without the model parameter, this constructor instantiates a random vehicle model.
     * @param simulation The simulation object of the current simulation.
     */
    public ElectricVehicle(Simulation simulation) {
        this(simulation, Model.values()[(int) (Model.values().length * Math.random())]);
    }

    // PROPERTIES
    /**
     * Time when this vechicle typically leaves the grid on workdays
     */
    private final DoubleProperty leaveTime = new SimpleDoubleProperty();

    public DoubleProperty leaveTimeProperty() {
        return leaveTime;
    }

    public double getLeaveTime() {
        return leaveTimeProperty().get();
    }

    public void setLeaveTime(double leave) {
        leaveTimeProperty().set(leave);
    }

    /**
     * Time when this vechicle typically returns to the grid on workdays
     */
    private final DoubleProperty returnTime = new SimpleDoubleProperty();

    public DoubleProperty returnTimeProperty() {
        return returnTime;
    }

    public double getReturnTime() {
        return returnTimeProperty().get();
    }

    public void setReturnTime(double r) {
        returnTimeProperty().set(r);
    }

    /**
     * Amount of kilometers this vechicle has to drive to work. Used for
     * calculation of battery drainage on work days.
     */
    private final DoubleProperty kilometersToWork = new SimpleDoubleProperty();

    public DoubleProperty kilometersToWorkProperty() {
        return kilometersToWork;
    }

    public double getKilometersToWork() {
        return kilometersToWorkProperty().get();
    }

    public void setKilometersToWork(double km) {
        kilometersToWorkProperty().set(km);
    }

    /**
     * The model of the EV
     */
    private final ObjectProperty<Model> model = new SimpleObjectProperty<Model>() {
        @Override
        public void set(Model value) {
            if (value == null) {
                throw new NullPointerException("Model may not be null");
            }
            setStateOfCharge(0);
            switch (value) {
                case TESLA_MODEL_S:
                    setModelName("Tesla Model S");
                    setCapacity(85000);
                    setMaxPower(20000);
                    setKilometersPerkWh(400.0 / 85.0);
                    break;
                case AUDI_A3_E_TRON:
                    setModelName("Audi A3 E-tron");
                    setCapacity(8800);
                    setMaxPower(3700);
                    setKilometersPerkWh(50.0 / 8.8);
                    break;
                case FORD_C_MAX:
                    setModelName("Ford C-Max");
                    setCapacity(7500);
                    setMaxPower(3700);
                    setKilometersPerkWh(44.0 / 7.5);
                    break;
                case VOLKSWAGEN_E_GOLF:
                    setModelName("VW e-Golf");
                    setCapacity(24000);
                    setMaxPower(3700);
                    setKilometersPerkWh(190.0 / 24.0);
                    break;
                case BMW_I3:
                    setModelName("BMW i3");
                    setCapacity(125000);
                    setMaxPower(7400);
                    setKilometersPerkWh(150.0 / 125.0);
                    break;
            }
            setDesiredCharge(getCapacity());
            setKilometersToWork(determineKilometersToWork());
            super.set(value);
        }
    };

    public ObjectProperty<Model> modelProperty() {
        return model;
    }

    public Model getModel() {
        return model.get();
    }

    public final void setModel(Model model) {
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

    // METHODS
    @Override
    public void doTick(boolean connected) {
        LocalDateTime time = simulation.getCurrentTime();

        //Convert the time to a double value for easier comparison
        double h = time.getHour() + (time.getMinute() / 60) * 100;

        // Update state of charge
        chargeBuffer(getCurrentConsumption(), GlobalSettings.TICK_MINUTES);
        if (!isWeekend(time) && (getLeaveTime() < h && h < getReturnTime())) {
            //calculate drainage
            double drainage = (((getKilometersToWork() * 2 / getKilometersPerkWh()) * 1000) / ((getReturnTime() - getLeaveTime())));
            chargeBuffer(-drainage, GlobalSettings.TICK_MINUTES);
        }

        //If there's no planning available, charge full power
        if (getPlanning() == null || getPlanning().get(time) == null) {
            // Decide consumption for upcoming tick, can only charge when at home and not fully charged
            if (!(getLeaveTime() < h && h < getReturnTime()) && !isCharged()) {
                setCurrentConsumption(getMaxPower());
            } else {
                setCurrentConsumption(0);
            }
            //Else charge according to planning
        } else {
            if (!(getLeaveTime() < h && h < getReturnTime()) && !isCharged()) {
                setCurrentConsumption(getPlanning().get(time));
            } else {
                setCurrentConsumption(0);
            }
        }
    }
    
    /**
     * Charge the buffer with an amount of power multiplied by the timestep of the simulation,
     * can also be negative (draining the battery).
     * @param power The amount of power the buffer is going to be charged with.
     * @param timestep The timestep of the simulation.
     */
    private void chargeBuffer(double power, double timestep) {
        if (power == 0) {
            return;
        }

        if (power > 0 && getStateOfCharge() < getCapacity()) {
            setStateOfCharge(getStateOfCharge() + power * (timestep / 60));
        } else if (power < 0) {
            setStateOfCharge(getStateOfCharge() + power * (timestep / 60));
        }
    }

    // NESTED ENUMS
    /**
     * Describes models of EVs
     */
    public enum Model {

        TESLA_MODEL_S,
        AUDI_A3_E_TRON,
        FORD_C_MAX,
        VOLKSWAGEN_E_GOLF,
        BMW_I3
    }

    public final double getKilometersPerkWh() {
        return kilometersPerkWh;
    }

    public void setKilometersPerkWh(double kilometersPerkWh) {
        //System.out.println("kilometersperkwh: "+kilometersPerkWh);
        this.kilometersPerkWh = kilometersPerkWh;
    }

    /**
     * Help function that determines the amount of kilometers this vehicle has to
     * drive on workdays. The function looks at the range the vehicle has and
     * thus never returns a higher value than the range.
     *
     * @return the amount of kilometers this vehicle has to drive on workdays.
     * is a value between the max / 2 and the max.
     */
    private int determineKilometersToWork() {

        //Calculate max amount of kilometers that can be driven with the capacity
        int maxKm = (int) (getKilometersPerkWh() * getCapacity() / 1000);
        //Little margin so battery is never fully drained
        maxKm = maxKm - 5;

        //The amount of kilometers is from maxKm/2 to maxKm divided by 2 bacause of two-way drive
        return (int) (Math.random() * (maxKm / 2) + maxKm / 2) / 2;
    }

}

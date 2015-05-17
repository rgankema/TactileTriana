/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.IController;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;

/**
 *
 * @author Richard
 */
public class ElectricVehicle extends BufferTimeShiftableBase {
    

    //Time when this vechicle typically leaves the grid on workdays
    private double leaveTime;
    //Time when this vechicle typically returns to the grid on workdays
    private double returnTime;
    //The amount of kilomters that can be driven with 1 kWh, used for calculation of battery drainage
    private double kilometersPerkWh;
    //Amount of kilometers this vechicle has to drive to work. Used for calculation of battery drainage on work days.
    private int kilometersToWork;
    
    /**
     * Constructs a BufferTimeShiftable device (electric vehicle). The model is determined by the model parameter.
     * @param simulation The simulation object of the current simulation.
     * @param model The model of the EV
     */
    public ElectricVehicle(SimulationBase simulation, Model model) {
        super(simulation, "Electric Vehicle");
        setModel(model);
        
        //set the leave time somewhere between 5:30am - 8:30am
        setLeaveTime(Math.random()*3 + 5.5); 
        //set the return time somewhere between 4:00 pv and 8:00pm
        setReturnTime(Math.random()*4 + 16);
    }
    
    public ElectricVehicle(Simulation simulation) {
        this(simulation, Model.values()[(int) (Model.values().length * Math.random())]);   
    }
    
    // PROPERTIES
    
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
                    setKilometersPerkWh(400.0/85.0);
                    break;
                case AUDI_A3_E_TRON:
                    setModelName("Audi A3 E-tron");
                    setCapacity(8800);
                    setMaxPower(3700);
                    setKilometersPerkWh(50.0/8.8);
                    break;
                case FORD_C_MAX:
                    setModelName("Ford C-Max");
                    setCapacity(7500);
                    setMaxPower(3700);
                    setKilometersPerkWh(44.0/7.5);
                    break;
                case VOLKSWAGEN_E_GOLF:
                    setModelName("Volkswagen e-Golf");
                    setCapacity(24000);
                    setMaxPower(3700);
                    setKilometersPerkWh(190.0/24.0);
                    break;
                case BMW_I3:
                    setModelName("BMW i3");
                    setCapacity(125000);
                    setMaxPower(7400);
                    setKilometersPerkWh(150.0/125.0);
                    break;
            }
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
    public void tick(boolean connected) {
        super.tick(connected);

        LocalDateTime time = simulation.getCurrentTime();
        
        //Convert the time to a double value for easier comparison
        double h = time.getHour() + (time.getMinute()/60)*100;
        
        // Update state of charge
        chargeBuffer(getCurrentConsumption(), SimulationConfig.TICK_MINUTES);
        if (!isWeekend(time) && (getLeaveTime() < h && h < getReturnTime())) {
            //calculate drainage
            double drainage = (((kilometersToWork*2/getKilometersPerkWh())*1000)/((getReturnTime()-getLeaveTime())));
            chargeBuffer(-drainage, SimulationConfig.TICK_MINUTES);
        }
        
        // Get planning if available
        IController controller = getSimulation().getController();
        Double plannedConsumption = (controller != null) ? controller.getPlannedConsumption(this, simulation.getCurrentTime()) : null;
        
        if (plannedConsumption == null) {
            // Decide consumption for upcoming tick, can only charge when at home and not fully charged
            if (!( getLeaveTime() < h && h < getReturnTime()) && !isCharged()){
                setCurrentConsumption(getMaxPower());                 
            } else {
                setCurrentConsumption(0);
            }
        } else {
            setCurrentConsumption(plannedConsumption);
        }
    }
    
    //Charge the buffer with an amount of power times timestep, can also be negative (draining the battery)
    private void chargeBuffer(double power, double timestep){
        if (power == 0) return;

        setStateOfCharge(getStateOfCharge() + power * (timestep/60));
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
    
    /**
     * Function that determines if the given time is in the weekend
     * @param time The time for which has to be determined if it is in the weekend
     * @return true if the day of the week of time is saturday or sunday, false if it isn't
     */
    public boolean isWeekend(LocalDateTime time){
        
        DayOfWeek dow = time.getDayOfWeek();
        
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }
    
    public double getLeaveTime() {
        return leaveTime;
    }

    public final void setLeaveTime(double leaveTime) {
        this.leaveTime = leaveTime;
    }

    public final double getReturnTime() {
        return returnTime;
    }

    
    public final void setReturnTime(double returnTime) {
        this.returnTime = returnTime;
    }
    
    public final double getKilometersPerkWh() {
        return kilometersPerkWh;
    }
    
    public void setKilometersPerkWh(double kilometersPerkWh) {
        //System.out.println("kilometersperkwh: "+kilometersPerkWh);
        this.kilometersPerkWh = kilometersPerkWh;
    }
    
    public void setKilometersToWork(int kilometersToWork) {
        //System.out.println("kmtowork: "+kilometersToWork);
        this.kilometersToWork = kilometersToWork;
    }
    
    /**
     * Function that determines the amount of kilometers this vechicle has to drive on workdays.
     * The function looks at the range the vechicle has and thus never returns a higher value than the range.
     * @return the amount of kilometers this vechicle has to drive on workdays.
               is a value between the max / 2 and the max. 
     */
    public int determineKilometersToWork(){
        
        //Calculate max amount of kilometers that can be driven with the capacity
        int maxKm = (int) (getKilometersPerkWh()*getCapacity()/1000);
        //Little margin so battery is never fully drained
        maxKm = maxKm - 5;
        
        //The amount of kilometers is from maxKm/2 to maxKm divided by 2 bacause of two-way drive
        return (int)(Math.random()*(maxKm/2)+maxKm/2)/2;
    }

    
}

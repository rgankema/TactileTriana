/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.*;

/**
 *
 * @author niels
 */
public class TimeShiftable extends DeviceBase {
        
    //FIXME implement possibility of multiple start and endtimes
    private int startTime;
    private int endTime;
    private double[] programUsage;
    private int currentMinute;
    
    public TimeShiftable(Simulation simulation) {
        super(simulation, "TimeShiftable");
        
        programUsage = TimeShiftable.dishWasherUsage;
        startTime = 20;
        endTime = 23;
    }
    
    @Override
    public void tick (Simulation simulation, boolean connected){
        super.tick(simulation,connected);
        
        setCurrentConsumption(getCurrentConsumption(simulation.getCurrentTime()));
    }

    public double getCurrentConsumption(LocalDateTime currentTime){
        double result = 0;
        int h = currentTime.getHour();
        if (startTime <= h && h <= endTime){
            result = getCurrentConsumptionInProgram();
        }
        return result;        
    }

    
    //Returns the average consumption that was consumed in the timestep.
    public double getCurrentConsumptionInProgram(){
        double result = 0;
        //Collect #timestep usages, if currentMinute >= programUsage.length, then program is done
        for (int i=currentMinute; i < programUsage.length && i < currentMinute+SimulationConfig.SIMULATION_TICK_TIME; i++){
            result = result + programUsage[i];
        }
        currentMinute = currentMinute+SimulationConfig.SIMULATION_TICK_TIME;
        //Calculate the average of the #timestep usages
        result = result / SimulationConfig.SIMULATION_TICK_TIME;
        System.out.println(result);
        return result;
    }
    
    
    //usage of a dishwasher program in 1m steps in W
    static final double[] dishWasherUsage = {
        66.229735,
        119.35574,
        162.44595,
        154.744551,
        177.089979,
        150.90621,
        170.08704,
        134.23536,
        331.837935,
        2013.922272,
        2032.267584,
        2004.263808,
        2023.32672,
        2041.49376,
        2012.8128,
        2040.140352,
        1998.124032,
        2023.459776,
        1995.309312,
        2028.096576,
        1996.161024,
        552.525687,
        147.718924,
        137.541888,
        155.996288,
        130.246299,
        168.173568,
        106.77933,
        94.445568,
        130.56572,
        121.9515,
        161.905679,
        176.990625,
        146.33332,
        173.06086,
        145.07046,
        188.764668,
        88.4058,
        117.010432,
        173.787341,
        135.315969,
        164.55528,
        150.382568,
        151.517898,
        154.275128,
        142.072704,
        171.58086,
        99.13293,
        94.5507,
        106.020684,
        194.79336,
        239.327564,
        152.75808,
        218.58576,
        207.109793,
        169.5456,
        215.87571,
        186.858018,
        199.81808,
        108.676568,
        99.930348,
        151.759998,
        286.652289,
        292.921008,
        300.5829,
        296.20425,
        195.74251,
        100.34136,
        312.36975,
        287.90921,
        85.442292,
        44.8647,
        2.343792,
        0.705584,
        0.078676,
        0.078744,
        0.078948,
        0.079152,
        0.079016,
        0.078812,
        0.941108,
        10.449,
        4.523148,
        34.157214,
        155.116416,
        158.38641,
        158.790988,
        158.318433,
        158.654276,
        131.583375,
        13.91745,
        4.489968,
        1693.082112,
        3137.819256,
        3107.713851,
        3120.197256,
        3123.464652,
        3114.653256,
        3121.27497,
        3116.305863,
        3106.801566,
        3117.703743,
        3118.851648,
        3110.016195,
        3104.806122,
        1148.154728,
        166.342624,
        161.205252,
        160.049824,
        158.772588,
        158.208076,
        157.926096,
        157.01364,
        112.30272,
        11.65632,
        17.569056,
        4.947208,
        4.724016,
        143.12025,
        161.129536,
        160.671915,
        23.764224,
        136.853808,
        159.11184,
        159.464682,
        159.04302,
        36.68544,
        9.767628,
        4.902772,
        2239.315008,
        3116.846106,
        3111.034014,
        3118.112712,
        3111.809778,
        3113.442189,
        3110.529708,
        3104.676432,
        3101.093424,
        3121.076178,
        1221.232208,
        159.964185,
        2663.07828,
        272.524675,
        7.76832,
        3.258112,
        3.299408,
        3.295136,
        3.256704,
        3.258112,
        3.262336,
        2224.648744,
        367.142872,
        4.711025
    };
    
    static final double[] washingMachineUsage = {
        66.229735,
        119.35574,
        162.44595,
        154.744551,
        177.089979,
        150.90621,
        170.08704,
        134.23536,
        331.837935,
        2013.922272,
        2032.267584,
        2004.263808,
        2023.32672,
        2041.49376,
        2012.8128,
        2040.140352,
        1998.124032,
        2023.459776,
        1995.309312,
        2028.096576,
        1996.161024,
        552.525687,
        147.718924,
        137.541888,
        155.996288,
        130.246299,
        168.173568,
        106.77933,
        94.445568,
        130.56572,
        121.9515,
        161.905679,
        176.990625,
        146.33332,
        173.06086,
        145.07046,
        188.764668,
        88.4058,
        117.010432,
        173.787341,
        135.315969,
        164.55528,
        150.382568,
        151.517898,
        154.275128,
        142.072704,
        171.58086,
        99.13293,
        94.5507,
        106.020684,
        194.79336,
        239.327564,
        152.75808,
        218.58576,
        207.109793,
        169.5456,
        215.87571,
        186.858018,
        199.81808,
        108.676568,
        99.930348,
        151.759998,
        286.652289,
        292.921008,
        300.5829,
        296.20425,
        195.74251,
        100.34136,
        312.36975,
        287.90921,
        85.442292,
        44.8647
    };
    
}

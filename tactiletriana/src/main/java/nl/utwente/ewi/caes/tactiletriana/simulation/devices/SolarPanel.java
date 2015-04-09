/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.devices;

import java.time.LocalDateTime;
import nl.utwente.ewi.caes.tactiletriana.simulation.*;

/**
 *
 * @author niels
 */
public class SolarPanel extends DeviceBase {
    //FIXME: Add these constants to the constructor
    //Area of the panel in m2
    private double area = 2;
    //Elevation of the panel in degrees
    private int elevation = 45;
    //Azimuth orientation of the panel in degrees, 0 = south, 90 = west, 180 = north & 270 = east
    private int azimuth = 180;
    //Effiency of the solar panel in percentage
    private int efficiency = 50;
    //Efficiency degradation due to temperature increase of the solar panel. Percentage degradation for maximum power per degree celcius [percent]
    private int temperatureEfficiency = 80;
    
    public SolarPanel(){
        
    }   
    
    
    @Override
    public void tick(Simulation simulation, boolean connected) {
        super.tick(simulation, connected);
        
        //Set the consumption according to temperature and radiation
        setCurrentConsumption(  calculateProduction(simulation.getTemperature(),simulation.getRadiance(),
                                Simulation.LONGITUDE, Simulation.LATITUDE, simulation.getCurrentTime()));
    }
    
    
    //Returns 
    public double calculateProduction(double temperature, double radiance, double longitude, double latitude, LocalDateTime time){
       
        double PI = 3.14159265359;
        
        double longitudeRadian = longitude*(PI/180);
        double latitudeRadian = latitude*(PI/180);
        double elevationRadian = elevation*(PI/180);	//Angle of the panel
	double azimuthRadian = azimuth*(PI/180);
        
        double rho_gnd = 0.2; //constant
        
         
        //TIme calculations
	int day = time.getDayOfYear();
	double delta = (23.44*Math.sin(2*PI*(day+284)/365.24))*(PI/180);        
	double N = 2*PI*(day/366); //one year
	double E_time = 229.2*(0.0000075+0.001868*Math.cos(N)-0.032077*Math.sin(N)-0.014614*Math.cos(2*N)-0.04089*Math.sin(N)); 
                
        //Calcuate h: Heigth of the sun
	double local_std_time = time.getHour()*60+time.getMinute();
	double solar_time = (-4.0*(longitudeRadian/(PI/180)))+E_time+local_std_time;        
	double omega = ((0.25*solar_time)-180)*(PI/180);
	double h = Math.asin(Math.cos(latitudeRadian)*Math.cos(delta)*Math.cos(omega)+Math.sin(latitudeRadian)*Math.sin(delta));
        
        //Calculate diffuse light
	double I_0 = (1367*3600/10000) * Math.sin(h);
	double k_T = 0.0;
	if(I_0 >= 0.001){
		k_T = radiance / I_0;
	}

	double I_d = radiance*0.165;
        
	if(k_T <= 0.0){
		I_d = 0.0;
	}
	else if(k_T <= 0.22){
		I_d = radiance*(1.0-0.09*k_T);
	}
	else if(k_T <= 0.8){
		I_d = radiance*(0.9511-0.1604*k_T+4.388*Math.pow(k_T,2)-16.638*Math.pow(k_T,3)+12.336*(Math.pow(k_T, 4)));
	}

	//Calcualte the direct beam
	double I_b = radiance-I_d;
	double I = Math.min(I_0,I_b / Math.sin(h));
	if(h < 0.001){
            I = 0.0;
        }
        
        	//Obtain the ammount of energy
	double G_ds = I_d*(1+Math.cos(elevationRadian))/2;
	double G_gnds = radiance*rho_gnd*(1-Math.cos(elevationRadian))/2;
	double theta = Math.acos(Math.sin(delta)*Math.sin(latitudeRadian)*Math.cos(elevationRadian)-
				   Math.sin(delta)*Math.cos(latitudeRadian)*Math.cos(azimuthRadian)*Math.sin(elevationRadian)+
				   Math.cos(delta)*Math.cos(latitudeRadian)*Math.cos(omega)*Math.cos(elevationRadian)+
				   Math.cos(delta)*Math.sin(latitudeRadian)*Math.cos(azimuthRadian)*Math.cos(omega)*Math.sin(elevationRadian)+
				   Math.cos(delta)*Math.sin(azimuthRadian)*Math.sin(omega)*Math.sin(elevationRadian));
	double G_bs = Math.max(0.0, I*Math.cos(theta));
	double G_ts = G_ds + G_bs + G_gnds; //This is the joules in one hour for a square cm

	double powerSquareMeter = (G_ts * 10000)/3600;

	//Guess for the PV temperature that affects the efficiency.
	double temperaturePV = temperature + (50*powerSquareMeter/1367); //Formula not based on anything or whatsover, this part can be improved
	double actualEfficiency = (efficiency * (1 - ((temperaturePV - 25) * temperatureEfficiency)/100)) / 100;

	//Return the W/m2 (coming from J/cm2 for a whole hour)
	return powerSquareMeter * actualEfficiency;
    }
    
    public static void main(String[] args){
        SolarPanel p = new SolarPanel();
        
        System.out.println(p.calculateProduction(30, 30, 50, 50, LocalDateTime.now()));
        
    }
    
}

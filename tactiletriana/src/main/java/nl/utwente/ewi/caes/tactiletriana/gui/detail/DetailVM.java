/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail;

import java.util.function.Consumer;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.chart.ChartVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime.DateTimeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.weather.WeatherVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario.TimeSpan;

/**
 *
 * @author Richard
 */
public class DetailVM {

    private final Simulation simulation;
    private final DateTimeVM dateTimeVM;
    private final WeatherVM weatherVM;
    private final ChartVM chartVM;
    
    public DetailVM(Simulation simulation) {
        this.simulation = simulation;

        dateTimeVM = new DateTimeVM(simulation);
        chartVM = new ChartVM();
        weatherVM = new WeatherVM(simulation);
    }
    
    /**
     * To be called by the view when it gets coupled to this VM. Describes a function
     * that should be called when the Simulation jumps to a new TimeSpan.
     * 
     * @param callback the function that should be called
     */
    public void setOnSimulationTimeSpanChange(Consumer<TimeSpan> callback) {
        this.simulation.getTimeScenario().addNewTimeSpanStartedCallback(callback);
        this.simulation.timeScenarioProperty().addListener((obs, oV, nV) -> { 
            oV.removeNewTimeSpanStartedCallback(callback);
            nV.addNewTimeSpanStartedCallback(callback);
        });
    }

    // Child VMs
    
    public DateTimeVM getDateTimeVM() {
        return dateTimeVM;
    }

    public ChartVM getChartVM() {
        return chartVM;
    }

    public WeatherVM getWeatherVM() {
        return weatherVM;
    }
}

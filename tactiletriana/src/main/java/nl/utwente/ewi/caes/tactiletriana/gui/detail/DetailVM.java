/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail;

import nl.utwente.ewi.caes.tactiletriana.gui.detail.chart.ChartVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime.DateTimeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.weather.WeatherVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class DetailVM {

    private final Simulation simulation;
    private final DateTimeVM dateTimeVM;
    private final WeatherVM weatherVM;

    private ChartVM chartVM;

    public DetailVM(Simulation simulation) {
        this.simulation = simulation;

        dateTimeVM = new DateTimeVM(simulation);
        chartVM = new ChartVM();
        weatherVM = new WeatherVM(simulation);
    }

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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.background;

import java.time.LocalDateTime;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.Util;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.WeatherData;

/**
 *
 * @author Richard
 */
public class BackgroundVM {
    
    public BackgroundVM(Simulation model) {
        model.currentTimeProperty().addListener(obs -> {
            updateDarknessFactor(model.getCurrentTime());
            updateSeason(model.getCurrentTime());
        });
    }
    
    // PROPERTIES
    /**
     * The darkness on a scale of 0 to 1. 0 means day, 1 means night, and
     * anything in between is twilight.
     */
    private final DoubleProperty darknessFactor = new SimpleDoubleProperty();

    public ReadOnlyDoubleProperty darknessFactorProperty() {
        return darknessFactor;
    }

    public final double getDarknessFactor() {
        return darknessFactorProperty().get();
    }

    private void setDarknessFactor(double darkness) {
        darknessFactor.set(darkness);
    }

    /**
     * The season to be shown on the touch screen.
     */
    private final ObjectProperty<Season> season = new SimpleObjectProperty<>();

    public ReadOnlyObjectProperty<Season> seasonProperty() {
        return season;
    }

    public final Season getSeason() {
        return seasonProperty().get();
    }

    private void setSeason(Season season) {
        this.season.set(season);
    }
    
    // HELP METHODS
    private void updateDarknessFactor(LocalDateTime time) {
        final double PI = Math.PI;

        final double PI_DIV_180 = PI / 180;
        double longitude = WeatherData.getInstance().getLongitude();
        double latitude = WeatherData.getInstance().getLatitude();
        double radiance = WeatherData.getInstance().getRadianceProfile()[Util.toTimeStep(time)];

        // Time calculations
        int day = time.getDayOfYear();
        double delta = (23.44 * Math.sin(2 * PI * (day + 284) / 365.24)) * PI_DIV_180;

        double N = 2 * PI * (day / 366);
        double E_time = 229.2 * (0.0000075 + 0.001868 * Math.cos(N) - 0.032077 * Math.sin(N) - 0.014614 * Math.cos(2 * N) - 0.04089 * Math.sin(N));

        // Calculate h: height of sun
        double local_std_time = time.getHour() * 60 + time.getMinute();
        double solar_time = (-4.0 * longitude) + E_time + local_std_time;
        double omega = ((0.25 * solar_time) - 180) * PI_DIV_180;
        double h = Math.asin(Math.cos(latitude * PI_DIV_180) * Math.cos(delta) * Math.cos(omega) + Math.sin(latitude * PI_DIV_180) * Math.sin(delta));

        // Twilight starts when the center of the sun is 12 degrees below the horizon
        double darkness = -Math.toDegrees(h) / 12;

        if (darkness > 1) {
            darkness = 1;
        }
        if (darkness < 0) {
            darkness = 0;
        }

        setDarknessFactor(darkness);
    }

    private void updateSeason(LocalDateTime time) {
        // Using the meteorological definition of seasons
        int month = time.getMonthValue() % 12;
        Season result = null;
        if (month < 3) {
            result = Season.WINTER;
        } else if (month < 6) {
            result = Season.SPRING;
        } else if (month < 9) {
            result = Season.SUMMER;
        } else if (month < 12) {
            result = Season.AUTUMN;
        }
        setSeason(result);
    }

    // NESTED ENUMS
    /**
     * Represents the seasons in a year
     */
    public enum Season {
        SPRING,
        SUMMER,
        AUTUMN,
        WINTER
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime;

import java.time.LocalDateTime;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class DateTimeVM {

    public DateTimeVM(Simulation simulation) {
        timeLabel.bind(Bindings.createStringBinding(() -> {
            LocalDateTime time = simulation.getCurrentTime();
            return String.format("%02d:%02d", time.getHour(), time.getMinute());
        }, simulation.currentTimeProperty()));

        dateLabel.bind(Bindings.createStringBinding(() -> {
            LocalDateTime time = simulation.getCurrentTime();
            return String.format("%d-%d-%d", time.getDayOfMonth(), time.getMonthValue(), time.getYear());
        }, simulation.currentTimeProperty()));
    }

    private final StringProperty timeLabel = new SimpleStringProperty();

    public StringProperty timeLabelProperty() {
        return timeLabel;
    }

    private final StringProperty dateLabel = new SimpleStringProperty();

    public StringProperty dateLabelProperty() {
        return dateLabel;
    }
}

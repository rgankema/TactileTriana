/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario.timespan;

import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario.TimeSpan;

/**
 *
 * @author Richard
 */
public class TimeSpanVM {
    
    /**
     * Creates a new instance of TimeSpanVM with initial start and end dates
     * 
     * @param start the initial start date
     * @param end   the initial end date
     */
    public TimeSpanVM(LocalDate start, LocalDate end) {
        // Need to set min/max before actual start/end dates to avoid NullPointerException
        minStartDate.set(start);
        maxStartDate.set(end);
        minEndDate.set(start);
        maxEndDate.set(end);
        
        setStartDate(start);
        setEndDate(end);
        
        maxStartDate.bind(endDate);
        minEndDate.bind(startDate);
    }
    
    // BINDABLE PROPERTIES
    
    /**
     * The start day of the time span, inclusive
     */
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<LocalDate>() {
        @Override
        public void set(LocalDate value) {
            if (value.isBefore(minStartDateProperty().get())) {
                throw new IllegalArgumentException("Start date may not be before minimum");
            }
            if (value.isAfter(maxStartDateProperty().get())) {
                throw new IllegalArgumentException("Start date may not be after maximum");
            }
            super.set(value);
        }
    };
    
    public ObjectProperty<LocalDate> startDateProperty() {
        return startDate;
    }
    
    public final LocalDate getStartDate() {
        return startDateProperty().get();
    }
    
    public final void setStartDate(LocalDate startDate) {
        startDateProperty().set(startDate);
    }
    
    /**
     * The end day of the time span, inclusive
     */
    private final ObjectProperty<LocalDate> endDate = new SimpleObjectProperty<LocalDate>() {
        @Override
        public void set(LocalDate value) {
            if (value.isBefore(minEndDateProperty().get())) {
                throw new IllegalArgumentException("End date may not be before minimum");
            }
            if (value.isAfter(maxEndDateProperty().get())) {
                throw new IllegalArgumentException("End date may not be after maximum");
            }
            super.set(value);
        }
    };
    
    public ObjectProperty<LocalDate> endDateProperty() {
        return endDate;
    }
    
    public final LocalDate getEndDate() {
        return endDateProperty().get();
    }
    
    public final void setEndDate(LocalDate endDate) {
        endDateProperty().set(endDate);
    }
    
    
    /**
     * The minimum date that may be chosen for the start date
     */
    private final ObjectProperty<LocalDate> minStartDate = new SimpleObjectProperty<>();
    
    public ObjectProperty<LocalDate> minStartDateProperty() {
        return minStartDate;
    }
    
    /**
     * The maximum date that may be chosen for the start date
     */
    private final ObjectProperty<LocalDate> maxStartDate = new SimpleObjectProperty<>();
    
    public ObjectProperty<LocalDate> maxStartDateProperty() {
        return maxStartDate;
    }
    
    /**
     * The minimum date that may be chosen for the end date
     */
    private final ObjectProperty<LocalDate> minEndDate = new SimpleObjectProperty<>();
    
    public ObjectProperty<LocalDate> minEndDateProperty() {
        return minEndDate;
    }
    
    /**
     * The maximum date that may be chosen for the end date
     */
    private final ObjectProperty<LocalDate> maxEndDate = new SimpleObjectProperty<>();
    
    public ObjectProperty<LocalDate> maxEndDateProperty() {
        return maxEndDate;
    }
    
    // METHODS
    
    /**
     * Creates a new TimeSpan based on this VM
     * @return a new TimeSpan
     */
    public TimeSpan build() {
        return new TimeSpan(getStartDate(), getEndDate());
    }
}

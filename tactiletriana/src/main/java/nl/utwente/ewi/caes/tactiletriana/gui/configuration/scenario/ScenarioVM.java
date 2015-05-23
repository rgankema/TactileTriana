/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario;

import java.time.LocalDate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario.timespan.TimeSpanVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario;
import nl.utwente.ewi.caes.tactiletriana.simulation.TimeScenario.TimeSpan;

/**
 *
 * @author Richard
 */
public class ScenarioVM {
    // Not meant to be configurable, just for reference
    private static final LocalDate MIN_DATE = LocalDate.of(2014, 1, 1);
    private static final LocalDate MAX_DATE = LocalDate.of(2014, 12, 31);
   
    private final ObservableList<TimeSpanVM> timeSpans;
    
    public ScenarioVM() {
        this.timeSpans = FXCollections.observableArrayList();
        
        try{
            TimeScenario ts = TimeScenario.parseTimeScenario(SimulationConfig.LoadProperty("timescenario"));
            for (TimeSpan span : ts.getTimeSpans()){
                timeSpans.add(new TimeSpanVM(span.getStart().toLocalDate(), span.getEnd().toLocalDate()));
            }
        }
        catch (Exception e){
            System.err.print(e.toString());
            // er is nog niks gedeclared
            timeSpans.add(new TimeSpanVM(MIN_DATE, MAX_DATE));
        }
        
    }
    
    public ObservableList<TimeSpanVM> getTimeSpans() {
        return timeSpans;
    }
    
    // PROPERTIES
    
    private final BooleanProperty removeButtonDisable = new SimpleBooleanProperty(true);
    
    public BooleanProperty removeButtonDisableProperty() {
        return removeButtonDisable;
    }
    
    // METHODS
    
    public TimeScenario build() {
        TimeScenario scenario = new TimeScenario();
        for (TimeSpanVM tsvm : timeSpans) {
            scenario.add(tsvm.build());
        }
        return scenario;
    }
    
    // EVENT HANDLING
    
    public void addTimeSpan() {
        TimeSpanVM last = timeSpans.get(timeSpans.size() - 1);
        // Don't add TimeSpan if there is no room for one
        if (!last.getEndDate().isBefore(MAX_DATE.minusDays(1))) {
            return;
        }
        
        TimeSpanVM timeSpanVM = new TimeSpanVM(last.getEndDate().plusDays(1), MAX_DATE);
        // New time span must start at least one day after last time span
        timeSpanVM.minStartDateProperty().bind(Bindings.createObjectBinding(() -> { 
            return last.getEndDate().plusDays(1);
        }, last.endDateProperty()));
        
        // Last time span may not exceed start of new time span
        last.maxEndDateProperty().bind(Bindings.createObjectBinding(() -> { 
            return timeSpanVM.getEndDate().minusDays(1);
        }, timeSpanVM.startDateProperty()));
        
        timeSpans.add(timeSpanVM);
        
        removeButtonDisable.set(timeSpans.size() < 2);
    }
    
    public void removeTimeSpan(TimeSpanVM timeSpanVM) {
        int index = timeSpans.indexOf(timeSpanVM);
        if (index >= 0) {
            TimeSpanVM prev = null;
            TimeSpanVM next = null;
            
            if (index > 0) {
                prev = timeSpans.get(index - 1);
                prev.maxEndDateProperty().unbind();
                prev.maxEndDateProperty().set(MAX_DATE);
                timeSpanVM.minStartDateProperty().unbind();
            }
            
            timeSpans.remove(timeSpanVM);
            
            if (index < timeSpans.size()) {
                next = timeSpans.get(index);
                next.minStartDateProperty().unbind();
                next.minStartDateProperty().set(MIN_DATE);
                timeSpanVM.maxEndDateProperty().unbind();
            }
            
            if (prev != null && next != null) {
                final TimeSpanVM fPrev = prev;
                final TimeSpanVM fNext = next;
                
                prev.maxEndDateProperty().bind(Bindings.createObjectBinding(() -> { 
                    return fNext.getStartDate().minusDays(1);
                }, next.startDateProperty()));
                
                next.minStartDateProperty().bind(Bindings.createObjectBinding(() -> { 
                    return fPrev.getEndDate().plusDays(1);
                }, prev.endDateProperty()));
            }
        }
        
        removeButtonDisable.set(timeSpans.size() < 2);
    }
}

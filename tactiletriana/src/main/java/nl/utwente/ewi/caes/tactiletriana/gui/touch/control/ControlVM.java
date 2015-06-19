/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.control;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation.SimulationState;

/**
 *
 * @author Richard
 */
public class ControlVM {

    private final Simulation simulation;

    public ControlVM(Simulation simulation) {
        this.simulation = simulation;

        // Bind pause button text to Simulation state
        simulation.stateProperty().addListener((obs) -> {
            if (simulation.getState() == SimulationState.RUNNING) {
                setPaused(false);
            } else {
                setPaused(true);
            }
        });
        
        isTrianaEnabled.bind(simulation.trianaEnabledProperty());
    }
    
    // PROPERTIES

    /**
     * Whether the simulation is paused
     */
    private final ReadOnlyBooleanWrapper paused = new ReadOnlyBooleanWrapper(false);

    public ReadOnlyBooleanProperty pausedProperty() {
        return paused.getReadOnlyProperty();
    }

    public final boolean isPaused() {
        return pausedProperty().get();
    }
    
    protected final void setPaused(boolean b) {
        paused.set(b);
    }
    
    /**
     * Whether Triana is enabled/disabled
     */
    private final ReadOnlyBooleanWrapper isTrianaEnabled = new ReadOnlyBooleanWrapper(true);
    
    public ReadOnlyBooleanProperty trianaEnabledProperty() {
        return simulation.trianaEnabledProperty();
    }
    
    public final boolean isTrianaEnabled() {
        return trianaEnabledProperty().get();
    }
    
    // EVENT HANDLING

    /**
     * Resumes the simulation when paused and pauses it when running
     */
    public void toggleSimulationState() {
        if (simulation.getState() == SimulationState.RUNNING) {
            simulation.pause();
        } else if (simulation.getState() == SimulationState.PAUSED) {
            simulation.start();
        }
    }
    
    /**
     * Toggles Triana from on to off and vice versa
     */
    public void toggleTriana() {
        simulation.setTrianaEnabled(!simulation.isTrianaEnabled());
    }
    
    
    
}

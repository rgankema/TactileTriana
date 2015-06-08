/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.control;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
                setPauseButtonText("Pause");
            } else {
                setPauseButtonText("Resume");
            }
        });
    }
    
    /**
     * The text for the pause button
     */
    private final StringProperty pauseButtonText = new SimpleStringProperty("Pause");
    
    public ReadOnlyStringProperty pauseButtonTextProperty() {
        return pauseButtonText;
    }
    
    protected final void setPauseButtonText(String text) {
        pauseButtonText.set(text);
    }
    
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
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.configuration;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Screen;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class ConfigurationVM {
    private static SimpleBooleanProperty launched = new SimpleBooleanProperty(false);
    
    // TODO: hele screen ding zou naar StageController mogen
    private ObservableList<Integer> screenIndexList;
    private List<Screen> screens;
    
    public ConfigurationVM() {
        screens = Screen.getScreens();
        screenIndexList = FXCollections.observableList(new ArrayList<Integer>());
        //screenIndexList.add(null);
        
        for (int i = 0; i < screens.size(); i++) {
            screenIndexList.add(i + 1);
        }
        
        // full screen checkbox is enabled when application hasn't launched yet
        fullScreenCheckBoxDisabled.bind(launched);
        
        // combo boxes are enabled when full screen is checked, and the application hasn't launched yet
        screenComboBoxesDisabled.bind(fullScreenChecked.not().or(launched));
        
        // start button is enabled when a valid port is chosen and combo boxes are not
        // at default position or fullscreen is not checked
        startButtonDisabled.bind(Bindings.createBooleanBinding(() -> {
            try {
                int port = Integer.parseInt(portFieldText.get());
                if (port < 1024 || port > 65535) return true;
            } catch (NumberFormatException e) {
                return true;
            }
            
            if (!fullScreenChecked.get()) {
                return false;
            } else {
                return (touchScreenSelection.get() == null || detailScreenSelection.get() == null);
            }
        }, fullScreenChecked, touchScreenSelection, detailScreenSelection, portFieldText));
        
        // start button text is set to "Start" when the simulation hasn't started yet,
        // and "Resume" when it already has
        startButtonText.bind(Bindings.createStringBinding(() -> {
            if (Simulation.getInstance().isStarted())
                return "Resume Simulation";
            else
                return "Start Simulation";
        }, Simulation.getInstance().startedProperty()));
        
        // reset button is enabled when simulation has been started
        resetButtonDisabled.bind(Simulation.getInstance().startedProperty().not());
    }
    
    // BINDABLE PROPERTIES
    
    /**
     * The text entered in the port field
     */
    private final StringProperty portFieldText = new SimpleStringProperty("4321");
    
    public StringProperty portFieldTextProperty() {
        return this.portFieldText;
    }
    
    /**
     * Whether the full screen check box is checked
     */
    private final BooleanProperty fullScreenChecked = new SimpleBooleanProperty(false);
    
    public BooleanProperty fullScreenCheckedProperty() {
        return this.fullScreenChecked;
    }
    
    /**
     * Whether the full screen check box is enabled
     */
    private final BooleanProperty fullScreenCheckBoxDisabled = new SimpleBooleanProperty(false);
    
    public BooleanProperty fullScreenCheckBoxDisabledProperty() {
        return fullScreenCheckBoxDisabled;
    }
    
    /**
     * Whether the combo boxes are enabled
     */
    private final BooleanProperty screenComboBoxesDisabled = new SimpleBooleanProperty(true);
    
    public BooleanProperty screenComboBoxesDisabledProperty() {
        return this.screenComboBoxesDisabled;
    }
    
    /**
     * Whether the start button is disabled
     */
    private final BooleanProperty startButtonDisabled = new SimpleBooleanProperty(false);
    
    public BooleanProperty startButtonDisabledProperty() {
        return this.startButtonDisabled;
    }
    
    /**
     * Whether the reset button is disabled
     */
    private final BooleanProperty resetButtonDisabled = new SimpleBooleanProperty(false);
    
    public BooleanProperty resetButtonDisabledProperty() {
        return this.resetButtonDisabled;
    }
    
    /**
     * The text to be shown on the start button
     */
    private final StringProperty startButtonText = new SimpleStringProperty("Start Simulation");
    
    public StringProperty startButtonTextProperty() {
        return this.startButtonText;
    }
    
    /**
     * The screen number that is chosen for the touch screen, null if none is chosen yet
     */
    private final ObjectProperty touchScreenSelection = new SimpleObjectProperty(null);
    
    public ObjectProperty touchScreenSelectionProperty() {
        return this.touchScreenSelection;
    }
    
    /**
     * The screen number that is chosen for the detail screen, null if none is chosen yet
     */
    private final ObjectProperty detailScreenSelection = new SimpleObjectProperty(null);
    
    public ObjectProperty detailScreenSelectionProperty() {
        return this.detailScreenSelection;
    }
    
    /**
     * 
     * @return an ObservableList of screen indexes
     */
    public ObservableList<Integer> getScreenIndexList() {
        return this.screenIndexList;
    }
    
    // METHODS
    
    /**
     * 
     * @param index the screen index
     * @return the Screen associated with the index as shown in the ComboBoxes
     */
    public Screen getScreenByIndex(Integer index) {
        if (index == null || index <= 0) 
            return Screen.getPrimary();
        else
            return this.screens.get(index - 1);
    }
    
    public void start() {
        StageController.getInstance().setMainStagesVisible(true);
        StageController.getInstance().setLauncherStageVisible(false);
        StageController.getInstance().setScreenIndexStagesVisible(false);
        Simulation.getInstance().start();
        
        launched.set(true);
    }
    
    public void reset() {
        Simulation.getInstance().reset();
    }
}

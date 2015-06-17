/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.configuration;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.utwente.ewi.caes.tactiletriana.Concurrent;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import nl.utwente.ewi.caes.tactiletriana.api.APIServer;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class ConfigurationVM {

    private static final SimpleBooleanProperty launched = new SimpleBooleanProperty(false);
    private final ObservableList screenIndexList = FXCollections.observableArrayList();
    
    private Simulation simulation;
    private APIServer server;
    
    public ConfigurationVM(Simulation simulation) {
        this.simulation = simulation;

        // full screen checkbox is enabled when application hasn't launched yet
        fullScreenCheckBoxDisabled.bind(launched);

        // combo boxes are enabled when full screen is checked, and the application hasn't launched yet
        screenComboBoxesDisabled.bind(fullScreenChecked.not().or(launched));

        // start button is enabled when a valid port is chosen and combo boxes are not
        // at default position or fullscreen is not checked
        startButtonDisabled.bind(Bindings.createBooleanBinding(() -> {
            try {
                int port = Integer.parseInt(portFieldText.get());
                if (port < 1024 || port > 65535) {
                    return true;
                }
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
            if (simulation.getState() == Simulation.SimulationState.PAUSED) {
                return "Resume Simulation";
            } else {
                return "Start Simulation";
            }
        }, this.simulation.stateProperty()));

        // reset button is enabled when simulation has been started
        resetButtonDisabled.bind(Bindings.createBooleanBinding(() -> {
            return simulation.getState() == Simulation.SimulationState.STOPPED;
        }, this.simulation.stateProperty()));

        // scenario is enabled when simulation is stopped
        scenarioViewDisable.bind(Bindings.createBooleanBinding(() -> {
            return simulation.getState() != Simulation.SimulationState.STOPPED;
        }, this.simulation.stateProperty()));
        
        // update connection info text if a controller has been added to the simulation
        this.simulation.controllerProperty().addListener(obs -> { 
            if (simulation.getController() != null) {
                connectionInfoText.set("Controller connected");
            } else {
                if (server == null) {
                    connectionInfoText.set("Connection closed");
                } else {
                    connectionInfoText.set("Waiting for controller to connect...");
                }
            }
        });
        
        // update settings when new ones have been loaded
        GlobalSettings.addSettingsChangedHandler(() -> updateSettingsText());
        updateSettingsText();
    }

    // BINDABLE PROPERTIES
    /**
     * The text to be shown in the settings text area
     */
    private final ReadOnlyStringWrapper settingsText = new ReadOnlyStringWrapper();
    
    public ReadOnlyStringProperty settingsTextProperty() {
        return settingsText;
    }
    
    /**
     * Whether the Scenario view is enabled
     */
    private final BooleanProperty scenarioViewDisable = new SimpleBooleanProperty(false);

    public ReadOnlyBooleanProperty scenarioViewDisableProperty() {
        return scenarioViewDisable;
    }

    public final boolean isScenarioViewDisable() {
        return scenarioViewDisableProperty().get();
    }

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
     * The screen number that is chosen for the touch screen, null if none is
     * chosen yet
     */
    private final ObjectProperty touchScreenSelection = new SimpleObjectProperty(null);

    public ObjectProperty touchScreenSelectionProperty() {
        return this.touchScreenSelection;
    }

    /**
     * The screen number that is chosen for the detail screen, null if none is
     * chosen yet
     */
    private final ObjectProperty detailScreenSelection = new SimpleObjectProperty(null);

    public ObjectProperty detailScreenSelectionProperty() {
        return this.detailScreenSelection;
    }
    
    /**
     * The text for the server start/stop button
     */
    private final ReadOnlyStringWrapper toggleServerButtonText = new ReadOnlyStringWrapper("Start Server");

    public ReadOnlyStringProperty toggleServerButtonTextProperty() {
        return toggleServerButtonText;
    }
    
    /**
     * The text for the connection info label
     */
    private final ReadOnlyStringWrapper connectionInfoText = new ReadOnlyStringWrapper("Connection closed");
    
    public ReadOnlyStringProperty connectionInfoTextProperty() {
        return connectionInfoText;
    }
    
    /**
     *
     * @return an ObservableList of screen indexes
     */
    public ObservableList<Integer> getScreenIndexList() {
        return screenIndexList;
    }

    // METHODS
    public void start() {

        StageController.getInstance().setMainStagesVisible(true);
        StageController.getInstance().setLauncherStageVisible(false);
        StageController.getInstance().setScreenIndexStagesVisible(false);

        if (this.simulation.getState() == Simulation.SimulationState.STOPPED) {
            this.simulation.setTimeScenario(GlobalSettings.TIME_SCENARIO);
        }

        /* Forget saving for now
        if (launched.get()) {
            TrianaSettings.FULLSCREEN = fullScreenCheckedProperty().get();
            if (fullScreenCheckedProperty().get()) {
                TrianaSettings.TOUCH_SCREEN_ID = (int) touchScreenSelectionProperty().get();
                TrianaSettings.DETAIL_SCREEN_ID = (int) detailScreenSelectionProperty().get();
            }
            TrianaSettings.save(TrianaSettings.DEFAULT_FILE);
        }*/
        
        this.simulation.start();

        launched.set(true);
    }

    public void reset() {
        StageController.getInstance().resetSimulation();
    }
    
    public void toggleServer() {
        if (server == null) {
            server = new APIServer(Integer.parseInt(portFieldTextProperty().get()), simulation);
            Concurrent.getExecutorService().submit(server);
            toggleServerButtonText.set("Stop Server");
            connectionInfoText.set("Waiting for controller to connect...");
        } else {
            server.stop();
            toggleServerButtonText.set("Start Server");
            connectionInfoText.set("Connection closed");
            server = null;
        }
    }
    
    public void loadSettingsFile() {
        StageController.getInstance().loadSettingsFile();
        // We can only change the screens if the app hasn't started yet
        if (!launched.get()) {
            fullScreenChecked.set(GlobalSettings.FULLSCREEN);
            if (getScreenIndexList().contains(GlobalSettings.TOUCH_SCREEN_ID)) {
                touchScreenSelection.set(GlobalSettings.TOUCH_SCREEN_ID);
            }
            if (getScreenIndexList().contains(GlobalSettings.DETAIL_SCREEN_ID)) {
                detailScreenSelection.set(GlobalSettings.DETAIL_SCREEN_ID);
            }
        }
    }
    
    private void updateSettingsText() {
        settingsText.set(GlobalSettings.settingsToString());
    }
}

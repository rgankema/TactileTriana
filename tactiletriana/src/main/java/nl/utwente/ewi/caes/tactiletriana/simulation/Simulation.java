/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import nl.utwente.ewi.caes.tactiletriana.Concurrent;
import static nl.utwente.ewi.caes.tactiletriana.Concurrent.runOnJavaFXThreadSynchronously;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.BufferBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.TimeShiftableBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.UncontrollableLoad;

/**
 *
 * @author Richard
 */
public class Simulation extends SimulationBase {

    private static final TimeScenario DEFAULT_SCENARIO = new TimeScenario(new TimeScenario.TimeSpan(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 12, 31)));

    public Simulation() {
        this.setState(SimulationState.STOPPED);
        // Initialise time
        setCurrentTime(getTimeScenario().getCurrentTime());
    }

    // PROPERTIES
    /**
     * The state of the simulation.
     */
    private final ReadOnlyObjectWrapper<SimulationState> state = new ReadOnlyObjectWrapper<>();

    public ReadOnlyObjectProperty<SimulationState> stateProperty() {
        return state.getReadOnlyProperty();
    }

    private void setState(SimulationState state) {
        this.state.set(state);
    }

    public SimulationState getState() {
        return this.stateProperty().get();
    }

    /**
     * The time scenario that this simulation follows.
     */
    private final ObjectProperty<TimeScenario> timeScenario = new SimpleObjectProperty<>(DEFAULT_SCENARIO);

    public ObjectProperty<TimeScenario> timeScenarioProperty() {
        return timeScenario;
    }

    public final TimeScenario getTimeScenario() {
        return timeScenarioProperty().get();
    }

    public final void setTimeScenario(TimeScenario timeScenario) {
        timeScenarioProperty().set(timeScenario);
    }

    /**
     * Whether the Simulation will use its controller for device plannings.
     */
    private final BooleanProperty trianaEnabled = new SimpleBooleanProperty(true);

    public BooleanProperty trianaEnabledProperty() {
        return trianaEnabled;
    }

    public final boolean isTrianaEnabled() {
        return trianaEnabledProperty().get();
    }

    public final void setTrianaEnabled(boolean trianaEnabled) {
        trianaEnabledProperty().set(trianaEnabled);
    }
    
    /**
     * The Controller that controls the devices in this simulation. May be null.
     */
    private final ObjectProperty<IController> controller = new SimpleObjectProperty<>(null);
    
    public ObjectProperty<IController> controllerProperty() {
        return controller;
    }
    
    public final IController getController() {
        return controllerProperty().get();
    }

    public final void setController(IController controller) {
        controllerProperty().set(controller);
    }

    // EVENT HANDLING
    private final List<Runnable> timeSpanShiftedCallbacks = new ArrayList<>();

    public final void addOnTimeSpanShiftedHandler(Runnable handler) {
        timeSpanShiftedCallbacks.add(handler);
    }

    public final void removeOnTimeSpanShiftedHandler(Runnable handler) {
        timeSpanShiftedCallbacks.remove(handler);
    }

    // PUBLIC METHODS
    private boolean startedOnce = false;

    /**
     * Starts the simulation if it is stopped, or resumes it if it is paused. If
     * the simulation is stopped, it will start a new thread to run the
     * simulation on.
     */
    public void start() {
        if (!startedOnce) {
            startedOnce = true;
            Concurrent.getExecutorService().scheduleAtFixedRate(() -> {
                if (this.getState() == SimulationState.RUNNING) {
                    tick();
                }
            }, GlobalSettings.SYSTEM_TICK_TIME, GlobalSettings.SYSTEM_TICK_TIME, TimeUnit.MILLISECONDS);
        }
        setState(SimulationState.RUNNING);
    }

    /**
     * Pauses the simulation if it is running. Unlike stop, this method does not
     * shut down the simulation thread.
     */
    public void pause() {
        setState(SimulationState.PAUSED);
    }

    /**
     * Resets all values to default.
     */
    public void reset() {
        setState(SimulationState.STOPPED);
        int i = 0;
        for (House house : houses) {
            house.repairFuse();
            house.getDevices().clear();
            if (GlobalSettings.UNCONTROLLABLE_LOAD_ENABLED) {
                house.getDevices().add(new UncontrollableLoad(i, this));
            }
            i++;
        }
        for (Cable cable : internalCables) {
            cable.repair();
        }
        for (Cable cable : houseCables) {
            cable.repair();
        }
        clearAllLogs();
        getTimeScenario().reset();
        setCurrentTime(getTimeScenario().getCurrentTime());
    }

    /**
     * Called at the start of each tick. Parts of the method have to run on the
     * JavaFX thread because of property bindings.
     */
    @Override
    protected final void tick() {
        // Run anything that involves the UI on the JavaFX thread
        runOnJavaFXThreadSynchronously(() -> {
            getTransformer().tick(true);
        });

        // Reset the nodes.
        prepareForwardBackwardSweep();
        // Calculate forward backward sweep
        doForwardBackwardSweep();
        
        // Run anything that involves the UI on the JavaFX thread
        runOnJavaFXThreadSynchronously(() -> {
            // Finish forward backward sweep
            finishForwardBackwardSweep();
            // Log total power consumption in network
            log(getCurrentTime(), transformer.getCables().get(0).getCurrent() * 230d);
            // Increment time
            incrementTime();
        });

        if (getController() != null && isTrianaEnabled()) {
            getController().retrievePlanning(50, getCurrentTime());
        }

        //If the api was just disabled, clear all the current plannings
        if (!isTrianaEnabled()) {
            //Clear all currently set plannings
            for (DeviceBase device : this.getDevices()) {
                if (device instanceof BufferBase) {
                    ((BufferBase) device).getPlanning().clear();
                } else if (device instanceof TimeShiftableBase) {
                    ((TimeShiftableBase) device).getPlanning().clear();
                }
            }
        }
    }

    /**
     * Increments the time by either the tick time, or leaps to the next time
     * span if required.
     */
    @Override
    protected void incrementTime() {
        boolean timeSpanShifted = getTimeScenario().next(GlobalSettings.TICK_MINUTES);
        setCurrentTime(getTimeScenario().getCurrentTime());
        if (timeSpanShifted) {

            for (Runnable callback : timeSpanShiftedCallbacks) {
                callback.run();
            }
            clearAllLogs();
        }
    }

    // HELP METHODS
    private void clearAllLogs() {
        for (House house : houses) {
            house.getLog().clear();
            house.invalid = true;
        }
        for (Node node : internalNodes) {
            node.getLog().clear();
            node.invalid = true;
        }
        for (Node node : houseNodes) {
            node.getLog().clear();
            node.invalid = true;
        }
        for (Cable cable : internalCables) {
            cable.getLog().clear();
            cable.invalid = true;
        }
        for (Cable cable : houseCables) {
            cable.getLog().clear();
            cable.invalid = true;
        }
        transformer.getLog().clear();
        for (Cable cable : transformer.getCables()) {
            cable.getLog().clear();
            cable.invalid = true;
        }
        this.getLog().clear();
        this.invalid = true;
    }

    // NESTED ENUMS
    /**
     * Describes the state the Simulation is in.
     */
    public static enum SimulationState {

        /**
         * The Simulation is running.
         */
        RUNNING,
        /**
         * The Simulation is paused.
         */
        PAUSED,
        /**
         * The Simulation is in its initial state.
         */
        STOPPED
    };
}

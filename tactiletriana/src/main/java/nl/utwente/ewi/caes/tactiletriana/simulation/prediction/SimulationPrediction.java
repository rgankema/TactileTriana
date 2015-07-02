/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation.prediction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.scene.chart.XYChart.Data;
import nl.utwente.ewi.caes.tactiletriana.Concurrent;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import static nl.utwente.ewi.caes.tactiletriana.Util.toMinuteOfYear;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntityBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.SimulationBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.*;

/**
 *
 * @author mickvdv
 */
public class SimulationPrediction extends SimulationBase {

    // Amount of hours that the prediction runs ahead

    private final static int RUN_AHEAD = 6;

    private final Simulation mainSimulation;
    private final Map<LoggingEntityBase, LoggingEntityBase> futureByActual = new HashMap<>();

    private boolean mainSimulationChanged = false;
    private boolean cancelled = false;

    /**
     * Creates a new SimulationPrediction.
     *
     * @param mainSimulation The real Simulation that this object will predict
     */
    public SimulationPrediction(Simulation mainSimulation) {
        this.mainSimulation = mainSimulation;
        setCurrentTime(mainSimulation.getCurrentTime());

        // Link this (future) simulation to acual simulation
        futureByActual.put(mainSimulation, this);
        linkNetwork(mainSimulation.getTransformer(), this.getTransformer());

        mainSimulation.addOnTimeSpanShiftedHandler(() -> {
            mainSimulationChanged = false;
            // Clear the log
            for (LoggingEntityBase logger : futureByActual.values()) {
                logger.getLog().clear();
                // Reset state of charges of all buffers
                if (logger instanceof BufferBase) {
                    ((BufferBase) logger).setStateOfCharge(((BufferBase) getActual(logger)).getStateOfCharge());
                    ((BufferBase) logger).setCurrentConsumption(((BufferBase) getActual(logger)).getCurrentConsumption());
                }
            }
            setCurrentTime(mainSimulation.getCurrentTime());
        });

        // Zorg dat de simulatie 12 uur vooruit loopt
        this.mainSimulation.currentTimeProperty().addListener((observable, oldValue, newValue) -> {

            // Er is iets veranderd. Run de simulation vanaf het huidige punt vooruit
            if (mainSimulationChanged) {
                mainSimulationChanged = false;
                cancelled = true;
                setCurrentTime(oldValue);

                // Clear the invalid log values
                int minuteOfYear = toMinuteOfYear(oldValue);
                for (LoggingEntityBase logger : futureByActual.values()) {
                    List<Data<Integer, Float>> toRemove = new ArrayList<>();
                    for (Data<Integer, Float> data : logger.getLog()) {
                        if (data.getXValue() >= minuteOfYear) {
                            toRemove.add(data);
                        }
                    }
                    for (Data<Integer, Float> data : toRemove) {
                        logger.getLog().remove(data);
                    }

                    // Reset state of charges of all buffers
                    if (logger instanceof BufferBase) {
                        ((BufferBase) logger).setCurrentConsumption(((BufferBase) getActual(logger)).getCurrentConsumption());
                        ((BufferBase) logger).setStateOfCharge(((BufferBase) getActual(logger)).getStateOfCharge());
                    }
                }
            }

            // Calculate new ticks in background
            cancelled = false;
            Concurrent.getExecutorService().submit(() -> {
                // Do tick while still behind, but only if the main simulation hasn't changed again
                while (!cancelled && getCurrentTime().isBefore(newValue.plusHours(RUN_AHEAD))) {
                    tick();
                }
            });

        });
    }

    // SIMULATIONBASE
    /**
     * Called at the start of each tick
     */
    @Override
    protected final void tick() {
        // Calculate device consumptions
        getTransformer().tick(true);
        // Reset the nodes and cables
        prepareForwardBackwardSweep();
        // Calculate forward backward sweep
        doForwardBackwardSweep();
        // Finish forward backward sweep
        finishForwardBackwardSweep();
        // Log total power consumption in network
        log(getCurrentTime(), transformer.getCables().get(0).getCurrent() * 230d);
        // Increment time
        incrementTime();
    }

    @Override
    protected void incrementTime() {
        setCurrentTime(getCurrentTime().plusMinutes(GlobalSettings.TICK_MINUTES));
    }

    // HELPER METHODS
    /**
     * Walks through the network tree and synchronizes equivalent LoggingEntityBases
     */
    private void linkNetwork(Node actual, Node future) {
        futureByActual.put(actual, future);
        if (actual.getHouse() != null) {
            linkHouse(actual.getHouse(), future.getHouse());
        }
        for (int i = 0; i < actual.getCables().size(); i++) {
            Cable actualCable = actual.getCables().get(i);
            Cable futureCable = future.getCables().get(i);
            actualCable.brokenProperty().addListener((observable, wasBroken, isBroken) -> {
                if (!isBroken) {
                    futureCable.repair();
   
                    mainSimulationChanged = true;
                }
            });
            // Bind length
            actualCable.lengthProperty().addListener(obs -> {
                futureCable.setLength(actualCable.getLength());
                mainSimulationChanged = true;
            });
            futureByActual.put(actualCable, futureCable);
            linkNetwork(actualCable.getChildNode(), futureCable.getChildNode());
        }
    }

    /**
     * Synchronizes two houses by synchronizing its device list
    */
    private void linkHouse(House actualHouse, House futureHouse) {
        futureByActual.put(actualHouse, futureHouse);
        //futureDevice.getProperties().get(property).bind(actualDevice.getProperties().get(property));

        // bind the fuse property from actualHouse to futureHouse
        actualHouse.fuseBlownProperty().addListener((observable, wasBroken, isBroken) -> {
            if (!isBroken) {
                futureHouse.repairFuse();
                mainSimulationChanged = true;
            }
        });

        actualHouse.getDevices().addListener((ListChangeListener.Change<? extends DeviceBase> c) -> {
            while (c.next()) {
                for (DeviceBase item : c.getRemoved()) {
                    mainSimulationChanged = true;
                    futureHouse.getDevices().remove((DeviceBase) futureByActual.get(item));
                }
                for (DeviceBase actualDevice : c.getAddedSubList()) {
                    mainSimulationChanged = true;

                    // maak een kopie van dit device in de map
                    DeviceBase futureDevice = (DeviceBase) futureByActual.get(actualDevice);
                    if (futureDevice == null) {
                        if (actualDevice instanceof Buffer) {
                            futureDevice = new Buffer(this);
                        } else if (actualDevice instanceof ElectricVehicle) {
                            futureDevice = new ElectricVehicle(this, ((ElectricVehicle) actualDevice).getModel());
                        } else if (actualDevice instanceof DishWasher) {
                            futureDevice = new DishWasher(this);
                        } else if (actualDevice instanceof SolarPanel) {
                            futureDevice = new SolarPanel(this);
                        } else if (actualDevice instanceof WashingMachine) {
                            futureDevice = new WashingMachine(this);
                        } else if (actualDevice instanceof BufferConverter) {
                            futureDevice = new BufferConverter(this);
                        } else if (actualDevice instanceof TrianaHouseController) {
                            futureDevice = new TrianaHouseController(this);
                        } else if (actualDevice instanceof UncontrollableLoad) {
                            futureDevice = new UncontrollableLoad(((UncontrollableLoad)actualDevice).getProfileNumber(), this);
                        } else {
                            throw new UnsupportedOperationException("Copying instances of type "
                                    + actualDevice.getClass().getName() + " not supported.");
                        }
                    }

                    // sla het nieuwe device op in de map
                    futureByActual.put(actualDevice, futureDevice);

                    // bind alle parameters
                    for (int i = 0; i < actualDevice.getProperties().size(); i++) {
                        futureDevice.getProperties().get(i).bind(actualDevice.getProperties().get(i));
                        actualDevice.getProperties().get(i).addListener(observable -> {
                            mainSimulationChanged = true;
                        });
                    }
                    for (int i = 0; i < actualDevice.getLists().size(); i++) {
                        Bindings.bindContent(futureDevice.getLists().get(i), actualDevice.getLists().get(i));
                        actualDevice.getLists().get(i).addListener((Observable observable) -> {
                            mainSimulationChanged = true;
                        });
                    }
                    for (int i = 0; i < actualDevice.getMaps().size(); i++) {
                        Bindings.bindContent(futureDevice.getMaps().get(i), actualDevice.getMaps().get(i));
                        actualDevice.getMaps().get(i).addListener((Observable observable) -> {
                            mainSimulationChanged = true;
                        });
                    }

                    // voeg het toe aan dit huis
                    futureHouse.getDevices().add(futureDevice);
                }
            }
        });
    }

    // PUBLIC METHODS
    /**
     * Returns a LoggingEntityBase that represents the future version of the
     * specified LoggingEntityBase.
     *
     * @param actual the real LoggingEntityBase
     * @return the future representation
     */
    public final LoggingEntityBase getFuture(LoggingEntityBase actual) {
        return futureByActual.get(actual);
    }

    /**
     * Returns the LoggingEntityBase that the specified LoggingEntityBase is the
     * the future version of.
     *
     * @param future the future representation of a LoggingEntityBase
     * @return the real LoggingEntityBase
     */
    public final LoggingEntityBase getActual(LoggingEntityBase future) {
        for (LoggingEntityBase actual : futureByActual.keySet()) {
            if (futureByActual.get(actual).equals(future)) {
                return actual;
            }
        }
        return null;
    }

}

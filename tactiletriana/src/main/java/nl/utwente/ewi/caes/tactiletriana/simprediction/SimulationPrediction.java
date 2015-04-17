/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simprediction;

import java.util.HashMap;
import java.util.Map;
import javafx.collections.ListChangeListener;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author mickvdv
 */
public class SimulationPrediction extends Simulation {
    public final static int RUN_AHEAD = 6; // aantal uren dat de prediction voorloopt
    
    private final Simulation mainSimulation;
    private boolean mainSimulationChanged = false;
    public Map<DeviceBase, DeviceBase> shadowDeviceMap = new HashMap<>();

    public SimulationPrediction(Simulation mainSimulation) {
        super();
        this.mainSimulation = mainSimulation;
        setCurrentTime(mainSimulation.getCurrentTime());

        // this() koppelen aan mainSimulation via HousePredictor()
        for (int iN = 0; iN < mainSimulation.getHouses().length; iN++) {
            linkHouse(mainSimulation.getHouses()[iN], getHouses()[iN]);
        }

        // zorg dat de simulatie 12 uur vooruit loopt
        this.mainSimulation.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            // er is iets veranderd. Run de simulation vanaf het huidige punt vooruit
            if (mainSimulationChanged) {
                mainSimulationChanged = false;
                setCurrentTime(newValue);
            }

            // Zo lang hij achterloopt -> doe een tick()
            while (getCurrentTime().isBefore(newValue.plusHours(RUN_AHEAD))) {
                tick();
            }
        });
    }
    
    private void linkHouse(House actual, House future) {
        actual.getDevices().addListener(new ListChangeListener<DeviceBase>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends DeviceBase> c) {
                while (c.next()) {
                    for (DeviceBase item : c.getRemoved()) {
                        mainSimulationChanged = true;
                        future.getDevices().remove(shadowDeviceMap.get(item));
                    }
                    for (DeviceBase item : c.getAddedSubList()) {
                        mainSimulationChanged = true;

                        // maak een kopie van dit device in de map
                        DeviceBase newDevice = null;
                        try {
                            newDevice = (DeviceBase) item.getClass().getConstructors()[0].newInstance(simulation);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }

                        // sla het nieuwe device op in de map
                        shadowDeviceMap.put(item, newDevice);

                        // bind alle parameters
                        for (int i = 0; i < item.getParameters().size(); i++) {
                            newDevice.getParameters().get(i).property.bind(item.getParameters().get(i).property);

                            // als er iets aan de parameters veranderd moet de simulation.setMainSimulationChanged() aangeroepen worden
                            // dit zorgt ervoor dat bij de eerst volgende tick() van de main simulation de prediction opnieuw begint
                            item.getParameters().get(i).property.addListener(observable -> {
                                mainSimulationChanged = true;
                            });
                        }

                        // voeg het toe aan dit huis
                        future.getDevices().add(newDevice);
                    }
                }
            }
        });
    }
}

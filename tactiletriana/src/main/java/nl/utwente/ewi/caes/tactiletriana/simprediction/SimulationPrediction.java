/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simprediction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import nl.utwente.ewi.caes.tactiletriana.App;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import nl.utwente.ewi.caes.tactiletriana.simulation.devices.UncontrollableLoad;

/**
 *
 * @author mickvdv
 */
public class SimulationPrediction extends Simulation {
    public final static int RUN_AHEAD = 6; // aantal uren dat de prediction voorloopt
    private static SimulationPrediction instance;
    private Simulation mainSimulation;

    ArrayList<HousePredictor> housePredictors;

    public SimulationPrediction(Simulation mainSimulation) {
        super();
        this.mainSimulation = mainSimulation;

        // this() koppelen aan mainSimulation via HousePredictor()
        housePredictors = new ArrayList<>();
        for (int iN = 0; iN < mainSimulation.getHouseNodes().length; iN++) {
            HousePredictor h = new HousePredictor(mainSimulation.getHouses()[iN], this);
            housePredictors.add(h);

            if (Simulation.UNCONTROLABLE_LOAD_ENABLED) {
                h.getDevices().add(new UncontrollableLoad(iN, this.simulation));
            }

            // vervang alle houses in this().houseNodes[] door HousePredictors gekoppeld aan houses van de mainSimulation.
            this.getHouseNodes()[iN].setHouse(h);

            // vervang alle houses in this().houses[] 
            this.getHouses()[iN] = h;
        }

        // zorg dat de simulatie 12 uur vooruit loopt
        this.mainSimulation.currentTimeProperty().addListener(new ChangeListener<LocalDateTime>() {
            @Override
            public void changed(ObservableValue<? extends LocalDateTime> observable, LocalDateTime oldValue, LocalDateTime newValue) {
                // Zo lang hij achterloopt -> doe een tick()
                while (getCurrentTime().plusHours(RUN_AHEAD).isBefore(newValue)) {
                    simulateTick();
                }
            }

        });

    }
}

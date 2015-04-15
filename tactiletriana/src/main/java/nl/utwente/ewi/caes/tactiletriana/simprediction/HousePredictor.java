/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simprediction;

import javafx.collections.ListChangeListener;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author mickvdv
 */
public class HousePredictor extends House {

    House linkedHouse;

    /**
     *
     * @param linkedHouse House waar deze HousePrediciton aan gekoppeld is.
     * @param simulation
     */
    public HousePredictor(House linkedHouse, Simulation simulation) {
        super(simulation);
        this.linkedHouse = linkedHouse;

        this.linkedHouse.getDevices().addListener(new ListChangeListener<DeviceBase>() {

            @Override
            public void onChanged(ListChangeListener.Change<? extends DeviceBase> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutate
                        }
                    } else if (c.wasUpdated()) {
                        //update item
                    } else {
                        for (DeviceBase remitem : c.getRemoved()) {
                            //remitem.remove(Outer.this);
                        }
                        for (DeviceBase additem : c.getAddedSubList()) {
                            //additem.add(Outer.this);
                        }
                    }
                }
            }
        });

    }
}



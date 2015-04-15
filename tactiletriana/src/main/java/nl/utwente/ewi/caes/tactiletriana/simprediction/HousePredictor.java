/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simprediction;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    Map<DeviceBase, DeviceBase> shadowDeviceMap;

    public HousePredictor(House linkedHouse, Simulation simulation) {
        super(simulation);
        this.linkedHouse = linkedHouse;
        shadowDeviceMap = new HashMap<DeviceBase, DeviceBase>();

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
                        for (DeviceBase item : c.getRemoved()) {
                            //remitem.remove(Outer.this);
                            getDevices().remove(shadowDeviceMap.get(item));
                        }
                        for (DeviceBase item : c.getAddedSubList()) {
                            // maak een kopie van dit device in de map
                            DeviceBase newDevice = null;
                            try {
                                //additem.add(Outer.this);
                                newDevice = item.getClass().asSubclass(item.getClass()).getConstructor(simulation.getClass()).newInstance(simulation);
                            } catch (Exception ex) {
                                System.out.println("House Predictor is stuk");
                            }
                            
                            // sla het nieuwe device op in de map
                            shadowDeviceMap.put(item, newDevice);
                            
                            // bind alle parameters
                            for (int i = 0; i < item.getParameters().size(); i++){
                                newDevice.getParameters().get(i).property.bind(item.getParameters().get(i).property);
                            }
                            
                            // voeg het toe aan dit huis
                            getDevices().add(newDevice);
                        }
                    }
                }
            }
        });

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author Richard
 */
public class Simulation {
    public static final int NUMBER_OF_HOUSES = 6;   // number of houses
    public static final int TICK_TIME = 100;        // time between ticks in ms
    
    private final Transformer transformer;
    private final Map<Node, Double> lastVoltageByNode;
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    private IController controller;
    
    public Simulation() {
        // keep an array of nodes for later reference
        this.lastVoltageByNode = new HashMap<>();
        
        // de tree maken
        transformer = new Transformer();
        
        Node[] internalNodes = new Node[NUMBER_OF_HOUSES];
        Node[] houseNodes = new Node[NUMBER_OF_HOUSES];
        Cable[] cables = new Cable[NUMBER_OF_HOUSES];
        House[] houses = new House[NUMBER_OF_HOUSES];
        
        // maak huizen aan met cables en dat soort grappen
        for(int i = 0; i <= NUMBER_OF_HOUSES-1; i ++){
            houses[i] = new House();
            
            houseNodes[i] = new Node(houses[i]);
            internalNodes[i] = new Node(null);
            Cable houseCable = new Cable(houseNodes[i], 110);
            internalNodes[i].getCables().add(houseCable);
            
            cables[i] = new Cable(internalNodes[i], 110 + (NUMBER_OF_HOUSES - i) * 60);
            if (i == 0) {
                transformer.getCables().add(cables[i]);
            }
            else {
                internalNodes[i-1].getCables().add(cables[i]);
            }
            
            lastVoltageByNode.put(internalNodes[i], 230d);
            lastVoltageByNode.put(houseNodes[i], 230d);
        }
        
        // initialise time
        setCurrentTime(0);
    }
    
    // PROPERTIES
    
    /**
     * The current time in the simulation.
     */
    private final DoubleProperty currentTime = new SimpleDoubleProperty(0d);
    
    public DoubleProperty currentTimeProperty() {
        return currentTime;
    }
    
    public double getCurrentTime() {
        return currentTimeProperty().get();
    }
    
    private void setCurrentTime(double time) {
        currentTimeProperty().set(time);
    }
    
    /**
     * The Controller that controls the devices in this simulation. May be null.
     * @return 
     */
    public IController getController() {
        return controller;
    }
    
    public void setController(IController controller) {
        this.controller = controller;
    }
    
    /**
     * The root of the network.
     * @return 
     */
    public Transformer getTransformer() {
        return transformer;
    }
    
    // PUBLIC METHODS
    
    public void start() {
        scheduler.scheduleAtFixedRate(() -> {
            // Todo: optimize dit, dit is slechts een hotfix
            // Uiteraard nogal idioot om de hele meuk op de JavaFX thread te draaien
            Platform.runLater(() -> { 
                getTransformer().tick(this, true);
                initiateForwardBackwardSweep();
            });
            
            setCurrentTime((getCurrentTime() + 1) % (24 * 60)); // een minuut per tick voor nu
        }, TICK_TIME, TICK_TIME, TimeUnit.MILLISECONDS);
    }
    
    public void stop() {
        scheduler.shutdown();
    }
    
    // FORWARD BACKWARD SWEEP METHODS

    // Start the forward backward sweep algorithm
    private void initiateForwardBackwardSweep() {
        // First reset the nodes.
        transformer.reset();
        // Run the ForwardBackwardSweep Load-flow calculation until converged or the iteration limit is reached
        for(int i = 0; i < 20; i++) {
            transformer.doForwardBackwardSweep(230); // this runs recursivly down the tree
            
            if (hasFBSConverged(0.0001)) break;
            
            // Store last voltage to check for convergence
            for (Node node : this.lastVoltageByNode.keySet()) {
                lastVoltageByNode.put(node, node.getVoltage());
            }
        }
    }
    
    // Calculate if the FBS algorithm has converged. 
    private boolean hasFBSConverged(double error) {
        boolean result = true;
        
        //Loop through the network-tree and compare the previous voltage from each with the current voltage.
        //If the difference between the previous and current voltage is smaller than the given error, the result is true
        for (Node node : this.lastVoltageByNode.keySet()) {
            if (!result) break;
            result = (Math.abs(lastVoltageByNode.get(node) - node.getVoltage()) < error);
        }
        return result;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

/**
 *
 * @author jd
 */
public class SimpleTest {
    public static void main(String args[]) {
        Simulation simulation = Simulation.getInstance();
        simulation.initiateForwardBackwardSweep();
        System.out.println(simulation.getTransformer().toString());
    }
}

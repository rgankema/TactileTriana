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
public interface IFWBWSEntity {

    /**
     * Called once just before the forward backward sweep
     */
    public void prepareForwardBackwardSweep();
    
    /**
     * The actual forward backward sweep
     */
    public double doForwardBackwardSweep(double voltage);

    /**
     * Called when the forward backward sweep has converged
     */
    public void finishForwardBackwardSweep();
}

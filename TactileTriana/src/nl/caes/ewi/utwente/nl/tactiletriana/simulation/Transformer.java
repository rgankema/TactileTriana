/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.simulation;

/**
 *
 * @author Richard
 */
public class Transformer extends Node {
    public Transformer() {
        super(null);
    }
    
    public void initiateForwardBackwardSweep() {
        for (ICable c : getCables()) {
            // do forward backward sweep on cables
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public String toString(){
        String output = "(Transformer:U="+this.getVoltage()+")\n";
        for (ICable c: this.getCables()){
            output += "->";
            output += c.toString();
            
        }
        return output;
    }
}

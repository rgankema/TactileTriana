/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.simulation;

/**
 *
 * @author Richard
 */
public class Transformer extends Node implements IFWBWSweepEntity {
    public final double TRANSFORMER_CURRENT = 230.0;
    
    public Transformer() {
        super(null);
        setVoltage(230);
    }
    
    @Override
    public String toString(){
        return toString(0);
    }
    
    @Override
    public String toString(int indentation){
        String output = "";
        for (int i = 0; i < indentation; i++){
            output += "\t";
        }
        output += "|-";
        
        output = "(Transformer:U="+this.getVoltage()+")\n";
        for (Cable c: this.getCables()){
            output += c.toString(indentation+1);
        }
        return output;
    }
    
}

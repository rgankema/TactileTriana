/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer;

import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.LoggingEntityVMBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.Transformer;

/**
 *
 * @author Richard
 */
public class TransformerVM extends LoggingEntityVMBase {
    private final Transformer model;
    
    public TransformerVM(Transformer model) {
        this.model = model;
    }
    
    public void longPressed() {
        StageController.getInstance().showOnChart(this, model);
    }
}

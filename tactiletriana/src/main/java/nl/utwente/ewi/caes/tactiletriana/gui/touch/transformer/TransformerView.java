/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer;

import javafx.scene.shape.Rectangle;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class TransformerView extends Rectangle {
    private TransformerVM viewModel;
    
    public TransformerView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(TransformerVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel already set");
        
        this.viewModel = viewModel;
    }
}

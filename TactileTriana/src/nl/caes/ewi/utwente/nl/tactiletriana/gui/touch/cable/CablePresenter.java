/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable;

import javafx.fxml.FXML;
import javafx.scene.Node;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class CablePresenter {
    private @FXML Node root;
    
    private CableVM viewModel;
    
    public Node getView() {
        return root;
    }
    
    protected void setViewModel(CableVM viewModel) {
        this.viewModel = viewModel;
    }
}

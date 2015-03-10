/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import nl.utwente.cs.caes.tactile.control.TactilePane;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class TouchPresenter implements Initializable {
    @FXML TactilePane root;
    
    private TouchVM viewModel;
    
    public TactilePane getView() {
        return root;
    }
    
    protected void setViewModel(TouchVM viewModel) {
        this.viewModel = viewModel;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch;

import java.io.IOException;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author Richard
 */
public class TouchPresenterFactory {
    public TouchPresenter buildTouchPresenter(TouchVM viewModel) {        
        TouchPresenter presenter = null;
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResourceAsStream("/nl/caes/ewi/utwente/nl/tactiletriana/gui/touch/TouchView.fxml"));
            presenter = (TouchPresenter) loader.getController();
            presenter.setViewModel(viewModel);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load TouchView.fxml", e);
        }
        
        return presenter;
    }
}

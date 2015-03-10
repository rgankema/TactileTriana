/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node;

import java.io.IOException;
import javafx.fxml.FXMLLoader;

/**
 *
 * @author Richard
 */
public class NodePresenterFactory {
    public NodePresenter buildNodePresenter(NodeVM viewModel) {        
        NodePresenter presenter = null;
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.load(getClass().getResourceAsStream("/nl/caes/ewi/utwente/nl/tactiletriana/gui/touch/node/NodeView.fxml"));
            presenter = (NodePresenter) loader.getController();
            presenter.setViewModel(viewModel);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load NodeView.fxml", e);
        }
        
        return presenter;
    }
}

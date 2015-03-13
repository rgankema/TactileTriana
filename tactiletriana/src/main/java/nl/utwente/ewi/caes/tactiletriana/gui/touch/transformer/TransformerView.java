/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Richard
 */
public class TransformerView extends Rectangle {
    public TransformerView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TransformerView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load TransformerView.fxml", e);
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.device;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Richard
 */
public class DeviceView extends StackPane {
    public DeviceView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("DeviceView.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load DeviceView.fxml", e);
        }
    }
}

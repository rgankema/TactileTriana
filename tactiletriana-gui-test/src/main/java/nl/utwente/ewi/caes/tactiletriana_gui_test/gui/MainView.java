/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana_gui_test.gui;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import nl.utwente.ewi.caes.tactiletriana_gui_test.gui.config.ConfigView;
import nl.utwente.ewi.caes.tactiletriana_gui_test.gui.test.TestView;

/**
 *
 * @author Richard
 */
public class MainView extends BorderPane {
    @FXML private ConfigView configView;
    
    public MainView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load MainView.fxml", e);
        }
        
        setLeft(new TestView(configView));
    }
}

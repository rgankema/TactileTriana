/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana_gui_test.gui;

import java.io.IOException;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;
import nl.utwente.ewi.caes.tactiletriana_gui_test.gui.config.ConfigView;
import nl.utwente.ewi.caes.tactiletriana_gui_test.gui.test.TestView;
import static org.mockito.Mockito.*;

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

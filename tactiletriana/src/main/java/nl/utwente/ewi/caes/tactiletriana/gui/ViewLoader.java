/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 *
 * @author Richard
 */
public class ViewLoader {
    
    /**
     * Loads an FXML file belonging to the given view, and sets that view as the
     * root and controller of that FXML file. Requires that an FXML file exists 
     * at src/main/resources/fxml with the same name as the simple class name of 
     * the given view.
     * @param view the view to load
     */
    public static void load(Node view) {
        String viewName = view.getClass().getSimpleName();
        
        FXMLLoader loader = new FXMLLoader(view.getClass().getResource(String.format("/fxml/%s.fxml", viewName)));
        loader.setRoot(view);
        loader.setController(view);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(String.format("Could not load %s.fxml", viewName), e);
        }
    }
}

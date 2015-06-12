/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.trash;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class TrashView extends StackPane {
    @FXML private Node activeZone;
    
    public TrashView() {
        ViewLoader.load(this);
    }
    
    public Node getActiveZone() {
        return activeZone;
    }
}

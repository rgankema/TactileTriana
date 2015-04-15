/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer;

import javafx.scene.shape.Rectangle;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class TransformerView extends Rectangle {

    public TransformerView() {
        ViewLoader.load(this);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.launcher;

import javafx.scene.control.Label;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class ScreenIndexView extends Label {
    public ScreenIndexView(int i) {
        ViewLoader.load(this);

        setText(Integer.toString(i));
    }
}

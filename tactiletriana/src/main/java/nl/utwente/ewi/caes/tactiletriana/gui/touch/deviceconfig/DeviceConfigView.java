/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.deviceconfig;

import javafx.scene.layout.GridPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class DeviceConfigView extends GridPane {
    public DeviceConfigView() {
        ViewLoader.load(this);
        
    }
}

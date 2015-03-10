/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable;

import javafx.scene.shape.Polygon;

/**
 *
 * @author Richard
 */
public class DirectionView extends Polygon {
    public DirectionView() {
        super(new double[] {
            -5,0,
            -5,-5,
            0,-10,
            5,-5,
            5,0,
            0,-5
        });
        
        setStrokeWidth(0);
    }
}

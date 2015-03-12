/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 *
 * @author Richard
 */
public class DirectionView extends StackPane {
    private Polygon arrow;
    public DirectionView() {
        arrow = new Polygon(new double[] {
            -5,0,
            -5,-5,
            0,-10,
            5,-5,
            5,0,
            0,-5
        });
        for (int i = 1; i < arrow.getPoints().size() ; i += 2) {
            arrow.getPoints().set(i, arrow.getPoints().get(i) + 5);
        }
        arrow.setStrokeWidth(0);
        StackPane.setAlignment(arrow, Pos.BOTTOM_CENTER);
        
        Rectangle background = new Rectangle(60, 60);
        background.setFill(Color.TRANSPARENT);
        getChildren().addAll(background, arrow);
    }
}

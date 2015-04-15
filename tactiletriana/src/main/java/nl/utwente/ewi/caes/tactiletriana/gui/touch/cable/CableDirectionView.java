/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * A polygon in the shape of an arrow to display the direction of current in a
 * cable.
 *
 * @author Richard
 */
public class CableDirectionView extends StackPane {

    private final Polygon arrow;

    public CableDirectionView() {
        arrow = new Polygon(new double[]{
            -5, 5,
            -5, 0,
            0, -5,
            5, 0,
            5, 5,
            0, 0
        });

        arrow.setStrokeWidth(0);
        StackPane.setAlignment(arrow, Pos.BOTTOM_CENTER);

        // Dirty trick to make the arrow align better
        Rectangle background = new Rectangle(60, 60);
        background.setFill(Color.TRANSPARENT);

        getChildren().addAll(background, arrow);
    }
}

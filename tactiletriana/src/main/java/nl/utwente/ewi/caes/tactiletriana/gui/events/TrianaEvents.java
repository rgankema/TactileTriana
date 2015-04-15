/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.events;


import java.util.function.Consumer;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 *
 * @author Richard
 */
public class TrianaEvents {
    /**
     * Adds a handler for when a Node is pressed for a short duration and for 
     * when its pressed for a longer (>700ms) duration. The longHandler is not
     * called when a long press is part of a drag operation.
     * 
     * @param <T>   The type of the Node
     * @param node  The Node to add the event handlers to
     * @param shortHandler  The function that should be called for a short press
     * @param longHandler   The function that should be called for a long press
     */
    public static <T extends Node> void addShortAndLongPressEventHandler(T node, Consumer<T> shortHandler, Consumer<T> longHandler) {
        Duration longPressDuration = Duration.millis(700);
        
        PauseTransition holdTimer = new PauseTransition(longPressDuration);
        
        // On a press, start the timer and set long press to false
        node.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            holdTimer.playFromStart();
        });
        // On a release, stop the timer, and if still within time, call shortHandler
        node.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> {
            if (holdTimer.getCurrentTime().lessThan(longPressDuration)) {
                if (shortHandler != null) shortHandler.accept(node);
            }
            holdTimer.stop();
        });
        // When drag detected, ignore long press
        node.addEventHandler(MouseEvent.DRAG_DETECTED, e -> holdTimer.stop());
        
        // If timer reaches end, call longHandler
        holdTimer.setOnFinished(event -> {
            longHandler.accept(node);
        });
    }
}

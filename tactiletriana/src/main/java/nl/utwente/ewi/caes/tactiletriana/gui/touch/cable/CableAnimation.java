/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.shape.Circle;

/**
 * Animates electricity flowing over a cable
 *
 * @author Richard
 */
class CableAnimation extends AnimationTimer {

    private final double BASE_SPEED = 1.0;
    private final double LOAD_MULTIPLIER = 3.0;

    private final CableView view;
    private final CableVM viewModel;
    private final List<Circle> onScreen = new ArrayList<>();
    private final Stack<Circle> offScreen = new Stack<>();

    private long previousTime = -1;

    public CableAnimation(CableView view) {
        this.view = view;
        this.viewModel = view.getViewModel();
    }

    @Override
    public void handle(long currentTime) {
        // No load means no particles
        if (viewModel.getLoad() == 0) {
            offScreen.addAll(onScreen);
            view.getChildren().removeAll(onScreen);
            onScreen.clear();
            return;
        }

        // Add new particle when more time has passed than the threshold
        if (previousTime == -1 || ((currentTime - previousTime) / 1000000) * viewModel.getLoad() >= 100) {
            addNewParticle();
            previousTime = currentTime;
        }

        // Distance vector from start of flow to end of flow
        Point2D end = (viewModel.getDirection() == CableVM.Direction.END)
                ? new Point2D(view.line.getEndX() - view.line.getStartX(), view.line.getEndY() - view.line.getStartY())
                : new Point2D(view.line.getStartX() - view.line.getEndX(), view.line.getStartY() - view.line.getEndY());
        Point2D direction = end.normalize();

        // Translate all particles on the screen
        for (int i = 0; i < onScreen.size(); i++) {
            Circle particle = onScreen.get(i);

            double speed = BASE_SPEED + LOAD_MULTIPLIER * viewModel.getLoad();

            double x = particle.getTranslateX() + direction.getX() * speed;
            double y = particle.getTranslateY() + direction.getY() * speed;

            // Remove particle if it reached its goal
            if (Math.abs(x) > Math.abs(end.getX()) || Math.abs(y) > Math.abs(end.getY())) {
                view.getChildren().remove(particle);
                onScreen.remove(i);
                offScreen.add(particle);
                i--;
                continue;
            }

            translate(particle, x, y);
        }
    }

    private void addNewParticle() {
        Circle particle;
        if (!offScreen.empty()) {
            particle = offScreen.pop();
            translate(particle, 0, 0);
        } else {
            particle = new Circle(view.line.getStrokeWidth() * 0.3);

            // Anchor base location to source of flow
            particle.layoutXProperty().bind(Bindings.createDoubleBinding(() -> {
                return (viewModel.getDirection() == CableVM.Direction.END) ? view.line.getStartX() : view.line.getEndX();
            }, viewModel.directionProperty(), view.line.startXProperty(), view.line.endXProperty()));
            particle.layoutYProperty().bind(Bindings.createDoubleBinding(() -> {
                return (viewModel.getDirection() == CableVM.Direction.END) ? view.line.getStartY() : view.line.getEndY();
            }, viewModel.directionProperty(), view.line.startYProperty(), view.line.endYProperty()));
            viewModel.directionProperty().addListener((obs, oldV, newV) -> {
                translate(particle, 0 - particle.getTranslateX(), 0 - particle.getTranslateY());
            });

            particle.getStyleClass().add("electricity");
            particle.setSmooth(false);
            particle.setCache(true);
            particle.setCacheHint(CacheHint.SPEED);
        }
        view.getChildren().add(particle);
        onScreen.add(particle);
    }

    private void translate(Node node, double x, double y) {
        node.setTranslateX(x);
        node.setTranslateY(y);
    }
}

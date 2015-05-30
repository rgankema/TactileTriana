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
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.events.EventUtil;

/**
 * The view for a single cable.
 * 
 * CSS class: cable-view
 *
 * @author Richard
 */
public class CableView extends Group {

    @FXML private Line line;
    
    private CableVM viewModel;

    public CableView() {
        ViewLoader.load(this);
    }

    /**
     * Sets the ViewModel of this CableView. It can only be set once.
     *
     * @param viewModel The CableVM that models this view
     */
    public void setViewModel(CableVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel can only be set once");
        }
        if (viewModel == null) {
            return;
        }

        this.viewModel = viewModel;
        
        // Bind color in view to load and broken in viewmodel
        line.strokeProperty().bind(Bindings.createObjectBinding(() -> {
            if (viewModel.isBroken()) {
                return Color.BLACK;
            }

            double load = viewModel.getLoad();
            return Color.DARKGRAY.interpolate(Color.RED, load);
        }, viewModel.loadProperty(), viewModel.brokenProperty()));
        
        // Bind diameter of cables to direction in viewmodel
        DoubleBinding diameterBinding = Bindings.createDoubleBinding(() -> {
            double current = viewModel.getMaximumCurrent();
            // Ik ga er voor het gemak vanuit dat de maximum current zich 1 op 1 verhoudt met de diameter van de kabel
            double diameter = 2 * Math.sqrt(current / Math.PI);
            return diameter;
        }, viewModel.loadProperty());

        line.strokeWidthProperty().bind(diameterBinding.divide(1.5));

        // Repair cable on short press, show on chart for long press
        EventUtil.addShortAndLongPressEventHandler(line, l -> {
            viewModel.pressed();
        }, l -> {
            viewModel.longPressed();
        });
        
        viewModel.shownOnChartProperty().addListener(obs -> {
            if (viewModel.isShownOnChart()) {
                getStyleClass().add("on-chart");
            } else {
                getStyleClass().remove("on-chart");
            }
        });
        
        viewModel.chartIndexProperty().addListener(obs -> { 
            int index = viewModel.getChartIndex();
            if (index == -1) {
                getStyleClass().removeIf(s -> s.startsWith("chart-"));
            } else {
                getStyleClass().add("chart-" + index);
            }
        });
        
        viewModel.brokenProperty().addListener((obs, wasBroken, isBroken) -> { 
            if (isBroken) {
                getStyleClass().add("broken");
            } else {
                getStyleClass().remove("broken");
            }
        });
        
        animate();
    }

    private void animate() {
        
        AnimationTimer timer = new AnimationTimer() {
            
            final List<Circle> onScreen = new ArrayList<>();
            final Stack<Circle> offScreen = new Stack<>();
            final Stack<Circle> toOffScreen = new Stack<>();
            
            long previousTime = -1;
            
            @Override
            public void handle(long currentTime) {
                if (viewModel.getLoad() == 0) {
                    offScreen.addAll(onScreen);
                    getChildren().removeAll(onScreen);
                    onScreen.clear();
                    return;
                }
                if (previousTime == -1 || ((currentTime - previousTime) / 1000000) * viewModel.getLoad() >= 100) {
                    Circle particle;
                    if (!offScreen.empty()) {
                        particle = offScreen.pop();
                        particle.setTranslateX(0);
                        particle.setTranslateY(0);
                    } else {
                        particle = new Circle(line.getStrokeWidth() * 0.3);
                        
                        // Anchor base location to source of flow
                        particle.layoutXProperty().bind(Bindings.createDoubleBinding(() -> { 
                            return (viewModel.getDirection() == CableVM.Direction.END) ? line.getStartX() : line.getEndX();
                        }, viewModel.directionProperty(), line.startXProperty(), line.endXProperty()));
                        particle.layoutYProperty().bind(Bindings.createDoubleBinding(() -> { 
                            return (viewModel.getDirection() == CableVM.Direction.END) ? line.getStartY() : line.getEndY();
                        }, viewModel.directionProperty(), line.startYProperty(), line.endYProperty()));
                        viewModel.directionProperty().addListener((obs, oldV, newV) -> { 
                            particle.setTranslateX(0 - particle.getTranslateX());
                            particle.setTranslateY(0 - particle.getTranslateY());
                        });

                        particle.getStyleClass().add("electricity");
                        particle.setSmooth(false);
                        particle.setCache(true);
                        particle.setCacheHint(CacheHint.SPEED);
                    }
                    getChildren().add(particle);
                    onScreen.add(particle);
                    previousTime = currentTime;
                }
                
                Point2D end = (viewModel.getDirection() == CableVM.Direction.END) ?
                    new Point2D(line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY()) : 
                    new Point2D(line.getStartX() - line.getEndX(), line.getStartY() - line.getEndY());
                Point2D direction = end.normalize();
                
                for (int i = 0; i < onScreen.size(); i++) {
                    Circle particle = onScreen.get(i);
                    
                    double speed = 1.5 + 3.0 * viewModel.getLoad();

                    double x = particle.getTranslateX() + direction.getX() * speed;
                    double y = particle.getTranslateY() + direction.getY() * speed;
                    
                    if (Math.abs(x) > Math.abs(end.getX()) || Math.abs(y) > Math.abs(end.getY())) {
                        getChildren().remove(particle);
                        onScreen.remove(i);
                        offScreen.add(particle);
                        i--;
                        continue;
                    }
                    
                    particle.setTranslateX(x);
                    particle.setTranslateY(y);
                }
            }
        };
        
        timer.start();
    }
    
    // PROPERTIES
    /**
     * The JavaFX Node that defines where the cable starts.
     */
    private final ObjectProperty<Node> startNode = new SimpleObjectProperty<Node>(null) {
        @Override
        public void set(Node value) {
            if (value == get()) {
                return;
            }

            line.startXProperty().unbind();
            line.startYProperty().unbind();

            if (value != null) {
                line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds boundsInParent = value.getBoundsInParent();
                    return boundsInParent.getMinX() + boundsInParent.getWidth() / 2;
                }, value.boundsInParentProperty()));
                line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds boundsInParent = value.getBoundsInParent();
                    return boundsInParent.getMinY() + boundsInParent.getHeight() / 2;
                }, value.boundsInParentProperty()));
            }
            super.set(value);
        }
    };

    public Node getStartNode() {
        return startNode.get();
    }

    public void setStartNode(Node startNode) {
        this.startNode.set(startNode);
    }

    public ObjectProperty<Node> startNodeProperty() {
        return startNode;
    }

    /**
     * The JavaFX Node that defines where the cable ends.
     */
    private final ObjectProperty<Node> endNode = new SimpleObjectProperty<Node>(null) {
        @Override
        public void set(Node value) {
            if (value == get()) {
                return;
            }

            line.endXProperty().unbind();
            line.endYProperty().unbind();

            if (value != null) {
                line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds boundsInParent = value.getBoundsInParent();
                    return boundsInParent.getMinX() + boundsInParent.getWidth() / 2;
                }, value.boundsInParentProperty()));
                line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                    Bounds boundsInParent = value.getBoundsInParent();
                    return boundsInParent.getMinY() + boundsInParent.getHeight() / 2;
                }, value.boundsInParentProperty()));
            }
            super.set(value);
        }
    };

    public Node getEndNode() {
        return endNode.get();
    }

    public void setEndNode(Node endNode) {
        this.endNode.set(endNode);
    }

    public ObjectProperty<Node> endNodeProperty() {
        return endNode;
    }
}

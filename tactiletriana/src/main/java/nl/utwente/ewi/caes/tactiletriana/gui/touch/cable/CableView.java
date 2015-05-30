/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
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

        // Calculates the angle between two points
        DoubleBinding angle = Bindings.createDoubleBinding(() -> {
            double theta = Math.atan2(line.getEndY() - line.getStartY(), line.getEndX() - line.getStartX());
            double a = Math.toDegrees(theta);
            a = (a + 90 + 360) % 360;
            return a;
        }, line.startXProperty(), line.startYProperty(), line.endXProperty(), line.startYProperty());
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
            return Color.DARKGRAY.interpolate(Color.RED, load);//new Color(load, 1.0 - load, 0, 1.0);
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
            
            final List<Circle> circles = new ArrayList<>();
            final List<Circle> removed = new ArrayList<>();
            
            long last = -1;
            
            @Override
            public void handle(long now) {
                if (viewModel.getLoad() == 0) {
                    for (Circle circle : circles) {
                        removed.add(circle);
                        getChildren().remove(circle);
                    }
                    for (Circle circle : removed) {
                        circles.remove(circle);
                    }   
                    return;
                }
                if (last == -1 || ((now - last) / 1000000) * viewModel.getLoad() >= 50) {
                    Circle circle;
                    if (removed.size() > 0) {
                        circle = removed.get(0);
                        circle.setTranslateX(0);
                        circle.setTranslateY(0);
                        removed.remove(0);
                    } else {
                        circle = new Circle(line.getStrokeWidth() * 0.3);
                        circle.layoutXProperty().bind(Bindings.createDoubleBinding(() -> { 
                            return (viewModel.getDirection() == CableVM.Direction.END) ? line.getStartX() : line.getEndX();
                        }, viewModel.directionProperty(), line.startXProperty(), line.endXProperty()));
                        circle.layoutYProperty().bind(Bindings.createDoubleBinding(() -> { 
                            return (viewModel.getDirection() == CableVM.Direction.END) ? line.getStartY() : line.getEndY();
                        }, viewModel.directionProperty(), line.startYProperty(), line.endYProperty()));
                        viewModel.directionProperty().addListener((obs, oldV, newV) -> { 
                            circle.setTranslateX(0 - circle.getTranslateX());
                            circle.setTranslateY(0 - circle.getTranslateY());
                        });

                        circle.getStyleClass().add("electricity");
                        circle.setFill(Color.YELLOW);
                        circle.setSmooth(false);
                    }
                    getChildren().add(circle);
                    circles.add(circle);
                    last = now;
                }
                
                Point2D end = (viewModel.getDirection() == CableVM.Direction.END) ?
                    new Point2D(line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY()) : 
                    new Point2D(line.getStartX() - line.getEndX(), line.getStartY() - line.getEndY());
                Point2D direction = end.normalize();
                
                for (Circle circle : circles) {
                    double speed = 1.5 + 3.0 * viewModel.getLoad();

                    double x = circle.getTranslateX() + direction.getX() * speed;
                    double y = circle.getTranslateY() + direction.getY() * speed;
                    
                    if (Math.abs(x) > Math.abs(end.getX()) || Math.abs(y) > Math.abs(end.getY())) {
                        removed.add(circle);
                        getChildren().remove(circle);
                        continue;
                    }
                    
                    circle.setTranslateX(x);
                    circle.setTranslateY(y);
                }
                
                for (Circle circle : removed) {
                    circles.remove(circle);
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

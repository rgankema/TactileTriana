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
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.events.EventUtil;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class CableView extends Group {

    @FXML
    private Line line;
    @FXML
    private CableDirectionView directionStart;
    @FXML
    private CableDirectionView directionEnd;
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

        directionStart.layoutXProperty().bind(line.startXProperty().subtract(30));
        directionStart.layoutYProperty().bind(line.startYProperty().subtract(30));
        directionStart.rotateProperty().bind(angle.subtract(180));

        directionEnd.layoutXProperty().bind(line.endXProperty().subtract(30));
        directionEnd.layoutYProperty().bind(line.endYProperty().subtract(30));
        directionEnd.rotateProperty().bind(angle);
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
        directionStart.scaleXProperty().bind(diameterBinding.divide(15d));
        directionStart.scaleYProperty().bind(diameterBinding.divide(15d));
        directionEnd.scaleXProperty().bind(diameterBinding.divide(15d));
        directionEnd.scaleYProperty().bind(diameterBinding.divide(15d));

        // Bind visibility of direction views to direction in viewmodel
        directionEnd.setVisible(false);
        directionStart.setVisible(false);
        
        //directionStart.visibleProperty().bind(viewModel.directionProperty().isEqualTo(CableVM.Direction.START));
        //directionEnd.visibleProperty().bind(viewModel.directionProperty().isEqualTo(CableVM.Direction.END));

        // Repair cable on short press, show on chart for long press
        EventUtil.addShortAndLongPressEventHandler(line, l -> {
            viewModel.pressed();
        }, l -> {
            viewModel.longPressed();
        });
        
        viewModel.shownOnChartProperty().addListener(obs -> {
            if (viewModel.isShownOnChart()) {
                line.setEffect(new DropShadow());
            } else {
                line.setEffect(null);
            }
        });
        
        animate();
    }

    private void animate() {
        final List<Circle> circles = new ArrayList<>();
        final List<Circle> removed = new ArrayList<>();
        AnimationTimer timer = new AnimationTimer() {
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
                if (last == -1) {
                    last = now;
                    return;
                } else {
                    if ((now - last) / 1000000 >= (3000 - 2950 * viewModel.getLoad())) {
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
                            
                            circle.setFill(Color.YELLOW);
                            circle.setEffect(new Glow(0.7));
                        }
                        getChildren().add(circle);
                        circles.add(circle);
                        
                        last = now;
                    }
                }
                for (Circle circle : circles) {
                    double speed = 1.5 + 3.0 * viewModel.getLoad();

                    Point2D end;
                    if (viewModel.getDirection() == CableVM.Direction.END) {
                        end = new Point2D(line.getEndX() - line.getStartX(), line.getEndY() - line.getStartY());
                    } else {
                        end = new Point2D(line.getStartX() - line.getEndX(), line.getStartY() - line.getEndY());
                    }
                    Point2D current = new Point2D(circle.getTranslateX(), circle.getTranslateY());

                    Point2D delta = end.subtract(current);
                    if (delta.magnitude() < 2.0) {
                        removed.add(circle);
                        getChildren().remove(circle);
                        continue;
                    }

                    Point2D direction = delta.normalize();

                    circle.setTranslateX(circle.getTranslateX() + direction.getX() * speed);
                    circle.setTranslateY(circle.getTranslateY() + direction.getY() * speed);
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

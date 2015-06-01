/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.events.EventUtil;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 * The view for a single cable.
 * 
 * CSS class: cable-view
 *
 * @author Richard
 */
public class CableView extends Group {

    @FXML protected Line line;
    
    private CableVM viewModel;

    public CableView() {
        ViewLoader.load(this);
    }

    public CableVM getViewModel() {
        return viewModel;
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
        
        // Add CSS class when on chart
        viewModel.shownOnChartProperty().addListener(obs -> {
            if (viewModel.isShownOnChart()) {
                getStyleClass().add("on-chart");
            } else {
                getStyleClass().remove("on-chart");
            }
        });
        
        // Add CSS class for specific chart
        viewModel.chartIndexProperty().addListener(obs -> { 
            int index = viewModel.getChartIndex();
            if (index == -1) {
                getStyleClass().removeIf(s -> s.startsWith("chart-"));
            } else {
                getStyleClass().add("chart-" + index);
            }
        });
        
        // Add CSS class for broken
        viewModel.brokenProperty().addListener((obs, wasBroken, isBroken) -> { 
            if (isBroken) {
                getStyleClass().add("broken");
            } else {
                getStyleClass().remove("broken");
            }
        });
        
        // Animate energy flow
        CableAnimation animation = new CableAnimation(this);
        animation.start();
        
        // Stop animation when Simulation is paused
        Simulation simulation = StageController.getInstance().getSimulation();
        simulation.stateProperty().addListener(obs -> { 
            if (simulation.getState() == Simulation.SimulationState.RUNNING) {
                animation.start();
            } else {
                animation.stop();
            }
        });
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

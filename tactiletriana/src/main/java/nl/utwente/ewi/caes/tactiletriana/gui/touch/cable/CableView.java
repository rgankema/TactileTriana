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
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import nl.utwente.ewi.caes.tactiletriana.App;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class CableView extends Group{
    @FXML private Line line;
    @FXML private DirectionView directionStart;
    @FXML private DirectionView directionEnd;
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
    
    public void setViewModel(CableVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel can only be set once");
        if (viewModel == null) return;
        
        this.viewModel = viewModel;
        // Bind model length to view length
        viewModel.bindCableLength(Bindings.createDoubleBinding(() -> { 
            double a = line.getStartX() - line.getEndX();
            double b = line.getStartY() - line.getEndY();
            return Math.sqrt(a*a + b*b);
        }, line.startXProperty(), line.startYProperty(), line.endXProperty(), line.endYProperty()));
        
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
            // Ik ga er voor het gemak vanuit dat de maximum current zich 1 op 1 verhoudt met de diameter van de kabel, later checken of dat klopt
            double diameter = 2 * Math.sqrt(current / Math.PI);
            return diameter;
        }, viewModel.loadProperty());
        
        /*
        line.strokeWidthProperty().bind(diameterBinding.divide(1.5));
        directionStart.scaleXProperty().bind(diameterBinding.divide(15d));
        directionStart.scaleYProperty().bind(diameterBinding.divide(15d));
        directionEnd.scaleXProperty().bind(diameterBinding.divide(15d));
        directionEnd.scaleYProperty().bind(diameterBinding.divide(15d));
        */
        
        // Bind visibility of direction views to direction in viewmodel
        directionStart.visibleProperty().bind(viewModel.directionProperty().isEqualTo(CableVM.Direction.START));
        directionEnd.visibleProperty().bind(viewModel.directionProperty().isEqualTo(CableVM.Direction.END));
        
        // Handle events for cable
        line.setOnMousePressed(e-> {
            viewModel.cablePressed();
        });
        
        if (App.DEBUG) {
            Label label = new Label();
            label.textProperty().bind(viewModel.debugStringProperty());
            getChildren().add(label);
            label.layoutXProperty().bind(line.endXProperty().add(line.startXProperty()).divide(2d));
            label.layoutYProperty().bind(line.endYProperty().add(line.startYProperty()).divide(2d));
        }
    }
    
    // PROPERTIES
    
    private final ObjectProperty<Node> startNode = new SimpleObjectProperty<Node>(null) {
        @Override
        public void set(Node value) {
            if (value == get()) return;
            
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
    
    private final ObjectProperty<Node> endNode = new SimpleObjectProperty<Node>(null) {
        @Override
        public void set(Node value) {
            if (value == get()) return;
            
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

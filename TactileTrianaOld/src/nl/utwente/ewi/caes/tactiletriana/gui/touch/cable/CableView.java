/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.cable;

import java.io.IOException;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CableView.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load CableView.fxml", e);
        }
        
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
        // Bind load and broken in viewmodel to color in view
        line.strokeProperty().bind(Bindings.createObjectBinding(() -> {
            if (viewModel.isBroken()) {
                return Color.BLACK;
            }
            
            double load = viewModel.getLoad();
            return new Color(load, 1.0 - load, 0, 1.0);
        }, viewModel.loadProperty(), viewModel.brokenProperty()));
        
        // Bind direction in viewmodel to visibility of direction views
        
        directionStart.visibleProperty().bind(viewModel.directionProperty().isEqualTo(CableVM.Direction.START));
        directionEnd.visibleProperty().bind(viewModel.directionProperty().isEqualTo(CableVM.Direction.END));
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

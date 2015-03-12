/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * FXML Controller class
 *
 * @author Richard
 */
public class CableView extends Group{
    @FXML private Line line;
    
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
    }
    
    public void setViewModel(CableVM viewModel) {
        line.strokeProperty().unbind();
        
        this.viewModel = viewModel;
        // Bind load and broken in viewmodel to color in view
        line.strokeProperty().bind(Bindings.createObjectBinding(() -> {
            if (viewModel.isBroken()) {
                return Color.BLACK;
            }
            
            double load = viewModel.getLoad();
            return new Color(load, 1.0 - load, 0, 1.0);
        }, viewModel.loadProperty(), viewModel.brokenProperty()));
    }
    
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
    
    /*
    private final DoubleProperty startX = new SimpleDoubleProperty(0);
    
    public double getStartX() {
        return startX.get();
    }
    
    public void setStartX(double startX) {
        this.startX.set(startX);
    }
    
    public DoubleProperty startXProperty() {
        return startX;
    }
    
    private final DoubleProperty startY = new SimpleDoubleProperty(0);
    
    public double getStartY() {
        return startY.get();
    }
    
    public void setStartY(double startY) {
        this.startY.set(startY);
    }
    
    public DoubleProperty startYProperty() {
        return startY;
    }
    
    private final DoubleProperty endX = new SimpleDoubleProperty(0);
    
    public double getEndX() {
        return endX.get();
    }
    
    public void setEndX(double endX) {
        this.endX.set(endX);
    }
    
    public DoubleProperty endXProperty() {
        return endX;
    }
    
    private final DoubleProperty endY = new SimpleDoubleProperty(0);
    
    public double getEndY() {
        return endY.get();
    }
    
    public void setEndY(double endY) {
        this.endY.set(endY);
    }
    
    public DoubleProperty endYProperty() {
        return endY;
    }
    */
}

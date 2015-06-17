/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.customcontrols;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *
 * @author Richard
 */
public class FloatPane extends Pane {

    // KEYS FOR ATTACHED PROPERTIES
    static final String ALIGNMENT = "float-pane-alignment";
    static final String MARGIN = "float-pane-margin";

    // ATTACHED PROPERTIES
    
    /**
     * Defines where the specified node will be aligned if it is added to this pane.
     */
    public static final ObjectProperty<Pos> alignmentProperty(Node child) {
        ObjectProperty<Pos> property = (ObjectProperty<Pos>) getConstraint(child, ALIGNMENT);
        if (property == null) {
            property = new SimpleObjectProperty<>(null, "alignment", Pos.TOP_LEFT);
            setConstraint(child, ALIGNMENT, property);
        }
        return property;
    }

    public static final Pos getAlignment(Node child) {
        return alignmentProperty(child).get();
    }

    public static final void setAlignment(Node child, Pos alignment) {
        alignmentProperty(child).set(alignment);
    }

    /**
     * Defines the margin that the specified node will have when it is added to this pane.
     */
    public static final ObjectProperty<Insets> marginProperty(Node child) {
        ObjectProperty<Insets> property = (ObjectProperty<Insets>) getConstraint(child, MARGIN);
        if (property == null) {
            property = new SimpleObjectProperty<>(null, "margin", Insets.EMPTY);
            setConstraint(child, MARGIN, property);
        }
        return property;
    }

    public static final Insets getMargin(Node child) {
        return marginProperty(child).get();
    }

    public static final void setMargin(Node child, Insets margin) {
        marginProperty(child).set(margin);
    }

    // STATIC HELPER METHODS
    
    // Used to attach a property to a Node
    static void setConstraint(Node node, Object key, Object value) {
        if (value == null) {
            node.getProperties().remove(key);
        } else {
            node.getProperties().put(key, value);
        }
        if (node.getParent() != null) {
            node.getParent().requestLayout();
        }
    }

    // Used to get an attached property of a Node
    static Object getConstraint(Node node, Object key) {
        if (node.hasProperties()) {
            Object value = node.getProperties().get(key);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    // CONSTRUCTOR
    public FloatPane() {
        getStyleClass().setAll(DEFAULT_STYLE_CLASS);
    }

    public FloatPane(Node... children) {
        this();
        getChildren().addAll(children);
    }

    // LAYOUT
    @Override
    public void layoutChildren() {
        for (Node child : getChildren()) {
            if (!child.isManaged()) {
                continue;
            }

            double areaX = 0;
            double areaY = 0;
            double areaW = getWidth();
            double areaH = getHeight();
            double offset = 0;
            Insets margin = getMargin(child);
            Pos alignment = getAlignment(child);

            layoutInArea(child, areaX, areaY, areaW, areaH, offset, margin,
                    false, false, alignment.getHpos(), alignment.getVpos());
        }
    }

    // STYLESHEET HANDLING
    private static final String DEFAULT_STYLE_CLASS = "tactiletriana-float-pane";
}

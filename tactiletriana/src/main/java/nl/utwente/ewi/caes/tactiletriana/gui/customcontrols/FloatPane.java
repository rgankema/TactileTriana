/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.customcontrols;

import com.sun.javafx.css.converters.EnumConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 *
 * @author Richard
 */
public class FloatPane extends Pane {
    
    static final String ALIGNMENT = "float-pane-alignment";
    static final String MARGIN = "float-pane-margin";
    
    // ATTACHED PROPERTIES
    
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
            if (!child.isManaged()) continue;
            
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
    private static final String DEFAULT_STYLE_CLASS = "customfx-float-pane";
    
    private static class Styleables {
        
        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables
                    = new ArrayList<>(Pane.getClassCssMetaData());
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }
}

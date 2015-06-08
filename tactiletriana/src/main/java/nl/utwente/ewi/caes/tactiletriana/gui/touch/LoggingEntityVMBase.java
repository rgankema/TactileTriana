/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * ViewModel base for views that can be shown on the chart.
 * 
 * @author Richard
 */
public abstract class LoggingEntityVMBase {
    /**
     * Whether the entity is shown on the chart
     */
    private BooleanProperty shownOnChart;
    
    public final boolean isShownOnChart() {
        return shownOnChartProperty().get();
    }
    
    public final void setShownOnChart(boolean value) {
        shownOnChartProperty().set(value);
    }
    
    public BooleanProperty shownOnChartProperty() {
        if (shownOnChart == null) {
            shownOnChart = new SimpleBooleanProperty(false) {
                @Override
                public void set(boolean value) {
                    if (!value) {
                        setChartIndex(-1);
                    }
                    super.set(value);
                }
            };
        }
        return shownOnChart;
    }
    
    /**
     * The index of the chart at which the entity is shown. -1 if not on a chart.
     */
    private IntegerProperty chartIndex;
    
    public final int getChartIndex() {
        return chartIndexProperty().get();
    }
    
    public final void setChartIndex(int index) {
        chartIndexProperty().set(index);
    }
    
    public IntegerProperty chartIndexProperty() {
        if (chartIndex == null) {
            chartIndex = new SimpleIntegerProperty(-1);
        }
        return chartIndex;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;

/**
 * ViewModel base for views that can be shown on the chart.
 * 
 * @author Richard
 */
public abstract class LoggingEntityVMBase {
    /**
     * Whether the device is shown on the chart
     */
    private BooleanProperty shownOnChart;
    
    public boolean isShownOnChart() {
        return shownOnChartProperty().get();
    }
    
    public void setShownOnChart(boolean value) {
        shownOnChartProperty().set(value);
    }
    
    public BooleanProperty shownOnChartProperty() {
        if (shownOnChart == null) {
            shownOnChart = new SimpleBooleanProperty(false);
        }
        return shownOnChart;
    }
}

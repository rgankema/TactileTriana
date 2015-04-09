/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.chart;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class ChartView extends Group {
    @FXML private AreaChart chart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    private ChartVM viewModel;
    
    public ChartView() {
        ViewLoader.load(this);
    }
    
    public void setViewModel(ChartVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel can only be set once");
        
        this.viewModel = viewModel;
        
        
    }
}

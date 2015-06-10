/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer;

import javafx.scene.shape.Rectangle;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.events.EventUtil;

/**
 * The view for the transformer.
 *
 * CSS class: transformer-view
 *
 * @author Richard
 */
public class TransformerView extends Rectangle {

    private TransformerVM viewModel;

    public TransformerView() {
        ViewLoader.load(this);
    }

    public void setViewModel(TransformerVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel may only be set once");
        }

        this.viewModel = viewModel;

        // Show entire network on chart on long press
        EventUtil.addShortAndLongPressEventHandler(this, null, n -> {
            viewModel.longPressed();
        });

        viewModel.shownOnChartProperty().addListener(obs -> {
            if (viewModel.isShownOnChart()) {
                getStyleClass().add("on-chart");
            } else {
                getStyleClass().remove("on-chart");
            }
        });

        viewModel.chartIndexProperty().addListener(obs -> {
            int index = viewModel.getChartIndex();
            if (index == -1) {
                getStyleClass().removeIf(s -> s.startsWith("chart-"));
            } else {
                getStyleClass().add("chart-" + index);
            }
        });
    }
}

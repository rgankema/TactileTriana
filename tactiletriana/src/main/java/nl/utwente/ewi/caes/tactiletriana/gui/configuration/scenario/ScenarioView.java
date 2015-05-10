package nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ListChangeListener.Change;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario.timespan.TimeSpanVM;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.scenario.timespan.TimeSpanView;

public class ScenarioView extends GridPane {
    @FXML private Button addButton;
    @FXML private Button removeButton;
    @FXML private VBox timeSpanContainer;

    private ScenarioVM viewModel;
    
    public ScenarioView() {
        ViewLoader.load(this);
    }
    
    public ScenarioVM getViewModel() {
        return viewModel;
    }
    
    public void setViewModel(ScenarioVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel can only be set once");
        
        this.viewModel = viewModel;
        
        // Add time span when button pressed
        addButton.setOnAction(e -> viewModel.addTimeSpan());
        removeButton.setOnAction(e -> viewModel.removeTimeSpan(viewModel.getTimeSpans().get(viewModel.getTimeSpans().size() - 1)));
        removeButton.disableProperty().bind(viewModel.removeButtonDisableProperty());
        
        // Synchronize time span views with time span view models
        for (TimeSpanVM tsvm : viewModel.getTimeSpans()) {
            TimeSpanView tsv = new TimeSpanView();
            tsv.setViewModel(tsvm);
            timeSpanContainer.getChildren().add(tsv);
        }
        viewModel.getTimeSpans().addListener((Change<? extends TimeSpanVM> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (TimeSpanVM tsvm : c.getAddedSubList()) {
                        TimeSpanView tsv = new TimeSpanView();
                        tsv.setViewModel(tsvm);
                        timeSpanContainer.getChildren().add(tsv);
                    }
                }
                if (c.wasRemoved()) {
                    List<TimeSpanView> toRemove = new ArrayList<>();
                    
                    for (TimeSpanVM tsvm : c.getRemoved()) {
                        for (Node n : timeSpanContainer.getChildren()) {
                            if (((TimeSpanView) n).getViewModel() == tsvm) {
                                toRemove.add((TimeSpanView)n);
                            }
                        }
                    }
                    for (TimeSpanView tsv : toRemove) {
                        timeSpanContainer.getChildren().remove(tsv);
                    }
                }
            }
        });
    } 
}
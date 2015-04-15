/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.house;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class HouseView extends Pane {

    @FXML
    private Rectangle rectangle;

    private HouseVM viewModel;

    public HouseView() {
        ViewLoader.load(this);
    }

    public HouseVM getViewModel() {
        return viewModel;
    }

    public void setViewModel(HouseVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel can only be set once");
        }

        this.viewModel = viewModel;

        // Binds the load and whether the fuse is blown to the border color
        rectangle.strokeProperty().bind(Bindings.createObjectBinding(() -> {
            if (viewModel.isFuseBlown()) {
                return Color.BLACK;
            }

            double load = viewModel.getLoad();
            return Color.DARKGRAY.interpolate(Color.RED, load);
        }, viewModel.loadProperty(), viewModel.fuseBlownProperty()));

        // Repair fuse when house receives touch event
        this.setOnMousePressed(e -> {
            if (!taskScheduled) {
                touchPressTimer = new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            StageController.getInstance().showOnChart(viewModel.getModel());
                            taskScheduled = false;
                        });
                    }
                };
                timer.schedule(touchPressTimer, 1000);
                taskScheduled = true;
            }
            e.consume();
        });
        this.setOnMouseReleased(e -> {
            if (taskScheduled) {
                timer.cancel();
                timer.purge();
                timer = new Timer();
                viewModel.repairFuse();
                taskScheduled = false;
            }
            e.consume();
        });
    }

    private boolean taskScheduled;
    private TimerTask touchPressTimer;
    private Timer timer = new Timer();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.DetailVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.DetailView;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.ConfigurationVM;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.ConfigurationView;
import nl.utwente.ewi.caes.tactiletriana.gui.configuration.ScreenIndexView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.TouchVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.TouchView;
import nl.utwente.ewi.caes.tactiletriana.simprediction.SimulationPrediction;
import nl.utwente.ewi.caes.tactiletriana.simulation.LoggingEntity;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public final class StageController {
    // STATIC FIELDS

    private static StageController instance;

    // STATIC METHODS
    public static StageController getInstance() {
        return instance;
    }

    public static void initialize(Stage primaryStage) {
        if (instance == null) {
            instance = new StageController(primaryStage);
        }
    }

    // INSTANCE FIELDS
    private final Stage launcherStage;
    private Stage touchStage;
    private Stage detailStage;
    private final List<Stage> screenIndexStages;

    private final ConfigurationVM configurationVM;
    private TouchVM touchVM;
    private DetailVM detailVM;
    private List<Stage> screenIndexWindows;

    private ConfigurationVM launcherVM;
    private Simulation simulation;
    private SimulationPrediction simulationprediction;
    // CONSTRUCTOR

    private StageController(Stage configurationStage) {
        // Build launcher stage
        this.launcherStage = configurationStage;
        this.simulation = new Simulation();
        this.simulationprediction = new SimulationPrediction(simulation);

        ConfigurationView lv = new ConfigurationView();
        configurationVM = new ConfigurationVM(this.simulation);
        lv.setViewModel(configurationVM);

        configurationStage.setScene(new Scene(lv));
        configurationStage.setOnCloseRequest(e -> closeAllStages());
        configurationStage.getIcons().add(new Image("images/triana.png"));
        configurationStage.setTitle("TactileTriana");

        // Build screen index stages
        screenIndexStages = new ArrayList<>();
        for (Integer i : configurationVM.getScreenIndexList()) {
            Screen screen = configurationVM.getScreenByIndex(i);

            Scene scene = new Scene(new ScreenIndexView(i));
            Stage stage = new Stage(StageStyle.TRANSPARENT);

            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.setX(screen.getVisualBounds().getMinX());
            stage.setY(screen.getVisualBounds().getMinY());
            screenIndexStages.add(stage);

        }
    }

    // METHODS
    public void setLauncherStageVisible(boolean visible) {
        if (visible) {
            launcherStage.show();
        } else {
            launcherStage.hide();
        }
    }

    public void setMainStagesVisible(boolean visible) {
        if (touchStage == null) {
            // Build touch screen stage
            touchStage = new Stage();

            touchVM = new TouchVM(simulation);
            TouchView tv = new TouchView(this.simulation);
            tv.setViewModel(touchVM);

            Scene touchScene = new Scene(tv);
            touchScene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) {
                    simulation.pause();
                    setLauncherStageVisible(true);
                }
            });

            touchStage.setScene(touchScene);
            touchStage.setOnCloseRequest(e -> closeAllStages());

            if (configurationVM.fullScreenCheckedProperty().get()) {
                Screen touchScreen = configurationVM.getScreenByIndex((Integer) configurationVM.touchScreenSelectionProperty().get());

                touchStage.setX(touchScreen.getVisualBounds().getMinX());
                touchStage.setY(touchScreen.getVisualBounds().getMinY());

                touchStage.initStyle(StageStyle.UNDECORATED);
            }

            // Build detail screen stage
            detailStage = new Stage();

            detailVM = new DetailVM(simulation);

            DetailView dv = new DetailView();
            dv.setViewModel(detailVM);

            detailStage.setScene(new Scene(dv));
            detailStage.setOnCloseRequest(e -> closeAllStages());

            if (configurationVM.fullScreenCheckedProperty().get()) {
                Screen detailScreen = configurationVM.getScreenByIndex((Integer) configurationVM.detailScreenSelectionProperty().get());

                detailStage.setX(detailScreen.getVisualBounds().getMinX());
                detailStage.setY(detailScreen.getVisualBounds().getMinY());

                detailStage.initStyle(StageStyle.UNDECORATED);
            }
        }

        if (visible) {
            detailStage.show();
            touchStage.show();
        } else {
            touchStage.hide();
            detailStage.hide();
        }
    }

    public void setScreenIndexStagesVisible(boolean visible) {
        for (Stage stage : screenIndexStages) {
            if (visible) {
                stage.show();
            } else {
                stage.hide();
            }
        }
    }

    public void closeAllStages() {
        launcherStage.close();
        for (Stage stage : screenIndexStages) {
            stage.close();
        }
        if (touchStage != null) {
            touchStage.close();
            detailStage.close();
        }
    }

    public Simulation getSimulation() {
        return this.simulation;
    }

    public void showOnChart(LoggingEntity entity) {
        detailVM.getChartVM().setEntity(entity);
    }
}

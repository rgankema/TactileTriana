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
        if (instance == null ) {
            instance = new StageController(primaryStage);
        }
    }
    
    // INSTANCE FIELDS
    
    private final Stage launcherStage;
    private Stage touchStage;
    private Stage detailStage;
    private List<Stage> screenIndexWindows;
    
    private ConfigurationVM launcherVM;
    
    // CONSTRUCTOR
    
    private StageController(Stage launcherStage) {
        // Build launcher stage
        this.launcherStage = launcherStage;
        
        ConfigurationView lv = new ConfigurationView();
        launcherVM = new ConfigurationVM();
        lv.setViewModel(launcherVM);
        
        launcherStage.setScene(new Scene(lv));
        launcherStage.setOnCloseRequest(e -> closeAllStages());
        launcherStage.getIcons().add(new Image("images/triana.png"));
        launcherStage.setTitle("TactileTriana");
        
        // Build screen index stages
        screenIndexWindows = new ArrayList<>();
        for (Integer i : launcherVM.getScreenIndexList()) {
            Screen screen = launcherVM.getScreenByIndex(i);
            
            Scene scene = new Scene(new ScreenIndexView(i));
            Stage stage = new Stage(StageStyle.TRANSPARENT);
            
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.setX(screen.getVisualBounds().getMinX());
            stage.setY(screen.getVisualBounds().getMinY());
            
            screenIndexWindows.add(stage);
        }
    }
    
    // METHODS
    
    public void setLauncherStageVisible(boolean visible) {
        if (visible)
            launcherStage.show();
        else
            launcherStage.hide();
    }
    
    public void setMainStagesVisible(boolean visible) {
        if (touchStage == null) {
            // Build touch screen stage
            touchStage = new Stage();

            TouchVM tvm = new TouchVM(Simulation.getInstance());
            TouchView tv = new TouchView();
            tv.setViewModel(tvm);

            Scene touchScene = new Scene(tv);
            touchScene.setOnKeyPressed(e -> { 
                if (e.getCode() == KeyCode.ESCAPE) {
                    Simulation.getInstance().pause();
                    setLauncherStageVisible(true);
                }
            });
            
            touchStage.setScene(touchScene);
            touchStage.setOnCloseRequest(e -> closeAllStages());
            
            if (launcherVM.fullScreenCheckedProperty().get()) {
                Screen touchScreen = launcherVM.getScreenByIndex((Integer)launcherVM.touchScreenSelectionProperty().get());
                touchStage.setX(touchScreen.getVisualBounds().getMinX());
                touchStage.setY(touchScreen.getVisualBounds().getMinY());
                
                touchStage.initStyle(StageStyle.UNDECORATED);
            }
            
            // Build detail screen stage
            detailStage = new Stage();
        
            DetailVM dvm = new DetailVM(Simulation.getInstance());
            DetailView dv = new DetailView();
            dv.setViewModel(dvm);

            detailStage.setScene(new Scene(dv));
            detailStage.setOnCloseRequest(e -> closeAllStages());
            
            if (launcherVM.fullScreenCheckedProperty().get()) {
                Screen detailScreen = launcherVM.getScreenByIndex((Integer)launcherVM.detailScreenSelectionProperty().get());
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
        for (Stage stage : screenIndexWindows) {
            if (visible)
                stage.show();
            else
                stage.hide();
        }
    }
    
    public void closeAllStages() {
        launcherStage.close();
        for (Stage stage : screenIndexWindows) {
            stage.close();
        }
        if (touchStage != null) {
            touchStage.close();
            detailStage.close();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class App extends Application {
    public static final boolean DEBUG = false;
    
    @Override
    public void start(Stage stage) throws Exception {
        StageController.initialize(stage);
        StageController.getInstance().setLauncherStageVisible(true);
        StageController.getInstance().setScreenIndexStagesVisible(true);
    }
    
    @Override
    public void stop() {
        Simulation.getInstance().stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

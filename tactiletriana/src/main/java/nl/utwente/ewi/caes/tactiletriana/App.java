/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import javafx.application.Application;
import javafx.stage.Stage;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;

/**
 *
 * @author Richard
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load settings file
        TrianaSettings.load(TrianaSettings.DEFAULT_FILE);
        
        StageController.initialize(stage);
        StageController.getInstance().setLauncherStageVisible(true);
        StageController.getInstance().setScreenIndexStagesVisible(true);
    }

    @Override
    public void stop() {
        Concurrent.getExecutorService().shutdownNow();
    }

    public static void main(String[] args) {
        launch(args);
    }

}

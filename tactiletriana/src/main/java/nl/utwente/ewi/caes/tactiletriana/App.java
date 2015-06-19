/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.stage.Stage;
import nl.utwente.ewi.caes.tactiletriana.gui.StageController;
import nl.utwente.ewi.caes.tactiletriana.simulation.data.UncontrollableData;

/**
 *
 * @author Richard
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //File file = new File(getClass().getResource("/datasets/house_profiles.csv").getPath());
        //System.out.println("All good");
        //Map<String, String> env = new HashMap<>();
        //env.put("create", "true");
        //FileSystem zipfs = FileSystems.
        // Load settings file
        GlobalSettings.load(GlobalSettings.DEFAULT_FILE);
        
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

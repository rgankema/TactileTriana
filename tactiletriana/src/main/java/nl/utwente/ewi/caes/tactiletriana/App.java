/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.TouchVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.TouchView;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class App extends Application {
    public static final boolean DEBUG = false;
    private Simulation simulation;
    
    @Override
    public void start(Stage stage) throws Exception {
        simulation = Simulation.getInstance();
        TouchVM tvm = new TouchVM(simulation);
        TouchView tv = new TouchView();
        tv.setViewModel(tvm);
        
        Scene scene = new Scene(tv);
        
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
        
        simulation.start();
    }
    
    @Override
    public void stop() {
        simulation.stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

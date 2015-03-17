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
    
    @Override
    public void start(Stage stage) throws Exception {
        TouchVM tvm = new TouchVM(Simulation.getInstance());
        TouchView tv = new TouchView();
        tv.setViewModel(tvm);
        
        Scene scene = new Scene(tv);
        
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
        
        Simulation.getInstance().start();
    }
    
    @Override
    public void stop() {
        Simulation.getInstance().stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

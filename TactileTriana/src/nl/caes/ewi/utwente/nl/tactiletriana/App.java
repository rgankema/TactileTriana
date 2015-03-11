/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.TouchView;

/**
 *
 * @author Richard
 */
public class App extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        TouchView tv = new TouchView();
        
        Scene scene = new Scene(tv);
        
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

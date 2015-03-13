/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.utwente.ewi.caes.tactiletriana.gui.test.TestView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.TouchView;

/**
 *
 * @author Richard
 */
public class App extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //TouchView tv = new TouchView();
        TestView tv = new TestView();
        
        Scene scene = new Scene(tv);
        
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

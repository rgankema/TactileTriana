/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana_gui_test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.utwente.ewi.caes.tactiletriana_gui_test.gui.MainView;

/**
 *
 * @author Richard
 */
public class App extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        MainView tv = new MainView();
        
        Scene scene = new Scene(tv);
        
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

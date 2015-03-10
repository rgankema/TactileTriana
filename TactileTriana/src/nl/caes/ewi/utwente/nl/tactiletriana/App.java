/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.TouchPresenter;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.TouchViewFactory;

/**
 *
 * @author Richard
 */
public class App extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        TouchViewFactory factory = new TouchViewFactory();
        TouchPresenter presenter = factory.getTouchPresenter(null);
        
        Scene scene = new Scene(presenter.getView());
        
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

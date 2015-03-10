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
import nl.caes.ewi.utwente.nl.tactiletriana.gui.mock.MockNode;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.TouchPresenter;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.TouchPresenterFactory;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node.NodePresenter;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node.NodePresenterFactory;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node.NodeVM;

/**
 *
 * @author Richard
 */
public class App extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        TouchPresenterFactory factory = new TouchPresenterFactory();
        TouchPresenter presenter = factory.buildTouchPresenter(null);
        
        NodePresenterFactory f2 = new NodePresenterFactory();
        
        presenter.getView().getChildren().add(f2.buildNodePresenter(new NodeVM(new MockNode())).getView());
 
        Scene scene = new Scene(presenter.getView());
        
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}

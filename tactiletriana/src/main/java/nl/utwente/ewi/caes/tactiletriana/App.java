/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.DetailVM;
import nl.utwente.ewi.caes.tactiletriana.gui.detail.DetailView;
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
    public void start(Stage touchStage) throws Exception {
        simulation = Simulation.getInstance();
        
        // Touch scherm bouwen
        TouchVM tvm = new TouchVM(simulation);
        TouchView tv = new TouchView();
        tv.setViewModel(tvm);
        
        Scene touchScene = new Scene(tv);
        touchScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F) {
                touchStage.setFullScreen(!touchStage.isFullScreen());
            }
        });
        
        touchStage.setScene(touchScene);
        touchStage.show();
        
        // Detail scherm bouwen
        Stage detailStage = new Stage();
        
        DetailVM dvm = new DetailVM(simulation);
        DetailView dv = new DetailView();
        dv.setViewModel(dvm);
        
        Scene detailScene = new Scene(dv);
        detailScene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F) {
                detailStage.setFullScreen(!detailStage.isFullScreen());
            }
        });
        
        detailStage.setScene(detailScene);
        detailStage.show();
        
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

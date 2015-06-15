/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.background;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import nl.utwente.ewi.caes.tactiletriana.GlobalSettings;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.background.BackgroundVM.Season;

/**
 *
 * @author Richard
 */
public class BackgroundView extends StackPane {
    private static final Image BG_SPRING_DAY = new Image("images/background-spring.jpg");
    private static final Image BG_SPRING_NIGHT = new Image("images/background-spring-night.jpg");
    private static final Image BG_SUMMER_DAY = new Image("images/background-summer.jpg");
    private static final Image BG_SUMMER_NIGHT = new Image("images/background-summer-night.jpg");
    private static final Image BG_AUTUMN_DAY = new Image("images/background-fall.jpg");
    private static final Image BG_AUTUMN_NIGHT = new Image("images/background-fall-night.jpg");
    private static final Image BG_WINTER_DAY = new Image("images/background-winter.jpg");
    private static final Image BG_WINTER_NIGHT = new Image("images/background-winter-night.jpg");
    
    private BackgroundVM viewModel;
    
    @FXML private ImageView imageDay;
    @FXML private ImageView imageNight;
    
    public BackgroundView() {
        ViewLoader.load(this);
        
        imageDay.fitWidthProperty().bind(this.widthProperty());
        imageDay.fitHeightProperty().bind(this.heightProperty());
        imageDay.setPreserveRatio(false);
        
        imageNight.fitWidthProperty().bind(this.widthProperty());
        imageNight.fitHeightProperty().bind(this.heightProperty());
        imageNight.setPreserveRatio(false);
    }
    
    public void setViewModel(BackgroundVM viewModel) {
        if (this.viewModel != null) throw new IllegalStateException("ViewModel already set");
            
        imageDay.opacityProperty().bind(viewModel.darknessFactorProperty().negate().add(1));
        imageNight.opacityProperty().bind(viewModel.darknessFactorProperty());
        
        viewModel.seasonProperty().addListener(obs -> { 
            // Temporarily replace day background with old night background
            ImageView temp = new ImageView(imageNight.getImage());
            temp.fitWidthProperty().bind(this.widthProperty());
            temp.fitHeightProperty().bind(this.heightProperty());
            getChildren().add(temp);
            
            // Fade out old night background
            FadeTransition fade = new FadeTransition(Duration.millis(GlobalSettings.SYSTEM_TICK_TIME * (120 / GlobalSettings.TICK_MINUTES)), temp);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            fade.setOnFinished(e -> { 
                getChildren().remove(temp);
            });
            fade.playFromStart();
            
            // Find the proper image
            Season season = viewModel.getSeason();
            Image day = null, night = null;
            if (season == Season.SPRING) {
                day = BG_SPRING_DAY;
                night = BG_SPRING_NIGHT;
            } else if (season == Season.SUMMER) {
                day = BG_SUMMER_DAY;
                night = BG_SUMMER_NIGHT;
            } else if (season == Season.AUTUMN) {
                day = BG_AUTUMN_DAY;
                night = BG_AUTUMN_NIGHT;
            } else if (season == Season.WINTER) {
                day = BG_WINTER_DAY;
                night = BG_WINTER_NIGHT;
            }
            imageDay.setImage(day);
            imageNight.setImage(night);
        });
    }
}

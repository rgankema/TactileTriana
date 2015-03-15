/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana_gui_test.gui.config;

import java.io.IOException;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.converter.DoubleStringConverter;

/**
 *
 * @author Richard
 */
public class ConfigView extends GridPane {
    @FXML private Slider nodeVoltageSlider;
    @FXML private Slider cableCurrentSlider;
    @FXML private Slider cableMaxCurrentSlider;
    @FXML private CheckBox cableBrokenCheckBox;
    @FXML private Slider houseConsumptionSlider;
    @FXML private Slider houseMaxConsumptionSlider;
    @FXML private CheckBox houseFuseBlownCheckBox;
    
    public ConfigView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConfigView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        
        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load ConfigView.fxml", e);
        }
        
        nodeVoltage.bind(nodeVoltageSlider.valueProperty());
        cableCurrent.bind(cableCurrentSlider.valueProperty());
        cableMaxCurrent.bind(cableMaxCurrentSlider.valueProperty());
        cableBroken.bind(cableBrokenCheckBox.selectedProperty());
        houseConsumption.bind(houseConsumptionSlider.valueProperty());
        houseMaxConsumption.bind(houseMaxConsumptionSlider.valueProperty());
        houseFuseBlown.bind(houseFuseBlownCheckBox.selectedProperty());
    }
    
    public final DoubleProperty nodeVoltage = new SimpleDoubleProperty();
    public final DoubleProperty cableCurrent = new SimpleDoubleProperty();
    public final DoubleProperty cableMaxCurrent = new SimpleDoubleProperty();
    public final BooleanProperty cableBroken = new SimpleBooleanProperty();
    public final DoubleProperty houseConsumption = new SimpleDoubleProperty();
    public final DoubleProperty houseMaxConsumption = new SimpleDoubleProperty();
    public final BooleanProperty houseFuseBlown = new SimpleBooleanProperty();
}

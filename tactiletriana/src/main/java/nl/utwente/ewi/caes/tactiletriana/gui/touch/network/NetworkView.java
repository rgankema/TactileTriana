/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer.TransformerView;

/**
 *
 * @author Richard
 */
public class NetworkView extends Pane {
    @FXML private TransformerView t;
    
    @FXML private HouseView h1;
    @FXML private HouseView h2;
    @FXML private HouseView h3;
    @FXML private HouseView h4;
    @FXML private HouseView h5;
    @FXML private HouseView h6;
    
    @FXML private NodeView n1;
    @FXML private NodeView n2;
    @FXML private NodeView n3;
    @FXML private NodeView n4;
    @FXML private NodeView n5;
    @FXML private NodeView n6;
    
    @FXML private NodeView nh1;
    @FXML private NodeView nh2;
    @FXML private NodeView nh3;
    @FXML private NodeView nh4;
    @FXML private NodeView nh5;
    @FXML private NodeView nh6;
    
    @FXML private CableView c1;
    @FXML private CableView c2;
    @FXML private CableView c3;
    @FXML private CableView c4;
    @FXML private CableView c5;
    @FXML private CableView c6;
    
    @FXML private CableView ch1;
    @FXML private CableView ch2;
    @FXML private CableView ch3;
    @FXML private CableView ch4;
    @FXML private CableView ch5;
    @FXML private CableView ch6;
 
    
    public NetworkView() {
        ViewLoader.load(this);
        
        // Move all cables to the background (they're just in the foreground at first because that works better with scene builder)
        List<Node> cables = new ArrayList<>();
        getChildren().stream().filter((node) -> (node instanceof CableView)).forEach(cables::add);
        cables.stream().forEach(Node::toBack);
    }
    
    public List<HouseView> getHouseViews() {
        List<HouseView> result = new ArrayList<>();
        getChildren().stream().filter(node -> node instanceof HouseView).forEach(node -> result.add((HouseView) node));
        return result;
    }
    
    public TransformerView getTransformer(){
        return t;
    }
    
    public HouseView[] getHouses(){
        return new HouseView[] {h1, h2, h3, h4, h5, h6};
    }
    
    public CableView[] getInternalCables(){
        return new CableView[] {c1, c2, c3, c4, c5, c6};
    }
    public CableView[] getHouseCables(){
        return new CableView[] {ch1, ch2, ch3, ch4, ch5, ch6};
    }
    
    public NodeView[] getInternalNodes(){
        return new NodeView[] {n1, n2, n3, n4,n5, n6};
    }
    public NodeView[] getHouseNodes(){
        return new NodeView[] {nh1, nh2, nh3, nh4, nh5, nh6};
    }
}

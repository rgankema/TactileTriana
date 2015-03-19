/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch.network;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.transformer.TransformerView;

/**
 * Pane that lays out all (static) views that form the network. Has methods for
 * retrieving the various views so that their ViewModels can be set.
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
    
    @FXML private NodeView ni1;
    @FXML private NodeView ni2;
    @FXML private NodeView ni3;
    @FXML private NodeView ni4;
    @FXML private NodeView ni5;
    @FXML private NodeView ni6;
    
    @FXML private NodeView nh1;
    @FXML private NodeView nh2;
    @FXML private NodeView nh3;
    @FXML private NodeView nh4;
    @FXML private NodeView nh5;
    @FXML private NodeView nh6;
    
    @FXML private CableView t_ni1;
    @FXML private CableView ni1_ni2;
    @FXML private CableView ni2_ni3;
    @FXML private CableView ni3_ni4;
    @FXML private CableView ni4_ni5;
    @FXML private CableView ni5_ni6;
    
    @FXML private CableView ni1_nh1;
    @FXML private CableView ni2_nh2;
    @FXML private CableView ni3_nh3;
    @FXML private CableView ni4_nh4;
    @FXML private CableView ni5_nh5;
    @FXML private CableView ni6_nh6;
    
    public NetworkView() {
        ViewLoader.load(this);
        
        // Move all cables to the background (they're just in the foreground at first because that works better with scene builder)
        List<Node> cables = new ArrayList<>();
        getChildren().stream().filter((node) -> (node instanceof CableView)).forEach(cables::add);
        cables.stream().forEach(Node::toBack);
    }
    
    public TransformerView getTransformer(){
        return t;
    }
    
    public HouseView[] getHouses(){
        return new HouseView[] {h1, h2, h3, h4, h5, h6};
    }
    
    public CableView[] getInternalCables(){
        return new CableView[] {t_ni1, ni1_ni2, ni2_ni3, ni3_ni4, ni4_ni5, ni5_ni6};
    }
    
    public CableView[] getHouseCables(){
        return new CableView[] {ni1_nh1, ni2_nh2, ni3_nh3, ni4_nh4, ni5_nh5, ni6_nh6};
    }
    
    public NodeView[] getInternalNodes(){
        return new NodeView[] {ni1, ni2, ni3, ni4,ni5, ni6};
    }
    
    public NodeView[] getHouseNodes(){
        return new NodeView[] {nh1, nh2, nh3, nh4, nh5, nh6};
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana_gui_test.gui.test;

import javafx.beans.property.SimpleDoubleProperty;
import nl.utwente.ewi.caes.tactilefx.control.TactilePane;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import nl.utwente.ewi.caes.tactiletriana_gui_test.gui.config.ConfigView;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class TestView extends TactilePane {
    
    public TestView(ConfigView configView) {
        setPrefWidth(600d);
        
        // Mock models
        
        Node mockedNode1 = mock(Node.class);
        when(mockedNode1.voltageProperty()).thenReturn(configView.nodeVoltage);
        
        Node mockedNode2 = mock(Node.class);
        when(mockedNode2.voltageProperty()).thenReturn(configView.nodeVoltage);
        
        Cable mockedCable = mock(Cable.class);
        when(mockedCable.currentProperty()).thenReturn(configView.cableCurrent);
        when(mockedCable.maximumCurrentProperty()).thenReturn(configView.cableMaxCurrent);
        when(mockedCable.brokenProperty()).thenReturn(configView.cableBroken);
        when(mockedCable.lengthProperty()).thenReturn(new SimpleDoubleProperty());
        
        House mockedHouse = mock(House.class);
        when(mockedHouse.currentConsumptionProperty()).thenReturn(configView.houseConsumption);
        when(mockedHouse.maximumConsumptionProperty()).thenReturn(configView.houseMaxConsumption);
        when(mockedHouse.fuseBlownProperty()).thenReturn(configView.houseFuseBlown);
        
        // Build view
        
        NodeView nv1 = new NodeView();
        nv1.setViewModel(new NodeVM(mockedNode1));
        nv1.relocate(50, 50);
        
        NodeView nv2 = new NodeView();
        nv2.setViewModel(new NodeVM(mockedNode2));
        nv2.relocate(450, 50);
        
        CableView cv = new CableView();
        cv.setViewModel(new CableVM(mockedCable));
        cv.setStartNode(nv1);
        cv.setEndNode(nv2);
        
        HouseView hv = new HouseView();
        hv.setViewModel(new HouseVM(mockedHouse));
        hv.relocate(50, 200);
        
        getChildren().addAll(cv, nv1, nv2, hv);
    }
}

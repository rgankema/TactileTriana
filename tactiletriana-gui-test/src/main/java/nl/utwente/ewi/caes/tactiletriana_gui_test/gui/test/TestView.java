/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana_gui_test.gui.test;

import nl.utwente.cs.caes.tactile.control.TactilePane;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableView;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.ewi.caes.tactiletriana.simulation.CableBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.NodeBase;
import nl.utwente.ewi.caes.tactiletriana_gui_test.gui.config.ConfigView;
import static org.mockito.Mockito.*;

/**
 *
 * @author Richard
 */
public class TestView extends TactilePane {
    
    public TestView(ConfigView configView) {
        setPrefWidth(600);
        
        // Mock models
        
        NodeBase mockedNode1 = mock(NodeBase.class);
        when(mockedNode1.voltageProperty()).thenReturn(configView.nodeVoltage);
        
        NodeBase mockedNode2 = mock(NodeBase.class);
        when(mockedNode2.voltageProperty()).thenReturn(configView.nodeVoltage);
        
        CableBase mockedCable = mock(CableBase.class);
        when(mockedCable.currentProperty()).thenReturn(configView.cableCurrent);
        when(mockedCable.maximumCurrentProperty()).thenReturn(configView.cableMaxCurrent);
        when(mockedCable.brokenProperty()).thenReturn(configView.cableBroken);
        
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
        
        getChildren().addAll(cv, nv1, nv2);
    }
}
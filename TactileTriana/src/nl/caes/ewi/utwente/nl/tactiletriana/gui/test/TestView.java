/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.caes.ewi.utwente.nl.tactiletriana.gui.test;

import javafx.scene.layout.Pane;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.test.mock.SweepMockCable;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.test.mock.MockNode;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.test.mock.SimpleMockCable;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable.CableVM;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.cable.CableView;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node.NodeVM;
import nl.caes.ewi.utwente.nl.tactiletriana.gui.touch.node.NodeView;
import nl.utwente.cs.caes.tactile.control.TactilePane;

/**
 *
 * @author Richard
 */
public class TestView extends TactilePane {
    public TestView() {
        NodeView nv1 = new NodeView();
        NodeView nv2 = new NodeView();
        CableView cv1 = new CableView();
        cv1.setStartNode(nv1);
        cv1.setEndNode(nv2);
        
        NodeView nv3 = new NodeView();
        NodeView nv4 = new NodeView();
        CableView cv2 = new CableView();
        cv2.setStartNode(nv3);
        cv2.setEndNode(nv4);
        
        nv1.relocate(50, 450);
        nv2.relocate(450, 550);
        nv3.relocate(50, 500);
        nv4.relocate(250, 600);
        
        nv1.setViewModel(new NodeVM(new MockNode()));
        nv2.setViewModel(new NodeVM(new MockNode()));
        cv1.setViewModel(new CableVM(new SweepMockCable()));
        cv2.setViewModel(new CableVM(new SimpleMockCable()));
        
        getChildren().addAll(cv1, cv2, nv2, nv4);//, nv1, nv2, nv3, nv4);//, nv1, nv2);
    }
}

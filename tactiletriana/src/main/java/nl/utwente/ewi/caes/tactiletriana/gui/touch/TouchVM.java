/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.touch;

import nl.utwente.ewi.caes.tactiletriana.gui.touch.cable.CableVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.house.HouseVM;
import nl.utwente.ewi.caes.tactiletriana.gui.touch.node.NodeVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.Cable;
import nl.utwente.ewi.caes.tactiletriana.simulation.Node;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class TouchVM {
    private final Simulation model;
    
    private final NodeVM internalNodes[];
    private final NodeVM houseNodes[];
    private final CableVM internalCables[];
    private final CableVM houseCables[];
    private final HouseVM houses[];
    
    public TouchVM(Simulation model) {
        this.model = model;
        
        this.internalNodes = new NodeVM[6];
        this.houseNodes = new NodeVM[6];
        this.internalCables = new CableVM[6];
        this.houseCables = new CableVM[6];
        this.houses = new HouseVM[6];
        
        internalCables[0] = new CableVM(model.getTransformer().getCables().get(0));
        Node node = model.getTransformer().getCables().get(0).getChildNode();
        for (int i = 0; i < 6; i++) {
            internalNodes[i] = new NodeVM(node);
            for (Cable cable: node.getCables()) {
                Node childNode = cable.getChildNode();
                if (childNode.getHouse() != null) {
                    houseNodes[i] = new NodeVM(childNode);
                    houseCables[i] = new CableVM(cable);
                    houses[i] = new HouseVM(childNode.getHouse());
                } else {
                    if (i < 5) {
                        internalCables[i + 1] = new CableVM(cable);
                    }
                    node = childNode;
                }
            }
        }
    }
    
    public NodeVM[] getInternalNodes() {
        return internalNodes;
    }
    
    public NodeVM[] getHouseNodes() {
        return houseNodes;
    }

    public CableVM[] getInternalCables() {
        return internalCables;
    }

    public CableVM[] getHouseCables() {
        return houseCables;
    }

    public HouseVM[] getHouses() {
        return houses;
    }
    
    
}

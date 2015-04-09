/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail;

import nl.utwente.ewi.caes.tactiletriana.gui.detail.datetime.DateTimeVM;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;

/**
 *
 * @author Richard
 */
public class DetailVM {
    private Simulation simulation;
    private DateTimeVM dateTimeVM;
    
    public DetailVM(Simulation simulation) {
        this.simulation = simulation;
        
        dateTimeVM = new DateTimeVM(simulation);
    }
    
    public DateTimeVM getDateTimeVM() {
        return dateTimeVM;
    }
}

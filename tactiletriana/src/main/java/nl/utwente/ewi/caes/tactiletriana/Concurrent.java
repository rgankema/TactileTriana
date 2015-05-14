/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Holds anything related to multi-threading
 * 
 * @author Richard
 */
public class Concurrent {
    private final static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);
    
    public static ScheduledExecutorService getExecutorService() {
        return executorService;
    }
}

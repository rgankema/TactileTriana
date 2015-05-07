/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.api;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;


/**
 *
 * @author jd
 */
public class APIServer implements Runnable {
    
    protected int serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected Simulation simulation = null;
    protected boolean isStopped    = false;
    private ServerConnection controlConnection = null;
    private ArrayList<ServerConnection> serverConnections = null;
    
    private final Object controllerLock = new Object();
    
    public APIServer(int port, Simulation sim) {
        this.serverPort = port;
        this.simulation = sim;
        this.serverConnections = new ArrayList<ServerConnection>();
    }
    
    /**
     * Main loop of the server
     */
    @Override
    public void run() {
        
        //Create a new server socket
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
        
        //Main server loop
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            ServerConnection sc = new ServerConnection(clientSocket, this);
            addConnection(sc);
            
            new Thread(sc).start();
            
        }
        System.out.println("Server Stopped.") ;
    }
    
    /** Check if the server is still running
     * 
     * @return 
     */
    private boolean isStopped() {
        return this.isStopped;
    }
    
    /**
     * Synchronized method for adding a ServerConnection to the list of active connections.
     * 
     * @param s 
     */
    public synchronized void addConnection(ServerConnection s) {
        serverConnections.add(s);
    }    
    
    /**
     * Synchronized method for removing a ServerConnection from the list of active ServerConnections
     * 
     * @param s 
     */
    public synchronized void removeConnection(ServerConnection s) {
        serverConnections.remove(s);
    }
    
    /**
     * Get the active server connections
     * TODO make the returned list immutable
     * 
     * @return list of ServerConnections
     */
    public ArrayList<ServerConnection> getConnections() {
        return serverConnections;
    }
    
    /**
     * Stop the server
     */
    //TODO test this method
    public void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
        
        //Close all ServerConnections
        for(ServerConnection sc : serverConnections) {
            sc.shutdown();
        }
    }
    
    /**
     * Request for a ServerConnection to become the controller of the Simulation
     * 
     * @param s
     * @return 
     */
    public boolean requestControl(ServerConnection s) {
        boolean result = false;
        synchronized(controllerLock) {
            if(controlConnection == null || !controlConnection.isRunning() ) {
                controlConnection = s;
                result = true;
            }
        }
        return result;
        
    }
    
    /**
     * Unregisters a ServerConnection as the controller. Only works if the given ServerConnection actually was the controller.
     * 
     * @param s ServerConnection that is the current controller of the simulation
     */
    public void releaseControl(ServerConnection s) {
        synchronized(controllerLock) {
            if(controlConnection == s) {
                controlConnection = null;
            }
        }
    }
    
    public Simulation getSimulation() {
        
        
        
        return this.simulation;
    }
    
    
    public static void main(String args[]) {
        System.out.println("Starting server...");
        APIServer s = new APIServer(8070, new Simulation());
        Thread t = new Thread(s);
        t.start();
        
    }
    
    
    
}

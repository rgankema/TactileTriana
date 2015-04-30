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


/**
 *
 * @author jd
 */
public class APIServer implements Runnable {
    
    protected int serverPort   = 8080;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped    = false;
    private ServerConnection controlConnection = null;
    
    private final Object controllerLock = new Object();
    
    public APIServer(int port) {
        this.serverPort = port;
    }
    
    
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
            new Thread(
                new ServerConnection(
                    clientSocket, this)
            ).start();
        }
        System.out.println("Server Stopped.") ;
    }
    
    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    
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
    
    public static void main(String args[]) {
        System.out.println("Starting server...");
        APIServer s = new APIServer(8070);
        Thread t = new Thread(s);
        t.start();
        
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author jd
 */
public class ServerConnection implements Runnable {
    
    Socket socket = null;
    APIServer server = null;
    BufferedReader in;
    BufferedWriter out;
    boolean isRunning;
    
    public ServerConnection(Socket s, APIServer server) {
        this.socket = s;
        this.server = server;
        this.isRunning = false;
    }
     
    private void createStreams() throws IOException {
        //The InputStreamReader converts the Socket's byte stream to a character stream. The BufferedReader ads efficiency.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }
    
    public synchronized void sendMessage(String s) {
        try {
            out.write(s + "\n");
            
        } catch (IOException e) {
            //FIXME
            System.out.println("Error while sending message to client socket.");
            shutdown();
            
        }
    }
    
    /**
     * Shutdown and clean-up this ServerConnection
     * This function might be called from two threads at the same time, and thus has to be synchronized 
     */
    private synchronized void shutdown() {
        
        //Check if this connection is still running (i.e. if this function was called before)
        if (this.isRunning) {
            try {
                this.isRunning = false;
                in.close();
                out.close();
                socket.close();
            } catch (IOException e ) {
                //FIXME
                System.out.println("Error on client socket shutdown.");
            }
        }
    }
    
    public synchronized boolean isRunning() {
        return this.isRunning;
    }
    @Override
    public void run() {
        
        try {
            createStreams();
        } catch (IOException ex) {
            //FIXME
            System.out.println("Failed to create input and output streams.");
            return;
        }
        this.isRunning = true;
        while(isRunning()) {
            try {
                String line = in.readLine();
                //TODO parse things
                System.out.println(line);
                
                
            } catch (IOException e) {
                //FIXME
                System.out.println("Error reading/writing from/to client socket");
                shutdown();
                return;
            }
        }
        
        
    }   
    
    public void processRequest(String message) {
        JSONParser parser = new JSONParser();
        try {
            Object e = parser.parse(message);
            //check if the message was a JSON object 
            if(!message.startsWith("{")) {
                //Invalid message
            }
            JSONObject json = (JSONObject) e;
            
            
        } catch(ParseException e) {
            
        }
    }
}

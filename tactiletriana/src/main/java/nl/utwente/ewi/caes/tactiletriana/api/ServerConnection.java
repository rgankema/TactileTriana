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
    
    public enum ClientState {
        CONNECTED,
        DISCONNECTED,
        CONTROL,
        WAITING,
    }
    
    public enum ClientError {
        INVALID_CATEGORY("Invalid message category specified."),
        INVALID_TYPE("Invalid message type specified."),
        INVALID_DATATYPE("Invalid data type encountered in JSON message."),
        UNKNOWN_TYPE("Unknown message type."),
        UNKNOWN_CATEGORY("Unknown message category")
        ;
        
        private final String errorMessage;
        
        ClientError(String m) {
            this.errorMessage = m;
        }
        
        public String errorMessage() {
            return this.errorMessage;
        }
    }
    
    public enum MessageType {
        STARTSIMULATION("StartSimulation"),
        RESETSIMULATION("ResetSimulation"),
        SIMULATIONINFO("SimulationInfo"),
        DEVICEPARAMETERS("DeviceParameters"),
        GETDEVICES("GetDevices"),
        SUBMITPLANNING("SubmitPlanning"),
        REQUESTCONTROL("RequestControl"),
        RELEASECONTROL("ReleaseControl"),
        SIMTIME("SimTime"),
        STOPSIMULATION("StopSimulation")
        ;
        
        private final String type;
        
        MessageType(String type) {
            this.type = type;
        }
        
        @Override
        public String toString() {
            return this.type;
        }
    }
    
    Socket socket = null;
    APIServer server = null;
    BufferedReader in;
    BufferedWriter out;
    //boolean isRunning;
    ClientState state = null;
    
    
    public ServerConnection(Socket s, APIServer server) {
        this.socket = s;
        this.server = server;
        //this.isRunning = false;
        this.state = ClientState.DISCONNECTED;
    }
     
    private void createStreams() throws IOException {
        //The InputStreamReader converts the Socket's byte stream to a character stream. The BufferedReader ads efficiency.
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }
    
    /**
     * Send a message s to the connected Client terminated with a newline.
     * 
     * @param s the String containing the message. May not contain a newline. 
     */
    public synchronized void sendMessage(String s) {
        try {
            out.write(s + "\n");
            out.flush();
            
        } catch (IOException e) {
            //FIXME
            log("Error while sending message to client socket.");
            shutdown();
            
        }
    }
    
    /**
     * Method to send a JSON formatted error message according to the API specification
     * 
     * @param s The error message. Cannot contain newlines. 
     */
    public synchronized void sendError(String s) {
         try {
            out.write("{\"succes\" : false, \"error\" : \"" + s + "\"" + "\n");
            out.flush();
            
        } catch (IOException e) {
            
            log("Error while sending message to client socket.");
            shutdown();
            
        }
    }
    
    /** 
     * Send a standard JSON formatted response indicating success. 
     */
    public synchronized void sendResponse() {
         try {
            out.write("{\"succes\" : true, \"error\" : \"\"" + "\n");
            out.flush();
            
        } catch (IOException e) {
            
            log("Error while sending message to client socket.");
            shutdown();
            
        }
    }
    
    /**
     * Shutdown and clean-up this ServerConnection
     * This function might be called from two threads at the same time, and thus has to be synchronized 
     */
    private synchronized void shutdown() {
        
        //Check if this connection is still running (i.e. if this function was called before)
        if (this.isRunning()) {
            try {
                this.state = ClientState.DISCONNECTED;
                in.close();
                out.close();
                socket.close();
            } catch (IOException e ) {
                //FIXME
                log("Error on client socket shutdown.");
            }
        }
    }
    
    /**
     * Checks if the connection with the Client is still up and this ServerConnection is still running.
     * @return 
     */
    public synchronized boolean isRunning() {
        return (this.state != ClientState.DISCONNECTED);
    }
    
    /**
     * This method provides synchronized access to this ServerConnections state.
     * 
     * @return 
     */    
    private synchronized ClientState getClientState() {
        return this.state;
    } 
    
    
    /**
     * This method allows for synchronized setting of this ServerConnections state.
     * @param s 
     */
    private synchronized void setClientState(ClientState s) {
        this.state = s;
    }
    
    /**
     * Main loop of this ServerConnection.
     */
    @Override
    public void run() {
        
        try {
            createStreams();
        } catch (IOException ex) {
            //FIXME
            log("Failed to create input and output streams.");
            return;
        }
        this.state = ClientState.CONNECTED;
        while(isRunning()) {
            try {
                String line = in.readLine();
                //Check for end of stream
                if(line != null) {
                    //TODO parse things
                    this.processRequest(line);
                    log("Message received: " + line);
                } else {
                    shutdown();
                }
                
                
            } catch (IOException e) {
                //FIXME
                log("Error reading/writing from/to client socket");
                shutdown();
                return;
            }
        }
        
        
    }   
    
    /**
     * Parses the message received by the client. The message are expected to be JSON formatted and should comply with the API protocol defined for the APIServer.
     * 
     * @param message the JSON formatted String containing the message.  
     */
    public void processRequest(String message) {
        JSONParser parser = new JSONParser();
        try {
            Object e = parser.parse(message);
            //check if the message was a JSON object 
            if(!message.startsWith("{")) {
                //Invalid message
            }
            JSONObject json = (JSONObject) e;
            
            String error = null;
            //Check the category of the message (only accept requests)
            boolean categoryPass = true;
            try {
                String category = (String) json.get("category");
                if (category != "request") {
                    error = ClientError.INVALID_CATEGORY.errorMessage();
                    categoryPass = false;
                    log(error);
                    sendError(error);
                    
                   
                }
            } catch (ClassCastException ce) {
                error = ClientError.INVALID_DATATYPE + "In 'message category'.";
                categoryPass = false;
                log(error);
                sendError(error);
                
            } catch (Exception ex) {
                error = ClientError.INVALID_CATEGORY.errorMessage();
                categoryPass = false;
                log(error);
                sendError(error);
                return;
            }
            
            //Check if the cateogry of the message was correct, RETURN if not
            if(!categoryPass) {
                return;
            }
            
            //Check the type of the message
            MessageType type = null;
            
            try {
                String type_received = (String) json.get("type");
                if(type_received == MessageType.DEVICEPARAMETERS.toString()) {
                    type = MessageType.DEVICEPARAMETERS;
                } else if(type_received == MessageType.GETDEVICES.toString()) {
                    type = MessageType.GETDEVICES;
                } else if(type_received == MessageType.RELEASECONTROL.toString()) {
                    type = MessageType.RELEASECONTROL;
                } else if(type_received == MessageType.REQUESTCONTROL.toString()) {
                    type = MessageType.REQUESTCONTROL;
                } else if(type_received == MessageType.RESETSIMULATION.toString()) {
                    type = MessageType.RESETSIMULATION;
                } else if (type_received == MessageType.SIMTIME.toString()) {
                    type = MessageType.SIMTIME;
                } else if (type_received == MessageType.SIMULATIONINFO.toString()) {
                    type = MessageType.SIMULATIONINFO;
                } else if (type_received == MessageType.STARTSIMULATION.toString()) {
                    type = MessageType.STARTSIMULATION;
                } else if (type_received == MessageType.SUBMITPLANNING.toString()) {
                    type = MessageType.SUBMITPLANNING;
                } else if(type_received == MessageType.STOPSIMULATION.toString()) {
                    type = MessageType.STOPSIMULATION;
                }      
                
            } catch (ClassCastException ce) {
                error = ClientError.INVALID_DATATYPE + "In 'message type'.";
                categoryPass = false;
                log(error);
                sendError(error);
                
            } catch (Exception ex) {
                error = ClientError.INVALID_TYPE.errorMessage();
                categoryPass = false;
                log(error);
                sendError(error);
                return;
            }
            
            //Check if the type was valid, return if not
            if(type == null) {
                sendError(ClientError.UNKNOWN_TYPE.errorMessage());
                log(ClientError.UNKNOWN_TYPE.errorMessage());
            } 
            
            //Check if the data field exists
            //JSONObject data = (JSONObject) json.get("data");
            
            //Process the message according to its type and the state of this server connection
            
            //Check the state of this connection
            if(getClientState() == ClientState.CONNECTED) {
                switch (type) {
                    case GETDEVICES:
                        break;
                    case REQUESTCONTROL:
                        break;
                    case SIMULATIONINFO:
                        break;
                }
            } else if (getClientState() == ClientState.CONTROL) {
                switch (type) {
                    case GETDEVICES:
                        break;
                    case SIMULATIONINFO:
                        break;
                    case STARTSIMULATION:
                        break;
                    case STOPSIMULATION:
                        break;
                    case RESETSIMULATION:
                        break;
                    case RELEASECONTROL:
                        break;
                    case SIMTIME: 
                        break;
                    case DEVICEPARAMETERS:
                        break;
                }
            } else if (getClientState() == ClientState.WAITING) {
                switch (type) {
                    case GETDEVICES:
                        break;
                    case SIMULATIONINFO:
                        break;
                    case STARTSIMULATION:
                        break;
                    case STOPSIMULATION:
                        break;
                    case RESETSIMULATION:
                        break;
                    case RELEASECONTROL:
                        break;
                    case SIMTIME: 
                        break;
                    case DEVICEPARAMETERS:
                        break;
                    case SUBMITPLANNING:
                        break;
                }
            }
            
            
            
            
        } catch(ParseException e) {
            this.sendMessage("{\"succes\" : false, \"error\" : \"Invalid JSON request received.\"}");
           
        }
    }
    
    public void processGetDevices() {
        
    }
    
    public void processStartSimulation() {
        
    }
    
    public void processStopSimulation() {
        
    }
    
    public void processResetSimulation() {
        
    }
    
    public void processDeviceParameters() {
        
    }
    
    public void processSimulationInfo() {
        
    }
    
    public void processSubmitPlanning() {
        
    }
    
    public void processReleaseControl() {
        
    }
    
    public void processRequestControl() {
        
    }
    
    
    public void log(String s) {
        System.out.println(this.hashCode() + " # " + s + "\n");
    }
}

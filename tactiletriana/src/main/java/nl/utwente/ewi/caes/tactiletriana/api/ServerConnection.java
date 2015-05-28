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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import nl.utwente.ewi.caes.tactiletriana.SimulationConfig;
import nl.utwente.ewi.caes.tactiletriana.simulation.DeviceBase;
import nl.utwente.ewi.caes.tactiletriana.simulation.House;
import nl.utwente.ewi.caes.tactiletriana.simulation.IController;
import nl.utwente.ewi.caes.tactiletriana.simulation.Simulation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import nl.utwente.ewi.caes.tactiletriana.Concurrent;


/**
 *
 * @author jd
 */
public class ServerConnection implements Runnable, IController {

    

    
    
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
        UNKNOWN_CATEGORY("Unknown message category"),
        TYPENOTACCEPTED("Message type not accepted."),
        INVALID_DATA("Data field not accepted.")
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
        GETHOUSES("GetHouses"),
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
    ClientState state = null;
    
    //IController specific variables
    LocalDateTime lastUpdatedPlanning = null;
    LocalDateTime lastRequestPlanning = null;
    
    
    
    
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
            out.write("{\"success\" : false, \"error\" : \"" + s + "\"" + "}\n");
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
            out.write("{\"success\" : true, \"error\" : \"\"" + "}\n");
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
    public synchronized void shutdown() {
        
        //Check if this connection is still running (i.e. if this function was called before)
        if (this.isRunning()) {
            try {
                //If this client is the current controller, deregister it
                server.releaseControl(this);
                
                //Clena up
                this.state = ClientState.DISCONNECTED;
                in.close();
                out.close();
                socket.close();
                server.removeConnection(this);
            } catch (IOException e ) {
                //FIXME
                log("Error on client socket shutdown.");
            }
        }
        log("Client disconnected.");
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
        log("Client state changed: " + s.toString());
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
                if (!category.equals("request")) {
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
                if(type_received.equals(MessageType.DEVICEPARAMETERS.toString())) {
                    type = MessageType.DEVICEPARAMETERS;
                } else if(type_received.equals(MessageType.GETHOUSES.toString())) {
                    type = MessageType.GETHOUSES;
                } else if(type_received.equals(MessageType.RELEASECONTROL.toString())) {
                    type = MessageType.RELEASECONTROL;
                } else if(type_received.equals(MessageType.REQUESTCONTROL.toString())) {
                    type = MessageType.REQUESTCONTROL;
                } else if(type_received .equals(MessageType.RESETSIMULATION.toString())) {
                    type = MessageType.RESETSIMULATION;
                } else if (type_received.equals(MessageType.SIMTIME.toString())) {
                    type = MessageType.SIMTIME;
                } else if (type_received.equals(MessageType.SIMULATIONINFO.toString())) {
                    type = MessageType.SIMULATIONINFO;
                } else if (type_received.equals(MessageType.STARTSIMULATION.toString())) {
                    type = MessageType.STARTSIMULATION;
                } else if (type_received.equals(MessageType.SUBMITPLANNING.toString())) {
                    type = MessageType.SUBMITPLANNING;
                } else if(type_received.equals(MessageType.STOPSIMULATION.toString())) {
                    type = MessageType.STOPSIMULATION;
                }      
                
            } catch (ClassCastException ce) {
                error = ClientError.INVALID_DATATYPE.errorMessage() + " In 'message type'.";
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
                return;
            } 
            
            //Check if the data field exists
            //JSONObject data = (JSONObject) json.get("data");
            
            //Process the message according to its type and the state of this server connection
            
            //Check the state of this connection
            if(getClientState() == ClientState.CONNECTED) {
                switch (type) {
                    case GETHOUSES:
                        processGetHouses();
                        break;
                    case REQUESTCONTROL:
                        processRequestControl();
                        break;
                    case SIMULATIONINFO:
                        processSimulationInfo();
                        break;
                    default:
                        sendError(ClientError.TYPENOTACCEPTED.errorMessage());
                        break;
                }
            } else if (getClientState() == ClientState.CONTROL) {
                switch (type) {
                    case GETHOUSES:
                        processGetHouses();
                        break;
                    case SIMULATIONINFO:
                        processSimulationInfo();
                        break;
                    case STARTSIMULATION:
                        processStartSimulation();
                        break;
                    case STOPSIMULATION:
                        processStopSimulation();
                        break;
                    case RESETSIMULATION:
                        processResetSimulation();
                        break;
                    case RELEASECONTROL:
                        processReleaseControl();
                        break;
                    case SIMTIME:
                        processSimTime();
                        break;
                    case DEVICEPARAMETERS:
                        processDeviceParameters(json);
                        break;
                    default:
                        sendError(ClientError.TYPENOTACCEPTED.errorMessage());
                        break;
                }
            } else if (getClientState() == ClientState.WAITING) {
                switch (type) {
                    case GETHOUSES:
                        processGetHouses();
                        break;
                    case SIMULATIONINFO:
                        processSimulationInfo();
                        break;
                    case STARTSIMULATION:
                        processStartSimulation();
                        break;
                    case STOPSIMULATION:
                        processStopSimulation();
                        break;
                    case RESETSIMULATION:
                        processResetSimulation();
                        break;
                    case RELEASECONTROL:
                        processReleaseControl();
                        break;
                    case SIMTIME:
                        processSimTime();
                        break;
                    case DEVICEPARAMETERS:
                        processDeviceParameters(json);
                        break;
                    case SUBMITPLANNING:
                        processSubmitPlanning(json);
                        break;
                    default:
                        sendError(ClientError.TYPENOTACCEPTED.errorMessage());
                        break;
                }
            }
            
            
            
            
        } catch(ParseException e) {
            this.sendMessage("{\"success\" : false, \"error\" : \"Invalid JSON request received.\"}");
            log("Invalid message received.");
           
        } catch (ClassCastException e) {
            this.sendMessage("{\"success\" : false, \"error\" : \"Invalid JSON request received.\"}");
            log("Invalid message received.");
        }
    }
    
    public void processGetHouses() {
        log("Processing GetDevices request...");
        //Get the houses in JSON format
        House[] houses = server.getSimulation().getHouses();
        JSONArray housesJSON = new JSONArray();
        for(House house : houses) {
            housesJSON.add(house.toJSON());
        }
        JSONObject response = new JSONObject();
        response.put("houses", housesJSON);
        this.sendMessage(response.toJSONString());
        
    }
    
    public void processStartSimulation() {
        log("Processing StartSimulation request...");
        server.getSimulation().start();
        sendResponse();
    }
    
    public void processStopSimulation() {
        log("Processing StopSimulation request...");
        server.getSimulation().pause();
        sendResponse();
    }
    
    public void processResetSimulation() {
        log("Processing ResetSimulation request...");
        server.getSimulation().reset();
        sendResponse();
    }
    
    public void processDeviceParameters(JSONObject json) {
        //Extract the date part of the JSON message
        String error = null;
        JSONObject data = null;
        try {
            data = (JSONObject) json.get("data");
                

        } catch (ClassCastException ce) {
            error = ClientError.INVALID_DATATYPE + "In 'data field'.";

            log(error);
            sendError(error);
            return;

        } catch (Exception ex) {
            error = ClientError.INVALID_DATA.errorMessage();

            log(error);
            sendError(error);
            return;
        }
        
        //Get the devices JSON
        ArrayList<JSONObject> devices = null;
        try {
            devices = (JSONArray) data.get("devices");
        } catch (ClassCastException e) {
            error = "GetDevices: wrong devices parameter in data field.";
            log(error);
            sendError(error);
            return;
        }
        
        //Loop through the devices and update the parameters
        boolean hasError = false;
        //Update the planning for all devices
        
        for(JSONObject deviceJSON : devices ) {
                try {
                //Get the deviceID
                int deviceID = (int) deviceJSON.get("deviceID");

                //Get the device
                DeviceBase device = server.getSimulation().getDeviceByID(deviceID);
                //Check if the device exists.
                if(device == null) {
                    hasError = true;
                    error = error + " Device with id " + deviceID + " does not exist.";
                    break;
                    
                }
                               
                
                //Get the parameters
                HashMap<String,JSONObject> parameters = (JSONObject) deviceJSON.get("parameters");
                
                //Update each parameter
                //TODO handle errors
                
                for(String parameter: parameters.keySet()) {
                    try {
                        //Run updateParameters on the JavaFX thread because it might change something in the GUI
                        Concurrent.runOnJavaFXThreadSynchronously(() -> {
                            device.updateParameter(parameter, parameters.get(parameter));
                        });
                        
                    } catch (Exception e) {
                        hasError = true;
                        error = error + "SubmitPlanning: updating paramter '" + parameter +"' failed.";
                    }
                }

                
            } catch (ClassCastException e) {
                error = "SubmitPlanning failed, wrong devices parameter";
                log(error);
                sendError(error);
                return;
            }

        }
        
        //Check for errors that happened in the loop
        if(hasError) {
            log(error);
            sendError(error);
        }
        
    }
    
    /**
     * Create a JSON response for the SimulationInfo request and send it to the client.
     */
    public void processSimulationInfo() {
        log("Processing SimulationInfo request...");
        Simulation sim = server.getSimulation();
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("isRunning", (sim.getState() == Simulation.SimulationState.RUNNING));
        jsonResponse.put("isStarted", (sim.getState() != Simulation.SimulationState.STOPPED));
        //Minute of the year in the simulation.
        jsonResponse.put("simTime", (sim.getCurrentTime().getDayOfYear()-1)*24*60 + sim.getCurrentTime().getHour() * 60 + sim.getCurrentTime().getMinute());
        jsonResponse.put("timeStep", SimulationConfig.TICK_MINUTES);
        sendMessage(jsonResponse.toJSONString());
    }
    
    public void processSubmitPlanning(JSONObject json) {
        //Extract the date part of the JSON message
        String error = null;
        JSONObject data = null;
        try {
            data = (JSONObject) json.get("data");
                

        } catch (ClassCastException ce) {
            error = ClientError.INVALID_DATATYPE + "In 'data field'.";

            log(error);
            sendError(error);
            return;

        } catch (Exception ex) {
            error = ClientError.INVALID_DATA.errorMessage();

            log(error);
            sendError(error);
            return;
        }
        
        //Extract the time for which the planning was send
        //The time should match the current simulation time.
        int intTime = 0;
        try {
            intTime = (int)((long) data.get("simTime"));
        } catch (ClassCastException e) {
            error = "SubmitPlanning failed, invalid simTime parameter.";
            log(error);
            sendError(error);
            return;
        }
        
        //Convert the time in minutes since the start of 2014 to a LocalDateTime instance
        LocalDateTime time = LocalDateTime.of(2014, 1, 1, 0,0).plusMinutes(intTime);
        
        //Check if the submitted planning is for the last requested planning.
        if(!time.equals(lastRequestedPlanningTime())) {
            error = "Submitted planning is not for the currently requested simulation time.";
            log(error);
            sendError(error);
            return;
        }
        
        this.lastUpdatedPlanning = time;
        
        //Extract the devices, if no devices parameter exists no plannings are updated
        ArrayList<JSONObject> devices = null;
        try {
            Object devicesRaw = data.get("devices");
            if(devicesRaw != null) {
                devices = (JSONArray) devicesRaw;
            } else {
                //No updated plannings
                sendResponse();
                return;
            }
            
        } catch (Exception e) {
            error = "SubmitPlanning failed, invalid devices parameter.";
            log(error);
            sendError(error);
            return;
        }
        
        //Check if the devices parameter was not empty
        if(devices != null && devices.size() == 0) {
            //No updated plannings
            sendResponse();
            return;
        }
        
        boolean hasError = false;
        //Update the planning for all devices
        
        for(JSONObject deviceJSON : devices ) {
                try {
                //Get the deviceID
                int deviceID = (int) deviceJSON.get("deviceID");

                //Get the device
                DeviceBase device = server.getSimulation().getDeviceByID(deviceID);
                //Check if the device exists.
                if(device == null) {
                    hasError = true;
                    error = error + " Device with id " + deviceID + " does not exist.";
                    break;
                }

                //Get the parameters
                JSONObject parameters = (JSONObject) deviceJSON.get("parameters");
                Set<String> availableParameters = device.getAPIProperties();

                //Update the planning
                //TODO catch errors in updateParameters()
                if(parameters.containsKey("planning")) {
                    //Run updateParameters on JavaFX thread, because it might change something in the GUI
                    Concurrent.runOnJavaFXThreadSynchronously(() -> {
                        device.updateParameter("planning", parameters.get("planning"));
                    });
                    
                } else if (parameters.containsKey("ts_planning")) {
                    device.updateParameter("ts_planning", parameters.get("ts_planning"));
                } else {
                    hasError = true;
                    error = error + "No valid planning parameter for device with id " + deviceID + ".";
                }
            } catch (ClassCastException e) {
                error = "SubmitPlanning failed, wrong devices parameter";
                log(error);
                sendError(error);
                return;
            }

        }
        
        //Update the last updated planning time on success
        this.lastUpdatedPlanning = time;
        log("Controller update planning.");
        
    }
    
    public void processSimTime() {
        log("Processing SimTime request... NOOP");
        
    }
    
    public void processReleaseControl() {
        log("Processing ReleaseControl request...");
        server.releaseControl(this);
        setClientState(ClientState.CONNECTED);
        sendResponse();
    }
    
    public void processRequestControl() {
        log("Processing RequestControl request...");
        if(server.requestControl(this)) {
            setClientState(ClientState.CONTROL);
            sendResponse();
            
        } else {
            sendError("RequestControl request denied.");
        }
        
    }
    
    /**
     * Returns the last time the planning was updated.
     * May return null if the planning has never been updated.
     * 
     * @return LocalDateTime containing the last time the planning was updated or null
     */
    public LocalDateTime lastPlanningTime() {
        return this.lastUpdatedPlanning;
    }
    
    public LocalDateTime lastRequestedPlanningTime() {
        return this.lastRequestPlanning;
    }
    
    
    
    /**
     * This method updates the planning. 
     * The (@code time} argument is used to record the last time the planning was updated.
     * The {@codetimeout} parameter specifies how long the retrieval of the planning may take.
     * 
     * @param timeout Timeout in 100 millisecond units
     * @param time  
     */
    public boolean retrievePlanning(int timeout, LocalDateTime time) {
        
        //Set the time this planning was requested
        this.lastRequestPlanning = time;
        
        //Set the ClientState
        setClientState(ClientState.WAITING);
        
        //Send the RequestPlanning request
        JSONObject response = new JSONObject();
        Simulation sim = server.getSimulation();
        response.put("category", "request");
        response.put("type", "RequestPlanning");
        response.put("simTime", (sim.getCurrentTime().getDayOfYear()-1)*24*60 + sim.getCurrentTime().getHour() * 60 + sim.getCurrentTime().getMinute());
        response.put("timeStep", SimulationConfig.TICK_MINUTES);
        sendMessage(response.toJSONString());
        log("Sent request for planning...");
        
        
        //Now wait for the timeout period specified or until a SubmitPlanning request has been received. 
        boolean planningReceived = false;
        log("a");
        int looptime = 0;
        while (!planningReceived && looptime < timeout) {
            log(planningReceived + " " + looptime + " " + timeout);
            //The lastPlanningTime will be updated when the new planning is received
            log("b");
            if(this.lastUpdatedPlanning.equals(this.lastRequestPlanning)) {
                planningReceived = true;
            }
            log("c");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                log("Sleep interrupted");
            }
            
            looptime++;
            
        }
        log("d");
        if(planningReceived) {
            //Planning accepted, going back to Control
            setClientState(ClientState.CONTROL);
            log("Received planning from Controller.");
        } else {
            log("Failed to receive planning from Controller. Timeout expired...");
        }
        log("e");
        
        return planningReceived;
    }
    
   
    
    
    public void log(String s) {
        System.out.println(this.hashCode() + " # " + s + "\n");
    }
}

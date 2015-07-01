/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.api;

import java.io.*;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author jd
 */
public class APIClient implements Runnable {
    
        public enum ClientError {

        INVALID_CATEGORY("Invalid message category specified."),
        INVALID_TYPE("Invalid message type specified."),
        INVALID_DATATYPE("Invalid data type encountered in JSON message."),
        UNKNOWN_TYPE("Unknown message type."),
        UNKNOWN_CATEGORY("Unknown message category"),
        TYPENOTACCEPTED("Message type not accepted."),
        INVALID_DATA("Data field not accepted.");

        private final String errorMessage;

        ClientError(String m) {
            this.errorMessage = m;
        }

        public String errorMessage() {
            return this.errorMessage;
        }
    }
        
    public enum ClientState {

        CONNECTED,
        DISCONNECTED,
        CONTROL,
        WAITING,
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
        STOPSIMULATION("StopSimulation"),
        REQUESTPLANNING("RequestPlanning");

        private final String type;

        MessageType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return this.type;
        }
    }
    
    
    
    private int defaultPortnumber = 4321;
    
    private Socket socket;
    private int portNumber;
    private String hostname;
    private BufferedReader in;
    private BufferedWriter out;
    private ClientState state;
    //Methods listening to the socket use this lock    
    private final ReentrantLock lock;
    
    
    
    public APIClient(String hostname, int portNumber) {
        this.portNumber = portNumber;
        this.hostname = hostname;
        this.state = ClientState.DISCONNECTED;
        this.lock = new ReentrantLock(true);
    }
    
    public void run() {
        //Create a new server socket
        try {
            this.socket = new Socket(this.hostname, this.portNumber);
            //The InputStreamReader converts the Socket's byte stream to a character stream. The BufferedReader ads efficiency.
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.state = ClientState.CONNECTED;
            
            while(isRunning()) {
                listenToServer(500); 
                Thread.sleep(2);
                
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        } catch (InterruptedException e ) {
            
        }
    }
    
    public void processRequest(String message) {
        JSONParser parser = new JSONParser();
        try {
            Object e = parser.parse(message);
            //check if the message was a JSON object 
            if (!message.startsWith("{")) {
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
            if (!categoryPass) {
                return;
            }

            //Check the type of the message
            MessageType type = null;

            try {
                String type_received = (String) json.get("type");
                if (type_received.equals(MessageType.REQUESTPLANNING.toString())) {
                    type = MessageType.REQUESTPLANNING;
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
            if (type == null) {
                sendError(ClientError.UNKNOWN_TYPE.errorMessage());
                log(ClientError.UNKNOWN_TYPE.errorMessage());
                return;
            }

            //Check if the data field exists
            //JSONObject data = (JSONObject) json.get("data");
            //Process the message according to its type and the state of this server connection
            //Check the state of this connection
            if (getClientState() == ClientState.CONNECTED) {
                sendError(ClientError.TYPENOTACCEPTED.errorMessage());
                
                
            } else if (getClientState() == ClientState.CONTROL) {
                if(type == MessageType.REQUESTPLANNING) {
                    log("Received request planning.");
                } else {
                    sendError(ClientError.TYPENOTACCEPTED.errorMessage());
                }    
                        
                
                
            } else if (getClientState() == ClientState.WAITING) {
                sendError(ClientError.TYPENOTACCEPTED.errorMessage());
                        
                
            }

        } catch (ParseException e) {
            this.sendMessage("{\"success\" : false, \"error\" : \"Invalid JSON request received.\"}");
            log("Invalid message received.");

        } catch (ClassCastException e) {
            this.sendMessage("{\"success\" : false, \"error\" : \"Invalid JSON request received.\"}");
            log("Invalid message received.");
        }
    }
    
    
    
    /**
     * Method to send a JSON formatted error message according to the API
     * specification
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
    
    public synchronized void sendMessage(String s) {
        try {
            log("Sending message: " + s);
            out.write(s + "\n");
            out.flush();

        } catch (IOException e) {
            //FIXME
            log("Error while sending message to client socket.");
            shutdown();

        }
    }
    
     /**
      * Shutdown the connection with the server.
      */
    public synchronized void shutdown() {

        //Check if this connection is still running (i.e. if this function was called before)
        if (this.isRunning()) {
            try {
                

                //Clean up
                this.state = ClientState.DISCONNECTED;
                in.close();
                out.close();
                socket.close();
                
            } catch (IOException e) {
                //FIXME
                log("Error on client socket shutdown.");
            }
        }
        log("Disconnected form server.");
    }
    
    /**
     * This method provides synchronized access to this ServerConnections state.
     *
     * @return
     */
    private ClientState getClientState() {
        return this.state;
    }
    
    public boolean getControl() {
        boolean result = false;
        lock.lock();
        try {
            log("GetControl called...");
            
            JSONObject request = new JSONObject();
            request.put("category", "request");
            request.put("type", MessageType.REQUESTCONTROL.toString());
            sendMessage(request.toJSONString());
            log("getControl calls getResponse...");
            JSONObject response = getResponse(1000);
            try {
                if(response != null && ((Boolean)response.get("success")) == true) {
                    this.state = ClientState.CONNECTED;
                    result = true;
                } else {
                    log("RequestControl failed, did not receive response.");
                }
            } catch (ClassCastException e) {

            }
        } finally {
            lock.unlock();
        }
        return result;
    }
    
    /**
     * 
     * 
     * @param timeout time to wait for the response
     * @return JSONObject containing the response, null if no response was received within the timeout 
     */
    public JSONObject getResponse(int timeout) {
        lock.lock();
        JSONObject response = null;
        
        
        try {
            log("Waiting for response...");        
            
            socket.setSoTimeout(timeout);
            String line = in.readLine();
            //Check for end of stream
            if (line != null) {
                JSONParser parser = new JSONParser();
                try {
                    Object e = parser.parse(line);
                    //check if the message was a JSON object 

                    response = (JSONObject) e;


                    log("Message received: " + line);
                } catch (ParseException e) {

                } catch (ClassCastException e) {

                }        

            } else {
                shutdown();
            }
            
            log("Stopped waiting for response...");
        } catch (SocketTimeoutException e) {
            //Timeout expired, break out of the loop
            log("GetResponse timed out..." + e.getMessage());

        } catch (IOException e) {
            //FIXME
            log("Error reading/writing from/to client socket");
            shutdown();

        } finally {
            lock.unlock();
        }

        
        return response;
    }
    
    /**
     * Listen to the server for a specified amount of time
     * 
     * @param timeout Time to listen to the server 
     */
    public void listenToServer(int timeout) {
        lock.lock();
        
        
        try {
            log("Listening to server...");
            while(isRunning()) {

                try {
                    //Set a timeout, allows getting reponses for specific calls inbetween general listening 
                    socket.setSoTimeout(timeout);

                    String line = in.readLine();
                    //Check for end of stream
                    if (line != null) {
                        //TODO parse things
                        this.processRequest(line);
                        log("listenToServer: Message received: " + line);
                    } else {
                        shutdown();
                    }

                } catch(SocketTimeoutException e) {
                    //Timeout expired, break out of the loop
                    break;
                } 
                catch (IOException e) {
                    //FIXME
                    log("Error reading/writing from/to client socket");
                    shutdown();
                    break;
                }
            }
            log("Stopped listening...");
        } finally {
            lock.unlock();
        }
    }
    
    
    /**
     * Checks if the connection with the Client is still up and this
     * ServerConnection is still running.
     *
     * @return
     */
    public boolean isRunning() {
        return (this.state != ClientState.DISCONNECTED);
    }
    
    public void log(String s) {
        System.out.println(this.hashCode() + " # " + s + "\n");
    }
    
    
    /**
     *
     * @param args
     */
    public static void main(String args[]) throws InterruptedException {
        APIClient client = new APIClient("localhost", 4321);
        //client.start();
        new Thread(client).start();
        //Execute a test scenario here
        while (client.getClientState() != ClientState.CONNECTED) {
            System.out.println("Waiting for connection to server.....");
            Thread.sleep(1000);
        }
        System.out.println("Send request control...");
        System.out.println("Result: " + client.getControl());
        
    }
    
}

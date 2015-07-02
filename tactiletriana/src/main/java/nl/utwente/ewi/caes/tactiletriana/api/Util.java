/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.api;

/**
 *
 * @author jd
 */
public class Util {
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
        INVALID_DATA("Data field not accepted.");

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
}

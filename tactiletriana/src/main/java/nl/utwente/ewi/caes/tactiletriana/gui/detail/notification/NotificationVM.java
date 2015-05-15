/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.notification;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Richard
 */
public class NotificationVM {
    
    public NotificationVM(String notification) {
        setMessage(notification);
    }
    
    // BINDABLE PROPERTIES
    
    /**
     * The message that should be shown
     */
    private final StringProperty message = new SimpleStringProperty();
    
    public ReadOnlyStringProperty messageProperty() {
        return message;
    }
    
    public final String getMessage() {
        return messageProperty().get();
    }
    
    private final void setMessage(String message) {
        this.message.set(message);
    }
}

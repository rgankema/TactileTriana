/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactiletriana.gui.detail.notification;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import nl.utwente.ewi.caes.tactiletriana.gui.ViewLoader;

/**
 *
 * @author Richard
 */
public class NotificationView extends StackPane {

    @FXML
    Label messageLabel;

    private NotificationVM viewModel;

    public NotificationView() {
        ViewLoader.load(this);
    }

    public void setViewModel(NotificationVM viewModel) {
        if (this.viewModel != null) {
            throw new IllegalStateException("ViewModel can only be set once");
        }

        this.viewModel = viewModel;

        messageLabel.textProperty().bind(viewModel.messageProperty());
    }
}

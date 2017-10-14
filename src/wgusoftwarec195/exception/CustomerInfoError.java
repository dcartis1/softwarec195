/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195.exception;

import javafx.scene.control.Alert;

/**
 *
 * @author Dan
 */
public class CustomerInfoError extends Exception {
    public CustomerInfoError() {}
    public CustomerInfoError(String message)
    {
        super(message);
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Customer data input invalid for the following reason -");
        alert.setContentText(message);

        alert.showAndWait();
    }
}

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
public class SchedulingError extends Exception {
    public SchedulingError() {}
    public SchedulingError(String message)
    {
        super(message);
        
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Schedule Error");
        alert.setHeaderText("Appointment time not available for the following reason -");
        alert.setContentText(message);

        alert.showAndWait();
    }
}

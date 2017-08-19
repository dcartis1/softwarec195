/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195.controller;

import com.sun.javafx.scene.control.skin.DatePickerSkin;
import java.time.LocalDate;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import wgusoftwarec195.MainApp;

/**
 *
 * @author Dan
 */
public class CalendarViewController {
    
    private Stage dialogStage;
    private MainApp mainApp;
    private final Boolean okClicked = false;
    @FXML
    private BorderPane borderPane;
    DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(LocalDate.now()));
    Node popupContent = datePickerSkin.getPopupContent();

    
    
    
    @FXML
    private void initialize(){
        borderPane.setCenter(popupContent);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    

    public boolean isOkClicked() {
        return okClicked;
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
  
}

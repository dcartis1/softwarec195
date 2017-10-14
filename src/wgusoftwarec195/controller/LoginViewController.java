/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195.controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;


/**
 *
 * @author Dan
 */
public class LoginViewController {

    @FXML TextField userNameField;
    @FXML TextField userPassField;
    @FXML Label greetingLabel;
    @FXML Label loginLabel;
    @FXML Label userNameLabel;
    @FXML Label passwordLabel;
    @FXML Button loginBtn;
    
    private MainApp mainApp;  
    private String sql;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private int userId;
    private String userName;
    private Locale locale;
    private final ResourceBundle bundle = ResourceBundle.getBundle("wgusoftwarec195/bundle/messages", Locale.getDefault());

    @FXML
    public void LoginBtnClick(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        //gets systems locale so that certain text and alerts can be translated
        locale = Locale.getDefault();

        try (Connection conn = DbConnection.createConnection()){
            //tries to match user input with a user in the db. displays
            //alert if no match is found
            sql = "SELECT user.userId, user.userName, user.password from user WHERE userName = ? AND password = ?";

            ps = conn.prepareStatement(sql);
            ps.setString(1, userNameField.getText());
            ps.setString(2, userPassField.getText());
            rs = ps.executeQuery();
            //if match is found, open userappview and pass on user details to mainapp
            //so they can then be passed to other controllers accordingly
            if (rs.next()) {
                userId = rs.getInt(1);
                userName = rs.getString(2);
                mainApp.setUserDetails(userId, userName);

                //writes user details and timestamp to txt file on login. if file
                //exists, append new record
                try (BufferedWriter out = new BufferedWriter(new FileWriter("logins.txt", true))) {
                    out.write("UserId:"+ userId + " UserName:" +userName+ " Time:" +Instant.now().toString()+ "\n");
                }

                boolean okClicked = mainApp.showUserAppView();

            }
            //no match was found, display alert in correct language
            else{
                wrongInputFXML();
            }
        }
    }
    
    //invalid user login information alert. translated between english, spanish, french
    //depending on the systems locale
    public void wrongInputFXML() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("loginAlertTitle"));
        alert.setHeaderText(bundle.getString("loginAlertHeader"));
        alert.setContentText(bundle.getString("loginAlertContent"));

        alert.showAndWait();  
    }
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        greetingLabel.setText(bundle.getString("greetingLabel"));
        loginLabel.setText(bundle.getString("loginLabel"));
        userNameLabel.setText(bundle.getString("userNameLabel"));
        passwordLabel.setText(bundle.getString("passwordLabel"));
        loginBtn.setText(bundle.getString("loginBtn"));
    }
}
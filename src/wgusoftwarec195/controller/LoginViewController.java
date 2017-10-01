/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    @FXML Button spanishBtn;
    @FXML Button frenchBtn;
    @FXML Button englishBtn;
    
    private MainApp mainApp;
    
    private String userName;
    private String userPass;
    private String sql;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private int userId;
    
    private Locale locale;
    private ResourceBundle bundle = ResourceBundle.getBundle("wgusoftwarec195/bundles/messages", Locale.getDefault());

    private final Locale l1 = new Locale.Builder()
        .setLanguage("en")
        .setRegion("US")
        .build();
    
    private final Locale l2 = new Locale.Builder()
        .setLanguage("es")
        .setRegion("MX")
        .build();
    
    private final Locale l3 = new Locale.Builder()
        .setLanguage("fr")
        .setRegion("CA")
        .build();
   
    
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        greetingLabel.setText(bundle.getString("greetingLabel"));
        loginLabel.setText(bundle.getString("loginLabel"));
        userNameLabel.setText(bundle.getString("userNameLabel"));
        passwordLabel.setText(bundle.getString("passwordLabel"));
        spanishBtn.setText(bundle.getString("spanishBtn"));
        frenchBtn.setText(bundle.getString("frenchBtn"));
        englishBtn.setText(bundle.getString("englishBtn"));
        loginBtn.setText(bundle.getString("loginBtn"));
    }
    
    public LoginViewController() {
    }
    
    
    @FXML
    void EnglishBtnClick(ActionEvent event){
        Locale.setDefault(l1);
        loadLanguage("en");
    }
    
    @FXML
    void SpanishBtnClick(ActionEvent event){
        Locale.setDefault(l2);
        loadLanguage("es");
    }
    
    @FXML
    void FrenchBtnClick(ActionEvent event){
        Locale.setDefault(l3);
        loadLanguage("fr");
    }
    
    private void loadLanguage(String Language){
        locale = new Locale(Language);
        this.bundle = ResourceBundle.getBundle("wgusoftwarec195/bundles/messages", locale);
        
        greetingLabel.setText(bundle.getString("greetingLabel"));
        loginLabel.setText(bundle.getString("loginLabel"));
        userNameLabel.setText(bundle.getString("userNameLabel"));
        passwordLabel.setText(bundle.getString("passwordLabel"));
        spanishBtn.setText(bundle.getString("spanishBtn"));
        frenchBtn.setText(bundle.getString("frenchBtn"));
        englishBtn.setText(bundle.getString("englishBtn"));
        loginBtn.setText(bundle.getString("loginBtn"));
        
        System.out.println("Locale set to " + Locale.getDefault());
    }
   
    @FXML
    public void LoginBtnClick(ActionEvent event) throws SQLException, IOException, ClassNotFoundException {
        locale = Locale.getDefault();
        
        userName = userNameField.getText().trim();
        userPass = userPassField.getText().trim();

        try (Connection conn = DbConnection.createConnection()){
            sql = "SELECT user.userId, user.userName, user.password from user WHERE userName = ? AND password = ?";
            
            ps = conn.prepareStatement(sql);
            ps.setString(1, userName);
            ps.setString(2, userPass);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                
                userId = rs.getInt(1);
                System.out.println("Login Successful!");
                System.out.println("Your location is " +locale);
                
                System.out.println("User has an id of " +userId);
                
            mainApp.setUserDetails(userId, userName);
            
            boolean okClicked = mainApp.showUserAppView();

            } else {
                wrongInputFXML();
            }
        }
    }
    public void wrongInputFXML() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(bundle.getString("loginAlertTitle"));
        alert.setHeaderText(bundle.getString("loginAlertHeader"));
        alert.setContentText(bundle.getString("loginAlertContent"));

        alert.showAndWait();
        
    }
}

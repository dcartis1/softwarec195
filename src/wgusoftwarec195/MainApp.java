/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import wgusoftwarec195.controller.AddAppointmentViewController;
import wgusoftwarec195.controller.AddCustomerViewController;
import wgusoftwarec195.controller.LoginViewController;
import wgusoftwarec195.controller.UserAppViewController;
import wgusoftwarec195.model.Customer;
import wgusoftwarec195.model.Schedule;
import wgusoftwarec195.controller.CalendarViewController;
import wgusoftwarec195.model.Appointment;

/**
 *
 * @author Dan
 */
public class MainApp extends Application {
    
    private Stage primaryStage;
    private BorderPane rootLayout;
    private int userId;
    private String userName;
    private final Schedule schedule = new Schedule();

    //i18n testing
    //the languages I chose are en_US, es_MX, and fr_CA
    public MainApp(){
        //Locale.setDefault(new Locale("es", "MX"));
        //Locale.setDefault(new Locale("fr", "CA"));
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("WGUSoftwareC195 - Daniel Cartisano");
        initRootLayout();

        showLoginView();
    }

    //initalize root layout
    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //show application overview in root layout
    public void showLoginView() {
        try {
            // Load loginview
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/LoginView.fxml"));
            AnchorPane appOverview = (AnchorPane) loader.load();
            rootLayout.setCenter(appOverview);   
            //give controller access to mainApp
            LoginViewController controller = loader.getController();
            controller.setMainApp(this);
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public boolean showUserAppView() throws ClassNotFoundException, SQLException {
    try {
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/UserAppView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();
        
        //give controller access to the main app and pass the logged in user ID
        UserAppViewController controller = loader.getController();
        controller.setMainApp(this, userId, schedule);

        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Product");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        //set dialog stage into the controller
        controller.setDialogStage(dialogStage);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();

        return controller.isOkClicked();
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}
    
   public boolean showAddCustView() {
    try {
        // Load the fxml file and create a new stage
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/AddCustomerView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Give the controller access to the main app and also
        //pass the current user's details to the controller so that
        //we know who is going to be making changes to the database
        AddCustomerViewController controller = loader.getController();
        controller.setMainApp(this, userId, userName, schedule);
        
        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Customer");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        //set dialog stage into the controller
        controller.setDialogStage(dialogStage);
        
        //tell controller that user is adding a new customer
        controller.setNewCust(true);
        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
        return controller.isOkClicked();
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}
   
   public boolean showAddCustView(Customer customer) {
    try {
        // Load the fxml file and create a new stage
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/AddCustomerView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Give the controller access to the main app and also
        //pass the current user's details to the controller so that
        //we know who is going to be making changes to the database
        AddCustomerViewController controller = loader.getController();
        controller.setMainApp(this, userId, userName, schedule);
        
        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Customer");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        //set dialog stage into the controller
        controller.setDialogStage(dialogStage);
        
        //tell controller that user is adding a new part
        controller.setNewCust(false);
        controller.setCustomer(customer);
        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
        
        return controller.isOkClicked();
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}
    public boolean showAddAppointmentView() {
    try {
        // Load the fxml file and create a new stage
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/AddAppointmentView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        // Give the controller access to the main app and also
        //pass the current user's details to the controller so that
        //we know who is going to be making changes to the database
        Appointment appointment = new Appointment();
        AddAppointmentViewController controller = loader.getController();
        controller.setMainApp(this, userId, userName, schedule, appointment);
        
        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Appointment");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        //set dialog stage into the controller
        controller.setDialogStage(dialogStage);
        
        //tell controller that user is adding a new customer
        controller.setNewAppoint(true);
        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
        return controller.isOkClicked();
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}
    
     public boolean showAddAppointmentView(Appointment appointment) throws ClassNotFoundException, SQLException {
    try {
        // Load the fxml file and create a new stage
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/AddAppointmentView.fxml"));
        AnchorPane page = (AnchorPane) loader.load();

        //Give the controller access to the main app 
        AddAppointmentViewController controller = loader.getController();

        //tell the controller that user selected an appointment for view/edit
        controller.setNewAppoint(false);
        //pass the current user's details to the controller, as well as
        //the selected appointment
        controller.setMainApp(this, userId, userName, schedule, appointment);
        controller.setAppoint(appointment);
        
        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Appointment");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        
        //set dialog stage into the controller
        controller.setDialogStage(dialogStage);
        
        //tell controller that user is adding a new appointment
        controller.setNewAppoint(false);
        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();
        return controller.isOkClicked();
    } catch (IOException e) {
        e.printStackTrace();
        return false;
    }
}

    //saves user details after login so they can be used throughout the application,
    //specifically when the user is creating or updating records in the database
    public void setUserDetails(int userId, String userName){
        this.userId = userId;
        this.userName = userName;
    }
    
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

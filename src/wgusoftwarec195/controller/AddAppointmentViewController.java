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
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;
import wgusoftwarec195.model.Appointment;
import wgusoftwarec195.model.City;
import wgusoftwarec195.model.Customer;
import wgusoftwarec195.model.Schedule;

/**
 *
 * @author Dan
 */
public class AddAppointmentViewController {
    
    @FXML
    private Label addAppointLabel;
    @FXML
    private TextField appointTitleField;
    @FXML
    private TextArea appointDescriptionArea;
    @FXML
    private TextField appointLocationField;
    @FXML
    private TextField appointContactField;
    @FXML
    private TextField appointUrlField;
    @FXML
    private DatePicker appointDateField;
    @FXML
    private Spinner startTimeSpinner;
    @FXML
    private Spinner endTimeSpinner;
    
    @FXML
    private TableView<Customer> custTable1;
    @FXML
    private TableColumn<Customer, Integer> custIdColumn1;
    @FXML
    private TableColumn<Customer, String> custNameColumn1;
    @FXML
    private TableColumn<Customer, String> custAddressOneColumn1;
    @FXML
    private TableColumn<Customer, String> custAddressTwoColumn1;
    @FXML
    private TableColumn<Customer, String> custPhoneColumn1;
    
    @FXML
    private TableView<Customer> custTable2;
    @FXML
    private TableColumn<Customer, Integer> custIdColumn2;
    @FXML
    private TableColumn<Customer, String> custNameColumn2;
    @FXML
    private TableColumn<Customer, String> custAddressOneColumn2;
    @FXML
    private TableColumn<Customer, String> custAddressTwoColumn2;
    @FXML
    private TableColumn<Customer, String> custPhoneColumn2;
    
    @FXML
    private Button addAppointBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button exitBtn;
    
    private MainApp mainApp;
    private Stage dialogStage;
    private final boolean okClicked = false;
    private boolean newAppoint;
    private String sql;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private int userId;
    private String userName;
    private int custActive;
    private Appointment appointment;
    private Customer customer;
    private Schedule schedule;
    private SpinnerValueFactory<Integer> startValueFactory;
    private SpinnerValueFactory<Integer> endValueFactory;
    
    private final ObservableList<City> cityData = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() throws SQLException, ClassNotFoundException{
        this.startValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 24, 1);
        this.endValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,24,1);
        startTimeSpinner.setValueFactory(startValueFactory);
        endTimeSpinner.setValueFactory(endValueFactory);
    }
        
    //save button inside add new customer window
    @FXML
    public void handleAddAppointBtn(ActionEvent event) throws SQLException, IOException, ClassNotFoundException{  

        //everything related to adding or updating customer is performed in this try-with-resources block
        //so that if any errors occur, the sql transaction will not commit and roll back any database changes.
        try (Connection conn = DbConnection.createConnection()){
            
            //begin sql transaction
            conn.setAutoCommit(false);

            //correctly formatted datetime to be inserted into createdate and lastupdateby columns
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = 
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(dt);

            if (newAppoint == true){
                //insert new record into address table first to retrieve address id for customer table
                sql = "INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdateBy) VALUES "
                        + "('"+ /*setcustomerid here*/"', '"+ appointTitleField.getText()+"', '"+ appointDescriptionArea.getText() +"',"
                        + "'"+ appointLocationField.getText() +"', '"+ appointContactField.getText() +"', '"+ appointUrlField +"'"
                        + ", '"+ appointDateField.getChronology() +"','"+ startTimeSpinner.getValue()+"', '"+endTimeSpinner.getValue()+"',"
                        + " '"+ currentTime+"', '" + userName +"', '" + userName +"')";
                //returns the auto_incremented addressId
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                //executeUpdate method does not return resultset but instead returns affected rows as an int.
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();

            }
            /*
            else{
                
                //update existing customer record
                sql = "UPDATE appointment SET customerName='"+ custNameField.getText() +"', active="+ custActive +","
                        + " lastUpdateBy='"+ userName +"' WHERE customerId='"+ customer.getCustomerId() +"'";

                ps = conn.prepareStatement(sql);
                
                /* 
                    executeUpdate method does not return resultset but instead 
                    returns affected rows as an int. no errors have occured and 
                    if any rows affected then commit sql transaction, update model
                    and close the add customer window
                
                int rows = ps.executeUpdate();
                if(rows > 0){

                    sql = "UPDATE address SET address='"+ custAddressOneField.getText()+"', address2='"+ custAddressTwoField.getText() +"', cityId='"+ selectedCity +"',"
                            + " postalCode='"+ custPostalCodeField.getText()+"', phone='"+ custPhoneField.getText() +"', lastUpdateBy='"+ userName +"'"
                            + " WHERE address.addressId='"+ customer.getAddressId()+"'";

                    ps = conn.prepareStatement(sql);
                    rows = ps.executeUpdate();

                    if(rows > 0){
                        customer.setCustomerName(custNameField.getText());
                        customer.setActive(custActive);
                        customer.setPhone(custPhoneField.getText());
                        customer.setAddressOne(custAddressOneField.getText());
                        customer.setAddressTwo(custAddressTwoField.getText());
                        customer.setCityId(selectedCity);
                        customer.setPostalCode(custPostalCodeField.getText());

                        schedule.updateCustomer(customer);

                        System.out.println("customer edited!");
                        dialogStage.close();
                        //no errors occured so commit sql transaction
                        conn.commit();
                    }
                }
            }*/
        }
    }
    
    @FXML
    public void addAppointCancelBtn(ActionEvent event){
        if(newAppoint == true){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("This Appointment will not be saved.");
        alert.setContentText("Are you sure you would like to cancel and go back to"
                + " main screen without saving this Appointment?");
        alert.showAndWait()
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response -> dialogStage.close());
        }
        else{
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Cancel");
            alert.setHeaderText("These changes will not be saved.");
            alert.setContentText("Are you sure you would like to cancel and go back to"
                    + " main screen without editing this Customer?");        
            alert.showAndWait()

                        .filter(response -> response == ButtonType.OK)
                        .ifPresent(response -> dialogStage.close());
        }
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    //main app tells us if user is adding new or editing existing customer
    public void setNewAppoint(boolean newAppoint){
        this.newAppoint = newAppoint;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
    
    //fills textfields with existing customer data 
    public void setAppoint(Appointment appointment){
        this.appointment = appointment;
        
    //finish populating textfields with existing appointment data
    }
    
    public void setMainApp(MainApp mainApp, int userId, String userName, Schedule schedule) {
        this.mainApp = mainApp;
        this.userId = userId;
        this.userName = userName;
        this.schedule = schedule;
    }
}

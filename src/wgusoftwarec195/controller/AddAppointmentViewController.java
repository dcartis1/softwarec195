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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;
import wgusoftwarec195.TimeConverter;
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
    private TextField startTimeField;
    @FXML
    private TextField endTimeField;
    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Button timeValidTest;
    
    @FXML
    private TableView<Customer> custTable;
    @FXML
    private TableColumn<Customer, Integer> custIdColumn;
    @FXML
    private TableColumn<Customer, String> custNameColumn;
    @FXML
    private TableColumn<Customer, String> custAddressOneColumn;
    @FXML
    private TableColumn<Customer, String> custAddressTwoColumn;
    @FXML
    private TableColumn<Customer, String> custPhoneColumn;
    
    @FXML
    private TableView<Customer> addedCustTable;
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
    private Button addCustomerBtn;
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
        
        custIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        custNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        custAddressOneColumn.setCellValueFactory(cellData -> cellData.getValue().addressOneProperty());
        custAddressTwoColumn.setCellValueFactory(cellData -> cellData.getValue().addressTwoProperty());
        custPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        
        custIdColumn1.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        custNameColumn1.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        custAddressOneColumn1.setCellValueFactory(cellData -> cellData.getValue().addressOneProperty());
        custAddressTwoColumn1.setCellValueFactory(cellData -> cellData.getValue().addressTwoProperty());
        custPhoneColumn1.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
    }
        
    //save button inside add appointment window
    @FXML
    public void handleAddAppointBtn(ActionEvent event) throws SQLException, IOException, ClassNotFoundException{  
 
        // adding or updating appointment is performed in this try-with-resources block
        //so that if any errors occur, the sql transaction will not commit and roll back any database changes.
        try (Connection conn = DbConnection.createConnection()){
            
            //begin sql transaction
            conn.setAutoCommit(false);

            LocalDate ad = appointDateField.getValue();
            LocalTime st = LocalTime.parse(startTimeField.getText());
            LocalTime et = LocalTime.parse(endTimeField.getText());
            
            LocalDateTime appointStart = LocalDateTime.of(ad, st);
            LocalDateTime appointEnd = LocalDateTime.of(ad, et);
            
            //correctly formatted timestamp to be inserted into createdate column
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = 
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(dt);
               
            
            if (newAppoint == true){
                //insert new record into address table first to retrieve address id for appointment table
                sql = "INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdateBy) VALUES "
                        + "('" + appointment.getCustomerInAppointmentData().get(0).getCustomerId() +"', '"+ appointTitleField.getText()+"', '"+ appointDescriptionArea.getText() +"',"
                        + "'"+ appointLocationField.getText() +"', '"+ appointContactField.getText() +"', '"+ appointUrlField.getText() +"'"
                        + ",'"+ TimeConverter.ConvertToUtc(appointStart) +"', '"+ TimeConverter.ConvertToUtc(appointEnd) +"',"
                        + " '"+ TimeConverter.ConvertToUtc(currentTime)+"', '" + userName +"', '" + userName +"')";
                //returns the auto_incremented addressId
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                //executeUpdate method does not return resultset but instead returns affected rows as an int.
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                 if(rs.next()){
                    
                     
                    int newAppointmentId = rs.getInt(1);
                    appointment = new Appointment();
                    appointment.setAppointmentId(newAppointmentId);
                    appointment.setTitle(appointTitleField.getText());
                    appointment.setDescription(appointDescriptionArea.getText());
                    appointment.setLocation(appointLocationField.getText());
                    appointment.setContact(appointContactField.getText());
                    appointment.setUrl(appointUrlField.getText());
                    appointment.setStart(startTimeField.getText());
                    appointment.setEnd(endTimeField.getText());
                    
                    schedule.addAppointment(appointment);
                    System.out.println("appointment added!");
                    dialogStage.close();
                    conn.commit();
                 }
            }
            /*
            else{
                
                //update existing appointment record
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
    
    //Adds customer from the all customer table into the associated customer table
    @FXML
    public void addCustomerBtnClick(){
        Customer selectedCustomer = custTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null){
            appointment.getCustomerInAppointmentData().add(selectedCustomer);
            addedCustTable.setItems(appointment.getCustomerInAppointmentData());
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer is selected");
            alert.setContentText("Please select a Customer from the top table.");
            alert.showAndWait();
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
    
    public void setMainApp(MainApp mainApp, int userId, String userName, Schedule schedule, Appointment appointment) {
        this.mainApp = mainApp;
        this.userId = userId;
        this.userName = userName;
        this.schedule = schedule;
        this.appointment = appointment;
        
        custTable.setItems(schedule.getCustomerData());
    }
    
    //validates user input of start and end appointment time textfields.
    public boolean timeValidate(){
        String time = startTimeField.getText();
        if(!time.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")){
            startTimeLabel.setTextFill(Color.web("red"));
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Input Validation");
            alert.setHeaderText("Start not valid");
            alert.setContentText("Max must be a number.");

            alert.showAndWait();
            return false;

        }
        
        time = endTimeField.getText();
        if(!time.matches("^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$")){
            endTimeLabel.setTextFill(Color.web("red"));
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Input Validation");
            alert.setHeaderText("End not valid");
            alert.setContentText("Max must be a number.");

            alert.showAndWait();
            return false;

        }
        
            LocalDate ad = appointDateField.getValue();
            LocalTime st = LocalTime.parse(startTimeField.getText());
            LocalTime et = LocalTime.parse(endTimeField.getText());
            
            LocalDateTime appointStart = LocalDateTime.of(ad, st);
            LocalDateTime appointEnd = LocalDateTime.of(ad, et);
            
            System.out.println(st+ ","+ et);
            System.out.println(appointStart+","+ appointEnd);
    return true;
    }
}

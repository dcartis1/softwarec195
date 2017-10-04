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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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

            //formats user input from date, starttime, endtime fields and converts
            //into localdatetime objects to be inserted into database.
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String startTime = (appointDateField.getValue()+ " "+ startTimeField.getText());
            String endTime = (appointDateField.getValue() + " " + endTimeField.getText());
            LocalDateTime appointStart = LocalDateTime.parse(startTime, dtf);
            LocalDateTime appointEnd = LocalDateTime.parse(endTime, dtf);
            
            //gets current timestamp to convert from localtime to utc for the
            //creadedate table in database
            Date now = new Date();
            LocalDateTime ldt = LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault());

            if (newAppoint == true){
                int custId = appointment.getCustomerInAppointmentData().get(0).getCustomerId();
                //insert new record into appointment table
                sql = "INSERT INTO appointment (customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdateBy) VALUES "
                        + "('" + custId +"', '"+ appointTitleField.getText()+"', '"+ appointDescriptionArea.getText() +"',"
                        + "'"+ appointLocationField.getText() +"', '"+ appointContactField.getText() +"', '"+ appointUrlField.getText() +"'"
                        + ",'"+ TimeConverter.ConvertToUtc(appointStart) +"', '"+ TimeConverter.ConvertToUtc(appointEnd) +"',"
                        + " '"+ TimeConverter.ConvertToUtc(ldt)+"', '" + userName +"', '" + userName +"')";
                //returns the auto_incremented appointmentId
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                //executeUpdate method does not return resultset but instead returns affected rows as an int.
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();
                
                 if(rs.next()){
                    int newAppointmentId = rs.getInt(1);
                    appointment.setCustomerId(custId);
                    appointment.setAppointmentId(newAppointmentId);
                    appointment.setTitle(appointTitleField.getText());
                    appointment.setDescription(appointDescriptionArea.getText());
                    appointment.setLocation(appointLocationField.getText());
                    appointment.setContact(appointContactField.getText());
                    appointment.setUrl(appointUrlField.getText());
                    
                    String localStartTime = DateTimeFormatter.ofPattern("hh:mm a MM-dd-yyyy").format(appointStart);
                    String localEndTime = DateTimeFormatter.ofPattern("hh:mm a MM-dd-yyyy").format(appointEnd);
                    appointment.setStart(localStartTime);
                    appointment.setEnd(localEndTime);
                    
                    schedule.addAppointment(appointment);
                    System.out.println("appointment added!");
                    dialogStage.close();
                    conn.commit();
                 }
            }
            else{
                //insert new record into appointment table
                sql = "UPDATE appointment SET customerId='"+ appointment.getCustomerInAppointmentData().get(0).getCustomerId()+ "',"
                        + " title='"+appointTitleField.getText() +"', description='"+appointDescriptionArea.getText() +"',"
                        + " location='"+appointLocationField.getText() +"', contact='"+ appointContactField.getText()+"',"
                        + " url='"+ appointUrlField.getText() +"', start='"+ TimeConverter.ConvertToUtc(appointStart) +"',"
                        + " end='"+ TimeConverter.ConvertToUtc(appointEnd) +"', lastUpdateBy='" + userName +"'"
                        + " WHERE appointmentId='"+appointment.getAppointmendId()+"'";
                //returns the auto_incremented appointmentId
                ps = conn.prepareStatement(sql);
                //executeUpdate method does not return resultset but instead returns affected rows as an int.
                int rows = ps.executeUpdate();
                if(rows > 0){
                    appointment.setCustomerId(appointment.getCustomerInAppointmentData().get(0).getCustomerId());
                    appointment.setTitle(appointTitleField.getText());
                    appointment.setDescription(appointDescriptionArea.getText());
                    appointment.setLocation(appointLocationField.getText());
                    appointment.setContact(appointContactField.getText());
                    appointment.setUrl(appointUrlField.getText());                        
                    String localStartTime = DateTimeFormatter.ofPattern("hh:mm a MM-dd-yyyy").format(appointStart);
                    String localEndTime = DateTimeFormatter.ofPattern("hh:mm a MM-dd-yyyy").format(appointEnd);
                    appointment.setStart(localStartTime);
                    appointment.setEnd(localEndTime);

                    schedule.updateAppointment(appointment);
                    System.out.println("appointment edited!");
                    dialogStage.close();
                    conn.commit();
                }
            }
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
    public void setAppoint(Appointment appointment) throws ClassNotFoundException, SQLException{
        this.appointment = appointment;
        this.addAppointLabel.setText("View/Edit Appointment");
        appointTitleField.setText(appointment.getTitle());
        appointDescriptionArea.setText(appointment.getDescription());
        appointLocationField.setText(appointment.getLocation());
        appointContactField.setText(appointment.getContact());
        appointUrlField.setText(appointment.getUrl());
        
        DateTimeFormatter dtf0 = DateTimeFormatter.ofPattern("hh:mm a MM-dd-yyyy");
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        String startString = appointment.getStart();
        String endString = appointment.getEnd();
        
        LocalDateTime localDateStart = LocalDateTime.parse(startString, dtf0);
        LocalDateTime localDateEnd = LocalDateTime.parse(endString, dtf0);
        
        String startDay = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDateStart);
        String startTime = DateTimeFormatter.ofPattern("hh:mm").format(localDateStart);
        String endTime = DateTimeFormatter.ofPattern("hh:mm").format(localDateEnd);
        
        LocalDate ld = LocalDate.parse(startDay, dtf1);
        
        appointDateField.setValue(ld);
        startTimeField.setText(startTime);
        endTimeField.setText(endTime);
        
        addedCustTable.setItems(appointment.getCustomerInAppointmentData());      
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

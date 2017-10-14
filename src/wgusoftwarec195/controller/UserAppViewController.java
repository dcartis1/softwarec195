/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;
import wgusoftwarec195.TimeConverter;
import wgusoftwarec195.model.Appointment;
import wgusoftwarec195.model.Customer;
import wgusoftwarec195.model.Schedule;

/**
 *
 * @author Dan
 */
public class UserAppViewController {

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
    private TableView<Appointment> appointTable;
    @FXML
    private TableColumn<Appointment, String> appointTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appointDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> appointCustomerColumn;
    @FXML
    private TableColumn<Appointment, String> appointLocationColumn;
    @FXML
    private TableColumn<Appointment, String> appointStartColumn;
    @FXML
    private TableColumn<Appointment, String> appointEndColumn;
    
    @FXML
    private Button exitBtn;
    @FXML
    private Button reportByMonth;
    @FXML
    private Button reportSchedule;
    @FXML
    private Button reportInactive;

    private String sql;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private boolean okClicked = false;
    private Locale locale;
    private MainApp mainApp;
    private Stage dialogStage;
    private int userId;
    private Schedule schedule;
    private String custName;
    private final Date date = new Date();
    private final LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a MM-dd-yyyy");
 
    @FXML
    private void initialize() throws ClassNotFoundException, SQLException {
      
        //all customer table
        custIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        custNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        custAddressOneColumn.setCellValueFactory(cellData -> cellData.getValue().addressOneProperty());
        custAddressTwoColumn.setCellValueFactory(cellData -> cellData.getValue().addressTwoProperty());
        custPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        
        //all appointment table
        appointTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        appointDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        appointCustomerColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        appointLocationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        appointStartColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
        appointEndColumn.setCellValueFactory(cellData -> cellData.getValue().endProperty());
    }
    
    //add new customer button
    @FXML 
    private void handleNewCust(){
        okClicked = mainApp.showAddCustView();
    }
 
    //edit customer button
    @FXML
    private void handleEditCust() {
        //gets selected customer and passes it to addcustomerview controller through mainApp
        Customer selectedCust = custTable.getSelectionModel().getSelectedItem();
        if (selectedCust != null) {
            okClicked = mainApp.showAddCustView(selectedCust);
        }
        else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a Customer to edit.");
            alert.showAndWait();
        }
    }
    
    //delete customer button. gives user confirmation alert before deleting the
    //selected customer from db and model
    @FXML
    private void deleteCustBtn(ActionEvent event) throws ClassNotFoundException, SQLException {
        Customer selectedCust = custTable.getSelectionModel().getSelectedItem();
        if (selectedCust != null) {
        //Confirm Delete
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete...");
        alert.setHeaderText("Would you like to delete Customer " + selectedCust.getCustomerName()+" now ?");
        alert.setContentText("Warning: This will permanently delete " + selectedCust.getCustomerName()+" and any appointments they have scheduled!");        
        alert.showAndWait()
            
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response ->
                            sql = "DELETE FROM customer WHERE customer.customerId="+ selectedCust.getCustomerId()+"");
                            
                            try (Connection conn = DbConnection.createConnection()){
                                //begin sql transaction, if error occurs before all deletions
                                //are successful, then rollback database changes.
                                conn.setAutoCommit(false);
                                ps = conn.prepareStatement(sql);
                                int rows = ps.executeUpdate();
                                //if customer delete success, then delete related address
                                if (rows>0) {
                                    sql = "DELETE FROM address WHERE address.addressId="+ selectedCust.getAddressId() +"";
                                    
                                    ps = conn.prepareStatement(sql);
                                    rows = ps.executeUpdate();
                                    //if address delete success, then delete related appointments
                                    if (rows>0){
                                        sql = "DELETE FROM appointment WHERE appointment.customerId= "+selectedCust.getCustomerId() +"";
                                        
                                        ps = conn.prepareStatement(sql);
                                        rows = ps.executeUpdate();
                                        //if no errors occurred, delete customer object and related appointment
                                        //objects from model and commit sql transaction
                                        if (rows>0){
                                            schedule.deleteCustomer(selectedCust);
                                            
                                            ObservableList<Appointment> removeAppointments = FXCollections.observableArrayList();
                                            
                                            schedule.getAppointmentData().stream().filter((a) -> (a.getCustomerId() == selectedCust.getCustomerId())).forEachOrdered((a) -> {
                                                removeAppointments.add(a);
                                            });
                                            schedule.getAppointmentData().removeAll(removeAppointments);
                                            
                                            conn.commit();
                                        }  
                                    }
                                }
                            } 
                            } else {
        // Nothing selected for deletion alert
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Customer Selected");
        alert.setContentText("Please select a Customer from the table.");

        alert.showAndWait();
        }
    }
    
    //delete appointment button. gives user confirmation before deleting selected
    //appointment from db and model
    @FXML
    private void deleteAppointBtn(ActionEvent event) throws ClassNotFoundException, SQLException {
        Appointment selectedAppointment = appointTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
        //Confirm Delete
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete...");
        alert.setHeaderText("Would you like to delete this Appointment now?");
        alert.setContentText("Warning: This appointment will be permanently deleted from the database.");        
        alert.showAndWait()

                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response ->
                            sql = "DELETE FROM appointment WHERE appointment.appointmentId="+ selectedAppointment.getAppointmendId()+"");

                            try (Connection conn = DbConnection.createConnection()){
                                conn.setAutoCommit(false);
                                ps = conn.prepareStatement(sql);
                                int rows = ps.executeUpdate();
                                //if delete success then delete appointment object from model and commit transaction
                                if (rows>0) {
                                    schedule.deleteAppointment(selectedAppointment);
                                    conn.commit();

                                }
                            }                   
        } else {
        // Nothing selected for deletion
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Appointment Selected");
        alert.setContentText("Please select an Appointment from the table.");

        alert.showAndWait();
        }
    }
    
    //add new appointment button
    @FXML
    public void handleNewAppointment(ActionEvent event){
        okClicked = mainApp.showAddAppointmentView();
    }
    
    //view/edit appointment button. passes selected appointment to addappointmentviewcontroller
    //through overloaded method in mainapp
    @FXML
    public void handleEditAppointment(ActionEvent event) throws ClassNotFoundException, SQLException{

        //gets selected apointment and passes it to overloaded addappointment controller through mainApp
        Appointment selectedAppointment = appointTable.getSelectionModel().getSelectedItem();
        if (selectedAppointment != null) {
            okClicked = mainApp.showAddAppointmentView(selectedAppointment);
        }
        else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please select an Appointment to view/edit.");

            alert.showAndWait();
        }
    }
    
    //adds data from database as objects into our model and populates tableviews
    public void setMainApp(MainApp mainApp, int userId, String userName, Schedule schedule) throws ClassNotFoundException, SQLException {
        this.mainApp = mainApp;
        this.userId = userId;
        this.schedule = schedule;

        try (Connection conn = DbConnection.createConnection()){
            //select customers from customer table and populate model/tableview
            sql = "SELECT customer.customerId, customer.customerName, address.addressId, address.address, address.address2,"
                + " address.postalCode, address.cityId, customer.active, address.phone"
                + " from customer join address on customer.addressId=address.addressId";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            //adds customer objects to schedule
            while (rs.next()) {
                schedule.addCustomer(new Customer(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getInt(7), rs.getInt(8), rs.getString(9)));
            }
            //select appointments from appointment table and populate model/tableview
            sql = "SELECT appointment.appointmentId, appointment.customerId, customer.customerName, appointment.title, appointment.description, appointment.location,"
                    + " appointment.contact, appointment.url, appointment.start, appointment.end, customer.customerName, address.addressId, address.address,"
                    + " address.address2, address.postalCode, address.cityId, customer.active, address.phone FROM appointment INNER JOIN customer ON"
                    + " appointment.customerId = customer.customerId LEFT OUTER JOIN address ON customer.addressId = address.addressId WHERE appointment.createdBy='"+ userName+"'";

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            int i = 0;
            while (rs.next()) {

                //convert appointment start and end timestamps to localdatetime, so they
                //can then be converted from UTC to our systems timezone times, then parsed into
                //readable format to be displayed to user
                LocalDateTime startLocalTime = rs.getTimestamp(9).toLocalDateTime();
                LocalDateTime endLocalTime = rs.getTimestamp(10).toLocalDateTime();

                //place appointment data into schedule model
                schedule.addAppointment(new Appointment(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getString(7), rs.getString(8), TimeConverter.ConvertToLocal(startLocalTime), TimeConverter.ConvertToLocal(endLocalTime)));
                //place the customer that this appointment is for into our customerInAppointment arraylist in appointment
                schedule.getAppointmentData().get(i).getCustomerInAppointmentData().add(new Customer(rs.getInt(2), rs.getString(11), rs.getInt(12), rs.getString(13),
                        rs.getString(14), rs.getString(15), rs.getInt(16), rs.getInt(17), rs.getString(18)));
                i++;
            }
        }
        //populate tableviews
        custTable.setItems(schedule.getCustomerData());
        appointTable.setItems(schedule.getAppointmentData());

        //call loginremindr function here to see if user has appointments that 
        //start within 15 minutes of logging in
        loginReminder();
    }
     
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
     
    public boolean isOkClicked() {
        return okClicked;
    }
    
    public void setCalendarAll(){
        appointTable.setItems(schedule.getAppointmentData());
    }
    
    //"This Month" button finds appointments with month matching current month and displays them in table view
    public void setCalendarMonthly(){
        ObservableList<Appointment> monthly = FXCollections.observableArrayList();
        
        //gets the current month
        int month = localDateTime.getMonthValue();
        
        //adds appointments with month matching current month to monthly arraylist to be displayed in tableview
        schedule.getAppointmentData().stream().filter((a) -> (month == LocalDate.parse(a.getStart(), dtf).getMonthValue())).forEachOrdered((a) -> {
            monthly.add(a);
        });
        
        appointTable.setItems(null);
        appointTable.setItems(monthly);
    }
    
    //"This Week" button finds apointments with week matching current week and displays them in table view
    public void setCalendarWeekly(){
        ObservableList<Appointment> weekly = FXCollections.observableArrayList();
        
        //gets current week
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int week = localDateTime.get(weekFields.weekOfWeekBasedYear());
        
        //adds appointments with week matching current week to weekly arraylist 
        schedule.getAppointmentData().stream().filter((a) -> (week == LocalDateTime.parse(a.getStart(), dtf).get(weekFields.weekOfWeekBasedYear()))).forEachOrdered((a) -> {
            weekly.add(a);
        });

        appointTable.setItems(null);
        appointTable.setItems(weekly);
    }
    
    //alerts user if there is an appointment starting within 15 minutes of log in
    public void loginReminder(){
        schedule.getAppointmentData().forEach((Appointment a) -> {
            long diffMinutes = java.time.Duration.between(LocalDateTime.parse(a.getStart(), dtf), localDateTime).toMinutes();
            if (diffMinutes < 1 && diffMinutes > -16) {
                schedule.getCustomerData().stream().filter((c) -> (a.getCustomerId() == c.getCustomerId())).forEachOrdered((c) -> {
                    UserAppViewController.this.custName = c.getCustomerName();
                });
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.initOwner(mainApp.getPrimaryStage());
                alert.setTitle("Upcoming Appointment!");
                alert.setHeaderText("You have an appointment with "+ custName);
                alert.setContentText("Starting in " + java.lang.Math.abs(diffMinutes) + " minutes.");
                alert.showAndWait();
            }
        });
    }
    @FXML
    public void reportBtn(){
        okClicked = mainApp.showReportGeneratorView();
    }
     
    @FXML
    private void exitApplication(ActionEvent event) {
        System.exit(0);
    }
}

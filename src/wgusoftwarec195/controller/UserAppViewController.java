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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;
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
    private TableColumn<Appointment, Integer> appointIdColumn;
    @FXML
    private TableColumn<Appointment, String> appointTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appointDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> appointLocationColumn;
    @FXML
    private TableColumn<Appointment, String> appointStartColumn;
    @FXML
    private TableColumn<Appointment, String> appointEndColumn;
    
    @FXML
    private Button exitBtn;
    

    private String sql;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private boolean okClicked = false;
    private Locale locale;
    private MainApp mainApp;
    private Stage dialogStage;
    private int userId;
    private Schedule schedule;
    private Appointment appointment;
    
    Date date = new Date();
    private final LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    
    private final ObservableList<Appointment> weekly = FXCollections.observableArrayList();
    private final ObservableList<Appointment> monthly = FXCollections.observableArrayList();

    
    @FXML
    private void initialize() throws ClassNotFoundException, SQLException {
        locale = Locale.getDefault();
      
        custIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        custNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        custAddressOneColumn.setCellValueFactory(cellData -> cellData.getValue().addressOneProperty());
        custAddressTwoColumn.setCellValueFactory(cellData -> cellData.getValue().addressTwoProperty());
        custPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        
        
        appointIdColumn.setCellValueFactory(cellData -> cellData.getValue().appointmentIdProperty().asObject());
        appointTitleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        appointDescriptionColumn.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        appointLocationColumn.setCellValueFactory(cellData -> cellData.getValue().locationProperty());
        appointStartColumn.setCellValueFactory(cellData -> cellData.getValue().startProperty());
        appointEndColumn.setCellValueFactory(cellData -> cellData.getValue().endProperty());
    }
    
    @FXML
    private Button fartBtn;
          
    
    @FXML
    private void fartBtnClick(){
        
        ZoneId timeZoneId = ZoneId.systemDefault(); // my timezone
        ZoneOffset timeZoneOffset = timeZoneId.getRules().getOffset(localDateTime);
        
        System.out.println(localDateTime);
        System.out.println(timeZoneId);
        System.out.println(timeZoneOffset);
        
    }
    
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a Customer to edit.");

            alert.showAndWait();
        }
    }
    
    @FXML
    private void deleteCustBtn(ActionEvent event) throws ClassNotFoundException, SQLException {
        Customer selectedCust = custTable.getSelectionModel().getSelectedItem();
        if (selectedCust != null) {
        //Confirm Delete
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete...");
        alert.setHeaderText("Would you like to delete Customer " + selectedCust.getCustomerName()+" now ?");
        alert.setContentText("Warning: This will permanently delete " + selectedCust.getCustomerName()+" from the database!");        
        alert.showAndWait()
            
                    .filter(response -> response == ButtonType.OK)
                    .ifPresent(response ->
                            sql = "DELETE FROM customer WHERE customer.customerId="+ selectedCust.getCustomerId()+"");
                            
                            try (Connection conn = DbConnection.createConnection()){
                                conn.setAutoCommit(false);
                                ps = conn.prepareStatement(sql);
                                int rows = ps.executeUpdate();

                                if (rows>0) {
                                    sql = "DELETE FROM address WHERE address.addressId="+ selectedCust.getAddressId() +"";
                                    
                                    ps = conn.prepareStatement(sql);
                                    rows = ps.executeUpdate();
                                    if (rows>0){
                                        schedule.deleteCustomer(selectedCust);
                                        conn.commit();
                                        System.out.println("customer deleted!");
                                    }
                                }
                            } 
        } else {
        // Nothing selected.
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("No Selection");
        alert.setHeaderText("No Customer Selected");
        alert.setContentText("Please select a Customer from the table.");

        alert.showAndWait();
        }
    }
    
    @FXML
    public void handleNewAppointment(ActionEvent event){
        okClicked = mainApp.showAddAppointmentView();
    }
    
    @FXML
    public void handleEditAppointment(ActionEvent event){}
    
    public void setMainApp(MainApp mainApp, int userId, Schedule schedule) throws ClassNotFoundException, SQLException {
        this.mainApp = mainApp;
        this.userId = userId;
        this.schedule = schedule;
        
        try (Connection conn = DbConnection.createConnection()){
            sql = "SELECT customer.customerId, customer.customerName, address.addressId, address.address, address.address2,"
                + " address.postalCode, address.cityId, customer.active, address.phone"
                + " from customer join address on customer.addressId=address.addressId";
            
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                //place customer's data from db into model
                schedule.addCustomer(new Customer(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5),
                        rs.getString(6), rs.getInt(7), rs.getInt(8), rs.getString(9)));
            }
            
            sql = "SELECT appointment.appointmentId, appointment.customerId, appointment.title, appointment.description, appointment.location, appointment.contact,"
                    + "appointment.url, appointment.start, appointment.end FROM appointment";
        
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                
                int month = localDateTime.getMonthValue();
                LocalDate localDate = rs.getTimestamp(9).toLocalDateTime().toLocalDate();
                int month2 = localDate.getMonthValue();
                
                if(month == month2){
                    monthly.add(new Appointment(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
                }
                
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                
                int week = localDateTime.get(weekFields.weekOfWeekBasedYear());
                int week2 = localDate.get(weekFields.weekOfWeekBasedYear());
                
                if(week == week2){
                    weekly.add(new Appointment(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
                }

                //place appointment data into model
                schedule.addAppointment(new Appointment(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4),
                        rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)));
            }
        }
        
        
        custTable.setItems(schedule.getCustomerData());
        appointTable.setItems(schedule.getAppointmentData());
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
    
    public void setCalendarMonthly(){
        appointTable.setItems(null);
        appointTable.setItems(monthly);

    }
    
    public void setCalendarWeekly(){
        appointTable.setItems(null);
        appointTable.setItems(weekly);
    }
     
    @FXML
    private void exitApplication(ActionEvent event) {
        System.exit(0);
    }
}

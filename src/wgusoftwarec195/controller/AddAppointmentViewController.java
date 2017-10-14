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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;
import wgusoftwarec195.TimeConverter;
import wgusoftwarec195.exception.SchedulingError;
import wgusoftwarec195.model.Appointment;
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
    private TextField startTimeHour;
    @FXML
    private TextField startTimeMinute;
    @FXML
    private ChoiceBox<String> startAmPm;
    @FXML
    private TextField endTimeHour;
    @FXML
    private TextField endTimeMinute;
    @FXML
    private ChoiceBox<String> endAmPm;
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
    private Button removeCustomerBtn;
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
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a MM-dd-yyyy");
    
    @FXML
    private void initialize() {
        //hardedcoded values for starttime endtime am/pm choiceboxes
        startAmPm.getItems().add("AM");
        startAmPm.getItems().add("PM");
        endAmPm.getItems().add("AM");
        endAmPm.getItems().add("PM");

        //listeners on starthour, startminute, endhour, and endminute textfields to ensure only numbers and a max of 2.
        startTimeHour.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                startTimeHour.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(startTimeHour.getText().length() > 2) {
                String s = startTimeHour.getText().substring(0, 2);
                startTimeHour.setText(s);
            }
        });
        startTimeMinute.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                startTimeMinute.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(startTimeMinute.getText().length() > 2) {
                String s = startTimeMinute.getText().substring(0, 2);
                startTimeMinute.setText(s);
            }
        });
        endTimeHour.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                endTimeHour.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(endTimeHour.getText().length() > 2) {
                String s = endTimeHour.getText().substring(0, 2);
                endTimeHour.setText(s);
            }
        });
        endTimeMinute.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            if (!newValue.matches("\\d*")) {
                endTimeMinute.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if(endTimeMinute.getText().length() > 2) {
                String s = endTimeMinute.getText().substring(0, 2);
                endTimeMinute.setText(s);
            }
        });
        
        //existing customer tableview
        custIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        custNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        custAddressOneColumn.setCellValueFactory(cellData -> cellData.getValue().addressOneProperty());
        custAddressTwoColumn.setCellValueFactory(cellData -> cellData.getValue().addressTwoProperty());
        custPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
        
        //customer in appointment tableview
        custIdColumn1.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());
        custNameColumn1.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
        custAddressOneColumn1.setCellValueFactory(cellData -> cellData.getValue().addressOneProperty());
        custAddressTwoColumn1.setCellValueFactory(cellData -> cellData.getValue().addressTwoProperty());
        custPhoneColumn1.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
    }
        
    //save button inside add appointment window
    @FXML
    public void handleAddAppointBtn(ActionEvent event) throws SQLException, IOException, ClassNotFoundException, SchedulingError{

        //makes sure that new appointment has a related customer
        if(appointment.getCustomerInAppointmentData().size() > 0){

            //try with resources block will auto close our connection resource
            try (Connection conn = DbConnection.createConnection()){

                //begin sql transaction, if any errors occur, the sql transaction 
                //will not commit and roll back any database changes.
                conn.setAutoCommit(false);

                //formats user input from date, starttime, endtime fields and converts
                //into localdatetime objects to be inserted into database.
                DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd a h:mm");
                String startTime = (appointDateField.getValue()+ " "+ startAmPm.getValue()+ " "+ startTimeHour.getText() + ":" + startTimeMinute.getText());
                String endTime = (appointDateField.getValue()+ " "+ endAmPm.getValue()+ " "+ endTimeHour.getText() + ":" + endTimeMinute.getText());
                LocalDateTime appointStart = LocalDateTime.parse(startTime, dtf1);
                LocalDateTime appointEnd = LocalDateTime.parse(endTime, dtf1);

                //gets current timestamp to convert from localtime to utc for the
                //creadedate table in database
                Date now = new Date();
                LocalDateTime ldt = LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault());

                try{
                    //checks if appointment times are valid and available. takes in
                    //the formatted start and end localdatetimes as parameters. throws
                    //schedulingerror custom exception if times are outside of business
                    //hours or overlaps existing appointments
                    checkSchedule(appointStart, appointEnd);

                    //mainapp tells us if user is adding or editing appointment
                    if (newAppoint == true){
                        int custId = appointment.getCustomerInAppointmentData().get(0).getCustomerId();
                        String custName = appointment.getCustomerInAppointmentData().get(0).getCustomerName();
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

                        //if insert success then add appointment object into schedule, commit sql transaction
                        //and close dialog
                        if(rs.next()){
                            int newAppointmentId = rs.getInt(1);
                            appointment.setCustomerId(custId);
                            appointment.setAppointmentId(newAppointmentId);
                            appointment.setCustomerName(custName);
                            appointment.setTitle(appointTitleField.getText());
                            appointment.setDescription(appointDescriptionArea.getText());
                            appointment.setLocation(appointLocationField.getText());
                            appointment.setContact(appointContactField.getText());
                            appointment.setUrl(appointUrlField.getText());

                            String localStartTime = DateTimeFormatter.ofPattern("h:mm a MM-dd-yyyy").format(appointStart);
                            String localEndTime = DateTimeFormatter.ofPattern("h:mm a MM-dd-yyyy").format(appointEnd);
                            appointment.setStart(localStartTime);
                            appointment.setEnd(localEndTime);

                            schedule.addAppointment(appointment);
                            dialogStage.close();
                            conn.commit();
                         }
                    }
                    //this means user is editing existing appointment
                    else{
                        //update existing record
                        sql = "UPDATE appointment SET customerId='"+ appointment.getCustomerInAppointmentData().get(0).getCustomerId()+ "',"
                                + " title='"+appointTitleField.getText() +"', description='"+appointDescriptionArea.getText() +"',"
                                + " location='"+appointLocationField.getText() +"', contact='"+ appointContactField.getText()+"',"
                                + " url='"+ appointUrlField.getText() +"', start='"+ TimeConverter.ConvertToUtc(appointStart) +"',"
                                + " end='"+ TimeConverter.ConvertToUtc(appointEnd) +"', lastUpdateBy='" + userName +"'"
                                + " WHERE appointmentId='"+appointment.getAppointmendId()+"'";
                        ps = conn.prepareStatement(sql);
                        //executeUpdate method does not return resultset but instead returns affected rows as an int.
                        int rows = ps.executeUpdate();
                        //if database update success then update existing appointment object in schedule model,
                        //commit sql transaction and close dialog
                        if(rows > 0){
                            int custId = appointment.getCustomerInAppointmentData().get(0).getCustomerId();
                            String custName = appointment.getCustomerInAppointmentData().get(0).getCustomerName();
                            appointment.setCustomerId(custId);
                            appointment.setCustomerName(custName);
                            appointment.setTitle(appointTitleField.getText());
                            appointment.setDescription(appointDescriptionArea.getText());
                            appointment.setLocation(appointLocationField.getText());
                            appointment.setContact(appointContactField.getText());
                            appointment.setUrl(appointUrlField.getText());                        
                            String localStartTime = DateTimeFormatter.ofPattern("h:mm a MM-dd-yyyy").format(appointStart);
                            String localEndTime = DateTimeFormatter.ofPattern("h:mm a MM-dd-yyyy").format(appointEnd);
                            appointment.setStart(localStartTime);
                            appointment.setEnd(localEndTime);

                            schedule.updateAppointment(appointment);
                            dialogStage.close();
                            conn.commit();
                        }
                    }
                }
                catch(SchedulingError e){
                    System.out.println(e);
                }
            }
        }
        //alert telling user that there must be one customer added to appointment
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No customers!");
            alert.setHeaderText("No Customer added to appointment!");
            alert.setContentText("Please add a customer by selecting one from the all customer table and then by clicking the"
                    + " Add Customer button to schedule an appointment.");
            alert.showAndWait();
        }
    }   
    
    //add appointmentview cancel button
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
                    + " main screen without editing this Appointment?");      
            alert.showAndWait()

                        .filter(response -> response == ButtonType.OK)
                        .ifPresent(response -> dialogStage.close());
        }
    }
    
    //sets the selected customer object from the all customer table into the associated customer table.
    //and also adds it to the customerInAppointment array inside our appointment object
    @FXML
    public void addCustomerBtnClick(){
        Customer selectedCustomer = custTable.getSelectionModel().getSelectedItem();
        if(appointment.getCustomerInAppointmentData().size() > 0){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Too many customers!");
            alert.setHeaderText("Only one Customer per appointment!");
            alert.setContentText("Please remove a customer from associated customers before adding another.");
            alert.showAndWait();
        }
        else if(selectedCustomer != null){
            appointment.getCustomerInAppointmentData().add(selectedCustomer);
            addedCustTable.setItems(appointment.getCustomerInAppointmentData());
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Customer is selected");
            alert.setContentText("Please select a Customer from the top table.");
            alert.showAndWait();
        }
    }
    
    //removes selected added customer from associated customer table and also
    //removes selected customer object from customerInAppointment array
    @FXML
    public void removeCustomerBtnClick(){
        Customer selectedCustomer = addedCustTable.getSelectionModel().getSelectedItem();
        if(selectedCustomer != null){
            appointment.removeCustomerInAppointment(selectedCustomer);
            addedCustTable.setItems(appointment.getCustomerInAppointmentData());
        }
    }
    
    //appointment that was selected for view/edit is from userappview through mainapp, and then
    //fills textfields and tables with existing appointment data for viewing/editing
    public void setAppoint(Appointment appointment) throws ClassNotFoundException, SQLException{
        try{
            this.appointment = appointment;
            this.addAppointLabel.setText("View/Edit Appointment");
            appointTitleField.setText(appointment.getTitle());
            appointDescriptionArea.setText(appointment.getDescription());
            appointLocationField.setText(appointment.getLocation());
            appointContactField.setText(appointment.getContact());
            appointUrlField.setText(appointment.getUrl());

            DateTimeFormatter dtf0 = DateTimeFormatter.ofPattern("h:mm a MM-dd-yyyy");
            DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            String startString = appointment.getStart();
            String endString = appointment.getEnd();

            LocalDateTime localDateStart = LocalDateTime.parse(startString, dtf0);
            LocalDateTime localDateEnd = LocalDateTime.parse(endString, dtf0);

            String startDay = DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDateStart);
            String startTimeHours = DateTimeFormatter.ofPattern("hh").format(localDateStart);
            String startTimeMinutes = DateTimeFormatter.ofPattern("mm").format(localDateStart);
            String startAmPms = DateTimeFormatter.ofPattern("a").format(localDateStart);
            String endTimeHours = DateTimeFormatter.ofPattern("hh").format(localDateEnd);
            String endTimeMinutes = DateTimeFormatter.ofPattern("mm").format(localDateEnd);
            String endAmPms = DateTimeFormatter.ofPattern("a").format(localDateEnd);

            LocalDate ld = LocalDate.parse(startDay, dtf1);

            appointDateField.setValue(ld);
            startTimeHour.setText(startTimeHours);
            startTimeMinute.setText(startTimeMinutes);
            endTimeHour.setText(endTimeHours);
            endTimeMinute.setText(endTimeMinutes);

            startAmPm.setValue(startAmPms);
            endAmPm.setValue(endAmPms);

            addedCustTable.setItems(appointment.getCustomerInAppointmentData());
        }catch(Exception e){
            System.err.println(e);
            throw e;
        }
    }
    
    //main app tells us if user is adding new or editing existing appointment
    public void setNewAppoint(boolean newAppoint){
        this.newAppoint = newAppoint;
    }
    
    public void setMainApp(MainApp mainApp, int userId, String userName, Schedule schedule, Appointment appointment) {
        this.mainApp = mainApp;
        this.userId = userId;
        this.userName = userName;
        this.schedule = schedule;
        this.appointment = appointment;
        
        custTable.setItems(schedule.getCustomerData());
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
    
    /*
    checked exception looks to see if the date and time of appointment that user is attempting to schedule
    is valid, is within business hours, on a business day, and does not overlap other scheduled appointments
    */
    public void checkSchedule(LocalDateTime appointStart, LocalDateTime appointEnd) throws SchedulingError{
        
        //gets time of day from our new appointment localdatetimes
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("h:mm a");
        String appointStartTime = tf.format(appointStart);
        String appointEndTime = tf.format(appointEnd);

        //gets day of the week from new appointment time
        DayOfWeek startDay = appointStart.getDayOfWeek();
        DayOfWeek endDay = appointEnd.getDayOfWeek();
        
        //hardcoded business hours to be compared with new appointment times
        String businessHourStart = "9:00 AM";
        String businessHourEnd = "5:00 PM";

        
        //checks if new appointment times is valid
        if(appointStart.isAfter(appointEnd)){
            throw new SchedulingError("Invalid Time! Appointment cannot start before it ends!");
        }
        
        //checks if new appointment times are on business days Monday - Friday
        if(startDay == DayOfWeek.SATURDAY || startDay == DayOfWeek.SUNDAY){
            throw new SchedulingError("Outside of Business hours! Business hours are Monday-Friday, 9am-5pm");

        }
        if(endDay == DayOfWeek.SATURDAY || endDay == DayOfWeek.SUNDAY){
            throw new SchedulingError("Outside of Business hours! Business hours are Monday-Friday, 9am-5pm");

        }
        
        //checks if new appointment times are within business hours 9am-5pm
        if(LocalTime.parse(appointStartTime, tf).isBefore(LocalTime.parse(businessHourStart, tf)) ||
                LocalTime.parse(appointStartTime, tf).isAfter(LocalTime.parse(businessHourEnd, tf))){
            throw new SchedulingError("Outside of Business hours! Business hours are Monday-Friday, 9am-5pm");
        }
        if(LocalTime.parse(appointEndTime, tf).isBefore(LocalTime.parse(businessHourStart, tf)) ||
                LocalTime.parse(appointEndTime, tf).isAfter(LocalTime.parse(businessHourEnd, tf))){
            throw new SchedulingError("Outside of Business hours! Business hours are Monday-Friday, 9am-5pm");
        }
        
        //checks if new appointment times do not overlap with existing appointment times
        for(Appointment a: schedule.getAppointmentData()){
            
            //if new appointment starts during an existing appointment
            if(appointStart.isAfter(LocalDateTime.parse(a.getStart(), dtf)) | appointStart.isEqual(LocalDateTime.parse(a.getStart(), dtf))
                    && appointStart.isBefore(LocalDateTime.parse(a.getEnd(), dtf))){
                throw new SchedulingError("This appointment would start during another! Please enter a new time.");
            }
            //if new appointment ends during an existing appointment
            if(appointEnd.isAfter(LocalDateTime.parse(a.getStart(), dtf)) && appointEnd.isBefore(LocalDateTime.parse(a.getEnd(), dtf)) |
                    appointEnd.isEqual(LocalDateTime.parse(a.getEnd(), dtf))){
                throw new SchedulingError("Another appointment would start before this one ends! Please enter a new time.");
            }      
            //if new appointment starts before and ends after an existing appointment
            if(appointStart.isBefore(LocalDateTime.parse(a.getStart(), dtf)) | appointStart.isEqual(LocalDateTime.parse(a.getStart(), dtf)) &&
                    appointEnd.isAfter(LocalDateTime.parse(a.getEnd(), dtf)) | appointEnd.isEqual(LocalDateTime.parse(a.getEnd(), dtf))){
                throw new SchedulingError("Another appointment takes place during this time! Please enter a new time.");
            }
        }
    }
}
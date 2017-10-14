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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;
import wgusoftwarec195.exception.CustomerInfoError;
import wgusoftwarec195.model.Customer;
import wgusoftwarec195.model.City;
import wgusoftwarec195.model.Schedule;
/**
 *
 * @author Dan
 */
public class AddCustomerViewController {
    
    @FXML
    private Label addCustLabel;
    @FXML
    private Label custNameLabel;
    @FXML
    private Label custPhoneLabel;
    @FXML
    private Label custAddress1Label;
    @FXML
    private Label custAddress2Label;
    @FXML
    private Label custPostalCodeLabel;
    @FXML
    private Label custCityLabel;
    @FXML
    private TextField custNameField;
    @FXML
    private TextField custPhoneField;
    @FXML
    private TextField custAddressOneField;
    @FXML
    private TextField custAddressTwoField;
    @FXML
    private TextField custPostalCodeField;
    @FXML
    private ComboBox<City> custCityComboBox;
    @FXML
    private CheckBox custActiveCheckBox;
    @FXML
    private Button addCustBtn;
    @FXML
    private Button cancelBtn;
    @FXML
    private Button testBtn;
    
    private MainApp mainApp;
    private Stage dialogStage;
    private final boolean okClicked = false;
    private boolean newCust;
    private String sql;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private int userId;
    private String userName;
    private int custActive;
    private Customer customer;
    private Schedule schedule;
    private final ObservableList<City> cityData = FXCollections.observableArrayList();
    
    @FXML
    private void initialize() throws SQLException, ClassNotFoundException{

        //gets all cities from city table in db and populates combobox with them
        //so the user can choose a city when adding or editing customers.
        //the cities available for choosing are cities where we have offices.
        //"New York, London, and Phoenix"
        sql = "SELECT city.cityId, city.city, city.countryId from city";
        try(Connection conn = DbConnection.createConnection()){
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                cityData.add(new City(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
        }
        custCityComboBox.setItems(cityData);

        //custom cellfactory for combobox will display city name for each city in citydata observablelist
        custCityComboBox.setCellFactory((ListView<City> p) -> new ListCell<City>() { 
            @Override
            protected void updateItem(City t, boolean bln) {
                super.updateItem(t, bln);
                if(t != null){
                    setText(t.cityProperty().getValue());
                } else {
                    setText(null);
                }
            }
        });
        //displays the selected city name in the combobox.
        custCityComboBox.setButtonCell(new ListCell<City>() {
            @Override
            protected void updateItem(City t, boolean bln) {
                super.updateItem(t, bln);
                if (t != null) {
                    setText(t.cityProperty().getValue());
                } else {
                    setText(null);
                }
            }
        });
    }
    
    //save button inside add new customer window
    @FXML
    public void handleAddCustBtn(ActionEvent event) throws SQLException, IOException, ClassNotFoundException, CustomerInfoError{
        //validates user input. throws CustomerInfoError
        checkCustomer();
        //everything related to adding or updating customer is performed in this try-with-resources block
        //so that if any errors occur, the sql transaction will not commit and roll back any database changes.
        try (Connection conn = DbConnection.createConnection()){

            //begin sql transaction
            conn.setAutoCommit(false);

            //gets selected city id from combobox
            int selectedCity = custCityComboBox.getSelectionModel().getSelectedItem().getCityId();

            //correctly formatted datetime to be inserted into createdate and lastupdateby columns
            java.util.Date dt = new java.util.Date();
            java.text.SimpleDateFormat sdf = 
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = sdf.format(dt);

            //convert active checkbox value to int
            if(custActiveCheckBox.isSelected() == true){
                this.custActive = 1;
            }
            else{
                this.custActive = 0;
            }

            //mainapp tells us if user is adding or editing a customer
            if (newCust == true){
                //insert new record into address table first to retrieve address id for customer table
                sql = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) VALUES "
                        + "('"+ custAddressOneField.getText() +"', '"+ custAddressTwoField.getText()+"', '"+ selectedCity +"',"
                        + "'"+ custPostalCodeField.getText() +"', '"+ custPhoneField.getText() +"', '"+ currentTime +"'"
                        + ", '" + userName +"', '" + userName +"')";
                //returns the auto_incremented addressId to be put into customer table
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                //executeUpdate method does not return resultset but instead returns affected rows as an int.
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();

                //if insert address success, then insert new customer into customer table
                if (rs.next()){
                    int newAddressId = rs.getInt(1);
                    sql = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) VALUES ('"+ custNameField.getText()
                            +"', '"+ newAddressId +"', "+ custActive+", '"+ currentTime +"'"
                    + ", '" + userName +"', '" + userName +"')";

                    ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.executeUpdate();
                    rs = ps.getGeneratedKeys();
                    //if insert success then add customer object into schedule, commit
                    //transaction and close dialog
                    if(rs.next()){
                        int newCustomerId = rs.getInt(1);
                        customer = new Customer();
                        customer.setCustomerId(newCustomerId);
                        customer.setCustomerName(custNameField.getText());
                        customer.setAddressId(newAddressId);
                        customer.setActive(custActive);
                        customer.setPhone(custPhoneField.getText());
                        customer.setAddressOne(custAddressOneField.getText());
                        customer.setAddressTwo(custAddressTwoField.getText());
                        customer.setCityId(selectedCity);
                        customer.setPostalCode(custPostalCodeField.getText());

                        schedule.addCustomer(customer);

                        dialogStage.close();
                        conn.commit();
                    }
                }
            }
            //this means user is updating an existing customer
            else{
                sql = "UPDATE customer SET customerName='"+ custNameField.getText() +"', active="+ custActive +","
                        + " lastUpdateBy='"+ userName +"' WHERE customerId='"+ customer.getCustomerId() +"'";

                ps = conn.prepareStatement(sql);
                //executeUpdate method does not return resultset but instead returns affected rows as an int.
                int rows = ps.executeUpdate();
                //if update success then update the related address record
                if(rows > 0){
                    sql = "UPDATE address SET address='"+ custAddressOneField.getText()+"', address2='"+ custAddressTwoField.getText() +"', cityId='"+ selectedCity +"',"
                            + " postalCode='"+ custPostalCodeField.getText()+"', phone='"+ custPhoneField.getText() +"', lastUpdateBy='"+ userName +"'"
                            + " WHERE address.addressId='"+ customer.getAddressId()+"'";

                    ps = conn.prepareStatement(sql);
                    rows = ps.executeUpdate();
                    //if update success then update existing customer object, commit
                    //sql transaction and close dialog
                    if(rows > 0){
                        customer.setCustomerName(custNameField.getText());
                        customer.setActive(custActive);
                        customer.setPhone(custPhoneField.getText());
                        customer.setAddressOne(custAddressOneField.getText());
                        customer.setAddressTwo(custAddressTwoField.getText());
                        customer.setCityId(selectedCity);
                        customer.setPostalCode(custPostalCodeField.getText());

                        schedule.updateCustomer(customer);

                        dialogStage.close();
                        conn.commit();
                    }
                }
            }
        }
    }
    
    //cancel button in add customer window
    @FXML
    public void addCustCancelBtn(ActionEvent event){
        if(newCust == true){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("This Customer will not be saved.");
        alert.setContentText("Are you sure you would like to cancel and go back to"
                + " main screen without saving this Customer?");
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
    
    //main app tells us if user is adding new or editing existing customer
    public void setNewCust(boolean newCust){
        this.newCust = newCust;
    }

    //selected customer object is passed through mainapp, from userappview, and then
    //fills textfields and tables with existing customer data for viewing/editing
    public void setCustomer(Customer customer){
        this.customer = customer;
        this.addCustLabel.setText("Edit Customer");
        custNameField.setText(customer.getCustomerName());
        custPhoneField.setText(customer.getPhone());
        custAddressOneField.setText(customer.getAddressOne());
        custAddressTwoField.setText(customer.getAddressTwo());
        custPostalCodeField.setText(customer.getPostalCode());
        
        //finds existing customer's city and sets it in combobox selection
        cityData.stream().filter((c) -> (c.getCityId() == customer.getCityId())).forEachOrdered((c) -> {
            custCityComboBox.setValue(c);
        });
        //determines if existing customer is active or not
        if(customer.activeProperty().getValue() == 1){
            custActiveCheckBox.setSelected(true);
        }
    }
    
    public void setMainApp(MainApp mainApp, int userId, String userName, Schedule schedule) {
        this.mainApp = mainApp;
        this.userId = userId;
        this.userName = userName;
        this.schedule = schedule;
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    
    //checks if customer input is valid
    public void checkCustomer() throws CustomerInfoError{
        if(custNameField.getText().isEmpty() || custNameField.getText() == null || !custNameField.getText().matches("^[\\p{L} .'-]+$")){
            custNameLabel.setTextFill(Color.web("red"));
            throw new CustomerInfoError("Customer Name must not be empty and contain only letters.");
        }
        if(custPhoneField.getText().isEmpty() || custPhoneField.getText() == null || !custPhoneField.getText().matches("\\d+")){
            custPhoneLabel.setTextFill(Color.web("red"));
            throw new CustomerInfoError("Customer Phone must not be empty and contain only digits.");
        }
        //address 1 and 2 cannot be null in database but I insert an empty string for address 2 incase customer has only 1 address
        if(custAddressOneField.getText().isEmpty() || custAddressOneField.getText() == null){
            custAddress1Label.setTextFill(Color.web("red"));
            throw new CustomerInfoError("Address 1 must not be empty.");
        }
        
        if(custPostalCodeField.getText().isEmpty() || custPostalCodeField.getText() == null || !custPostalCodeField.getText().matches("\\d+")){
            custPostalCodeLabel.setTextFill(Color.web("red"));
            throw new CustomerInfoError("Postal Code must not be empty and contain only digits.");
        }
        if(custCityComboBox.getSelectionModel().isEmpty() || custCityComboBox.getSelectionModel() == null){
            custCityLabel.setTextFill(Color.web("red"));
            throw new CustomerInfoError("Please select a City in the dropdown box.");
        }    
    }
}

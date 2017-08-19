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
import javafx.stage.Stage;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;
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

        //gets all cities from city table and populates combobox with them
        //so the user can choose a customer's city when adding or editing customers
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
        //displays city name when a city is selected in combobox.
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
    public void handleAddCustBtn(ActionEvent event) throws SQLException, IOException, ClassNotFoundException{  

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
            
            if (newCust == true){
                //insert new record into address table first to retrieve address id for customer table
                sql = "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdateBy) VALUES "
                        + "('"+ custAddressOneField.getText() +"', '"+ custAddressTwoField.getText()+"', '"+ selectedCity +"',"
                        + "'"+ custPostalCodeField.getText() +"', '"+ custPhoneField.getText() +"', '"+ currentTime +"'"
                        + ", '" + userName +"', '" + userName +"')";
                //returns the auto_incremented addressId
                ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                //executeUpdate method does not return resultset but instead returns affected rows as an int.
                ps.executeUpdate();
                rs = ps.getGeneratedKeys();

                //insert new record into customer table
                if (rs.next()){
                    int newAddressId = rs.getInt(1);
                    sql = "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdateBy) VALUES ('"+ custNameField.getText()
                            +"', '"+ newAddressId +"', "+ custActive+", '"+ currentTime +"'"
                    + ", '" + userName +"', '" + userName +"')";

                    ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    ps.executeUpdate();
                    rs = ps.getGeneratedKeys();
                    //add customer window
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
                        
                        System.out.println("customer added!");
                        dialogStage.close();
                        conn.commit();
                    }
                }
            }
            else{
                //update existing customer record
                sql = "UPDATE customer SET customerName='"+ custNameField.getText() +"', active="+ custActive +","
                        + " lastUpdateBy='"+ userName +"' WHERE customerId='"+ customer.getCustomerId() +"'";

                ps = conn.prepareStatement(sql);
                
                /* 
                    executeUpdate method does not return resultset but instead 
                    returns affected rows as an int. no errors have occured and 
                    if any rows affected then commit sql transaction, update model
                    and close the add customer window
                */
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
            }
        }
    }
    
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
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    //main app tells us if user is adding new or editing existing customer
    public void setNewCust(boolean newCust){
        this.newCust = newCust;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
    
    //fills textfields with existing customer data 
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
    
}

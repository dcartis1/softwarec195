/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Dan
 */
public class Customer {
    private final IntegerProperty customerId;
    private final StringProperty customerName;
    private final IntegerProperty addressId;
    private final StringProperty addressOne;
    private final StringProperty addressTwo;
    private final StringProperty postalCode;
    private final IntegerProperty cityId;
    private final IntegerProperty active;
    private final StringProperty phone;
    
    
    public Customer(){
        this.customerId = new SimpleIntegerProperty(0);
        this.customerName = new SimpleStringProperty("");
        this.addressId = new SimpleIntegerProperty(0);
        this.addressOne = new SimpleStringProperty("");
        this.addressTwo = new SimpleStringProperty("");
        this.postalCode = new SimpleStringProperty("");
        this.cityId = new SimpleIntegerProperty(0);
        this.active = new SimpleIntegerProperty(0);
        this.phone = new SimpleStringProperty("");
    }
    
    public Customer(int customerId, String customerName, int addressId, String addressOne, String addressTwo, String postalCode, int cityId, int active, String phone){      
        this.customerId = new SimpleIntegerProperty(customerId);
        this.customerName = new SimpleStringProperty(customerName);
        this.addressId = new SimpleIntegerProperty(addressId);
        this.addressOne = new SimpleStringProperty(addressOne);
        this.addressTwo = new SimpleStringProperty(addressTwo);
        this.postalCode = new SimpleStringProperty(postalCode);
        this.cityId = new SimpleIntegerProperty(cityId);
        this.active = new SimpleIntegerProperty(active);
        this.phone = new SimpleStringProperty(phone);
    }
    
    public int getCustomerId(){
        return customerId.get();
    }
    
    public void setCustomerId(int customerId){
        this.customerId.set(customerId);
    }
    
    public IntegerProperty customerIdProperty(){
        return customerId;
    }

    public String getCustomerName(){
        return customerName.get();
    }

    public void setCustomerName(String customerName) {
        this.customerName.set(customerName);
    }
    
    public StringProperty customerNameProperty(){
        return customerName;
    }
    
    public int getAddressId(){
        return addressId.get();
    }
    
    public void setAddressId(int addressId){
        this.addressId.set(addressId);
    }
    
    public IntegerProperty addressIdProperty(){
        return addressId;
    }
    
    public String getAddressOne(){
        return addressOne.get();
    }

    public void setAddressOne(String addressOne) {
        this.addressOne.set(addressOne);
    }
    
    public StringProperty addressOneProperty(){
        return addressOne;
    }
    
    public String getAddressTwo(){
        return addressTwo.get();
    }

    public void setAddressTwo(String addressTwo) {
        this.addressTwo.set(addressTwo);
    }
    
    public StringProperty addressTwoProperty(){
        return addressTwo;
    }
    
    public String getPostalCode(){
        return postalCode.get();
    }

    public void setPostalCode(String postalCode) {
        this.postalCode.set(postalCode);
    }
    
    public StringProperty postalCodeProperty(){
        return postalCode;
    }
    
    public int getCityId(){
        return cityId.get();
    }
    
    public void setCityId(int cityId){
        this.cityId.set(cityId);
    }
    
    public IntegerProperty cityIdProperty(){
        return cityId;
    }
    
    public int getActive(){
        return active.get();
    }
    
    public void setActive(int active){
        this.active.set(active);
    }
    
    public IntegerProperty activeProperty(){
        return active;
    }
    
    public String getPhone(){
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    
    public StringProperty phoneProperty(){
        return phone;
    }
}

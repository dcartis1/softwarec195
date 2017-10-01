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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Dan
 */
public class Appointment {
    
    private final IntegerProperty appointmentId;
    private final IntegerProperty customerId;
    private final StringProperty title;
    private final StringProperty description;
    private final StringProperty location;
    private final StringProperty contact;
    private final StringProperty url;
    private final StringProperty start;
    private final StringProperty end;
    
    private final ObservableList<Customer> customerInAppointment = FXCollections.observableArrayList();
    
    public Appointment(){
        
        this.appointmentId = new SimpleIntegerProperty(0);
        this.customerId = new SimpleIntegerProperty(0);
        this.title = new SimpleStringProperty("");
        this.description = new SimpleStringProperty("");
        this.location = new SimpleStringProperty("");
        this.contact = new SimpleStringProperty("");
        this.url = new SimpleStringProperty("");
        this.start = new SimpleStringProperty("");
        this.end = new SimpleStringProperty("");
    }
    
    public Appointment(int appointmentId, int customerId, String title, String description, String location, String contact, String url,
            String start, String end){
        
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.url = new SimpleStringProperty(url);
        this.start = new SimpleStringProperty(start);
        this.end = new SimpleStringProperty(end);
    }
    
    public int getAppointmendId(){
        return appointmentId.get();
    }
    
    public void setAppointmentId(int appointmentId){
        this.appointmentId.set(appointmentId);
    }
    
    public IntegerProperty appointmentIdProperty(){
        return appointmentId;
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
    
    public String getTitle(){
        return title.get();
    }
    
    public void setTitle(String title){
        this.title.set(title);
    }
    
    public StringProperty titleProperty(){
        return title;
    }
    
    public String getDescription(){
        return description.get();
    }
    
    public void setDescription(String description){
        this.description.set(description);
    }
    
    public StringProperty descriptionProperty(){
        return description;
    }
    
    public String getLocation(){
        return location.get();
    }
    
    public void setLocation(String location){
        this.location.set(location);
    }
    
    public StringProperty locationProperty(){
        return location;
    }
    
    public String getContact(){
        return contact.get();
    }
    
    public void setContact(String contact){
        this.contact.set(contact);
    }
    
    public StringProperty contactProperty(){
        return contact;
    }
    
    public String getUrl(){
        return url.get();
    }
    
    public void setUrl(String url){
        this.url.set(url);
    }
    
    public StringProperty urlProperty(){
        return url;
    }
    
    public String getStart(){
        return start.get();
    }
    
    public void setStart(String start){
        this.start.set(start);
    }
    
    public StringProperty startProperty(){
        return start;
    }
    
    public String getEnd(){
        return end.get();
    }
    
    public void setEnd(String end){
        this.end.set(end);
    }
    
    public StringProperty endProperty(){
        return end;
    }

    public ObservableList<Customer> getCustomerInAppointmentData() {
        return customerInAppointment;
    }
    
    public void addCustomerInAppointment(Customer customer){
        customerInAppointment.add(customer);
    }
    
    public void removeCustomerInAppointment(Customer selectedCustomer){
        customerInAppointment.remove(selectedCustomer);
    }
}

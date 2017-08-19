/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195.model;

import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Dan
 */
public class Schedule {
    
    private final ObservableList<Appointment> appointmentData;
    private final ObservableList<Customer> customerData;

    public Schedule() {
        this.appointmentData = FXCollections.observableArrayList();
        this.customerData = FXCollections.observableArrayList();
    }

    public Schedule(ObservableList<Appointment> appointmentData, ObservableList<Customer> customerData) {
        this.appointmentData = appointmentData;
        this.customerData = customerData;
    }

    public ObservableList<Appointment> getAppointmentData() {
        return appointmentData;
    }
    
    /*
    public ObservableList<Appointment> getAppointmentByMonth(int Month){
        for(Appointment a: appointmentData){
            if(a.getStart(). == selectedId){
            productData.set(i, product);
            }
            i++;

        }
    
    }
        
        return appointmentData;
    }*/
    
    public ObservableList<Customer> getCustomerData() {
        return customerData;
    }
    
    public void addAppointment(Appointment appointment){
        appointmentData.add(appointment);
    }
    
    public void addCustomer(Customer customer){
        customerData.add(customer);
    }
    
    //lambda expression for updating a customer
    public void updateCustomer(Customer customer){
        int i = customer.getCustomerId();
        customerData.stream().filter((c) -> (c.getCustomerId() == i)).forEachOrdered((c) -> {
            customerData.set(customerData.indexOf(c), customer);
        });
    }
    
    public void deleteCustomer(Customer customer) {
        getCustomerData().remove(customer);
    }

}
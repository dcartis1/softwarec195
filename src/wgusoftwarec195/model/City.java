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
public class City {
    
    private final IntegerProperty cityId;
    private final StringProperty city;
    private final IntegerProperty countryId;
    
    public City(){
        this.cityId = new SimpleIntegerProperty(0);
        this.city = new SimpleStringProperty("");
        this.countryId = new SimpleIntegerProperty(0);
    }
    
    public City(int cityId, String city, int countryId){
        this.cityId = new SimpleIntegerProperty(cityId);
        this.city = new SimpleStringProperty(city);
        this.countryId = new SimpleIntegerProperty(countryId);
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
    
    public String getCity(){
        return city.get();
    }
    
    public void setCity(String city){
        this.city.set(city);
    }
    
    public StringProperty cityProperty(){
        return city;
    }
    
    public int getCountryId(){
        return countryId.get();
    }
    
    public void setCountryId(int countryId){
        this.countryId.set(countryId);
    }
    
    public IntegerProperty countryIdProperty(){
        return countryId;
    }
    
}

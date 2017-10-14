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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import wgusoftwarec195.DbConnection;
import wgusoftwarec195.MainApp;
/**
 *
 * @author Dan
 * 
 */
 // each button will pass a sql statement to getReportData() which creates our
 // tableview dynamically
public class ReportGeneratorController {
    
    @FXML
    private TableView reportTable;
    @FXML
    private Label reportLabel;
    
    
    private MainApp mainApp;
    private Stage dialogStage;
    private final boolean okClicked = false;
    private String sql;
    private PreparedStatement ps = null;
    private ResultSet rs = null;
    private final ObservableList<ObservableList> data = FXCollections.observableArrayList();
    
    //appointment types by month button
    @FXML
    public void appointTypeBtn() throws ClassNotFoundException, SQLException{
        try{
            this.sql = "SELECT MONTHNAME(start) AS 'Month', title AS 'Title', count(title) AS 'Count' FROM appointment GROUP BY Title, MonthName(start)";
            getReportData(sql);
            reportLabel.setText("Appoint Types by Month");
        }catch(ClassNotFoundException | SQLException e){
            System.err.println(e);
            throw e;
        }
    }

    //inactive customers button
    @FXML
    public void reportInactiveBtn() throws ClassNotFoundException, SQLException{
        try{
            this.sql = "SELECT customer.customerId AS 'Id', customer.customerName AS 'Name', address.address AS 'Address', address.phone AS 'Phone #'"
                    + " FROM customer JOIN address ON customer.addressId = address.addressId WHERE customer.active = 0";
            getReportData(sql);
            reportLabel.setText("Inactive Customers");
        }catch(ClassNotFoundException | SQLException e){
            System.err.println(e);
            throw e;
        }
    }
    
    //schedules button
    @FXML
    public void reportScheduleBtn() throws ClassNotFoundException, SQLException{
        try{
            this.sql = "SELECT appointment.createdBy AS 'Consultant', appointment.title as 'Title', customer.customerName AS 'Customer', appointment.start AS 'When' FROM"
                    + " appointment JOIN customer ON appointment.customerId = customer.customerId ORDER BY appointment.createdBy";
            getReportData(sql);
            reportLabel.setText("Consultant Schedules");
        }catch(ClassNotFoundException | SQLException e){
            System.err.println(e);
            throw e;
        }
    }

    //executes the sql strings that were passed from button click, then build tableview
    //dynamically according to the data that is returned.
    @FXML
    public void getReportData(String sql) throws ClassNotFoundException, SQLException{
        try{
            //reset tableview
            data.clear();
            reportTable.getColumns().clear();

            try (Connection conn = DbConnection.createConnection()){
                //prepares and executes the sql string passed from button
                ps = conn.prepareStatement(sql);
                rs = ps.executeQuery();

                //adds table columns dynamically
                for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                    final int j = i;
                    TableColumn col = new TableColumn(ps.getMetaData().getColumnLabel(i+1));
                    col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>(){
                        @Override
                        public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());
                        }
                    });
                    reportTable.getColumns().addAll(col);
                }

                //iterate rows and columns, add data to observablelist, then set into tableview
                while(rs.next()){
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                        row.add(rs.getString(i));
                    }
                    data.add(row);
                }
                reportTable.setItems(data);
            }
         }catch(ClassNotFoundException | SQLException e){
            System.err.println(e);
            throw e;
        }
    }
    
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
    
    public void setMainApp(MainApp mainApp){
        this.mainApp = mainApp;
    }
    
}

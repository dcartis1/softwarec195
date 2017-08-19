/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wgusoftwarec195;


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author Dan
 */
public class DbConnection {
    
    private static Connection conn = null;

    public static Connection createConnection() throws ClassNotFoundException {
        String driver = "com.mysql.jdbc.Driver";
        String db = "U03w0x";
        String url = "jdbc:mysql://52.206.157.109/" + db;
        String user = "U03w0x";
        String pass = "53688102969";
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url,user,pass);
            return conn;
        } catch (SQLException e) {
            System.out.println("SQLException: "+e.getMessage());
            System.out.println("SQLState: "+e.getSQLState());
            System.out.println("VendorError: "+e.getErrorCode());
            return null;
        }
    }
}
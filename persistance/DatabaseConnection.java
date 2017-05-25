
package mafia.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class DatabaseConnection {
    
    Connection connection = null;
    PreparedStatement statement = null;
    
    
    public DatabaseConnection(){
        try {
            Class.forName("org.apache.derby.jdbc.ClientDataSource40");   
            connection = DriverManager.getConnection("****URL HERE****");
        } catch(ClassNotFoundException ex) {   
            System.out.println("Could not find database driver");
        } catch(SQLException ex) {
            System.out.println("Could not connect to database");            
        }
    }
    
    
    public int validate(String username, String password) {
        return 0;
    }
}

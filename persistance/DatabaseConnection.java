
package mafia.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DatabaseConnection {
    
    Connection connection;
    PreparedStatement validationStatement;
    PreparedStatement registrationStatement;
    PreparedStatement verificationStatement;
    ResultSet result;
    
    
    public DatabaseConnection(){
        String query;
        this.connection = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDataSource40");   
            connection = DriverManager.getConnection("****URL HERE****");
            
            query = "SELECT * FROM USERS "
                    + "WHERE username = '?' "
                    + "AND password = '?'";            
            validationStatement = connection.prepareStatement(query);
            
            query = "INSERT INTO USERS (username, password, score) "
                    + "VALUES ('?', '?', 0)";
            registrationStatement = connection.prepareStatement(query);
            
            query = "SELECT * FROM USERS "
                    + "WHERE username = '?'";
            verificationStatement = connection.prepareStatement(query);
        } catch(ClassNotFoundException ex) {   
            System.out.println("Could not find database driver");
        } catch(SQLException ex) {
            System.out.println("Could not connect to database");            
        }
    }
    
    
    public int validateCredentials(String username, String password) {
        return 0;
    }
    
    public int registerUser(String username, String password) {
        return 0;
    }
    
    public boolean checkIfUserExists(String username){
        return false;
    }
    
    public void close() {
        try {
            if(connection != null) {
                connection.close();
            }
            if(validationStatement != null) {
                validationStatement.close();
            }
            if(registrationStatement != null) {
                registrationStatement.close();
            }
        } catch(SQLException ex) {
            System.out.println("Could not close database connection");            
        }
    }
}

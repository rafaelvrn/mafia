
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
    PreparedStatement updateStatement;
    ResultSet result;
    
    
    public DatabaseConnection(){
        String query;
        this.connection = null;
        try {
            Class.forName("org.apache.derby.jdbc.ClientDataSource40");   
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/mafiaDB;create=true");
            
            query = "SELECT * FROM USERS "
                    + "WHERE username = ? "
                    + "AND password = ?";            
            validationStatement = connection.prepareStatement(query);
            
            query = "INSERT INTO USERS (username, password, score) "
                    + "VALUES (?, ?, 0)";
            registrationStatement = connection.prepareStatement(query);
            
            query = "SELECT * FROM USERS "
                    + "WHERE username = ?";
            verificationStatement = connection.prepareStatement(query);
            
            query = "UPDATE USERS "
                    + "SET score = ? "
                    + "WHERE username = ?";
            updateStatement = connection.prepareStatement(query); 
        } catch(ClassNotFoundException ex) {   
            System.out.println("Could not find database driver");
        } catch(SQLException ex) {
            System.out.println("Could not connect to database"); 
            System.out.println(ex.toString());
        }
    }
    
    
    public int validateCredentials(String username, String password) {
        if(connection == null || validationStatement == null) {
            return 2;
        }
        try {
            validationStatement.setString(1, username);
            validationStatement.setString(2, password);
            
            result = validationStatement.executeQuery();
            
            if(result.next()) {
                return 0;
            } else {
                return 1;
            }
        } catch(SQLException ex) {
            System.out.println(ex.toString());
            return 2;
        }
    }
    
    public int registerUser(String username, String password) {
        if(connection == null || registrationStatement == null) {
            return 2;
        }
        try {
            registrationStatement.setString(1, username);
            registrationStatement.setString(2, password);                        
            
            if(userExists(username)) {
                return 1;
            } else {
                registrationStatement.executeUpdate();
                return 0;
            }
        } catch(SQLException ex) {
            System.out.println(ex.toString());
            return 2;
        }
    }
    
    public boolean userExists(String username) throws SQLException{
        
        verificationStatement.setString(1, username);               
        result = verificationStatement.executeQuery();
        
        return result.next();
        
    }
    
    public void updateScore(String username, int score) {
        if(connection != null && updateStatement != null) {
            try {
                int newScore = score;
                verificationStatement.setString(1, username);
                result = verificationStatement.executeQuery();
                
                if(result.next()) {
                    newScore += result.getInt("score");                      
                }
                updateStatement.setInt(1, newScore);
                updateStatement.setString(2, username);
                
                updateStatement.executeUpdate();
                
            } catch(SQLException ex) {
                System.out.println(ex.toString());
            }
        }
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

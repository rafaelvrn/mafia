
package mafia.persistance;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eduard Duman
 */
public class DBCreator {
    
    public static void main(String[] args) {
        
        try {
            Class.forName("org.apache.derby.jdbc.ClientDataSource40");
            System.out.println("Found DBMS driver.");
        } catch(ClassNotFoundException ex) {
            System.out.println("Could not find DBMS driver.");
            return;
        }
        
        String dbIP = "localhost";
        String port = "1527";
        String dbURL = "jdbc:derby://" + dbIP + ":" + port
                + "/mafiaDB;create=true";
        String query;
        
        Connection connection = null;
        Statement sqlStatement = null;
        
        
        try {
            System.out.println("Connecting to database...");
            connection = DriverManager.getConnection(dbURL);
            System.out.println("Connected to database.");
        } catch (SQLException ex) {
            System.out.println("Connection to database failed.");  
            System.out.println(ex.getMessage());
            return;
        }
        
        try {
            
            
            sqlStatement = connection.createStatement();
                              
            query = "CREATE TABLE USERS ("
                        + "username VARCHAR(12) NOT NULL PRIMARY KEY, "
                        + "password VARCHAR(20) NOT NULL, "                        
                        + "score INTEGER "                      
                        + ")";            
            sqlStatement.executeUpdate(query);
            System.out.println("Created USERS table.");
                
                
            
        } catch(SQLException ex) {
            System.out.println("Could not run one or more SQL statements.");
            System.out.println(ex.getMessage());            
        } finally {
            try {
                
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
                if(connection != null) {
                    connection.close();
                }
                
            } catch(SQLException ex) {
                System.out.println("An error occured while closing connections.");
            }
        }
                
    }
}

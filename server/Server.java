
package mafia.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import mafia.persistance.DatabaseConnection;


public class Server {
    
    private int port = 50000;
    private ServerSocket serverSocket = null;
    
    private ArrayList<Handler> clientHandlers;
    
    private HashMap<Handler, String> clients;
    
    private DatabaseConnection connection;
    
    private boolean gameIsFull = false;
    
    public static void main(String[] args) {
        
        Server server = new Server();
        server.start();
    }
    
    public void start() {
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println("Could not open port " + port);
            return;
        }
        
        clientHandlers = new ArrayList<>();
        clients = new HashMap<>();
        connection = new DatabaseConnection();
        
        while(true) {    
            
            try {
                Socket clientSocket;
                clientSocket = serverSocket.accept();
                
                Handler currentClientHandler = new Handler(clientSocket);
                clientHandlers.add(currentClientHandler);
                currentClientHandler.start();
            } catch (IOException ex) {
                System.out.println("Could not accept connection on port " + port);
                break;
            }
        }
    }
    
    private class Handler extends Thread {
        
        private Socket clientSocket;
        private BufferedReader input;
        private PrintWriter output;
        
        private Handler(Socket socket) {
            try {
                clientSocket = socket;
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                output = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
    }
}

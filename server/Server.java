
package mafia.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import mafia.persistance.DatabaseConnection;


public class Server {
    
    private final int port = 50000;
    private ServerSocket serverSocket = null;
    
    private final ArrayList<ClientHandler> clientHandlers;
    
    private final HashMap<ClientHandler, String> clients;
    
    private final DatabaseConnection connection;
    
    private final GameHandler gameHandler;        
    
    public Server() {
        gameHandler = new GameHandler();          
        clientHandlers = new ArrayList<>();
        clients = new HashMap<>();       
        connection = new DatabaseConnection();
    }
    
    public static void main(String[] args) {
        
        Server server = new Server();
        server.start();
    }
    
    public void start() {
        
        //gameHandler.start();
        
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println("Could not open port " + port);
            return;
        }
        
        
        
        
        while(true) {    
            
            try {
                Socket clientSocket;
                clientSocket = serverSocket.accept();
                
                ClientHandler currentClientHandler = new ClientHandler(clientSocket);
                clientHandlers.add(currentClientHandler);
                currentClientHandler.start();
            } catch (IOException ex) {
                System.out.println("Could not accept connection on port " + port);
                break;
            }
        }
    }
    
    
    private synchronized boolean addClient(ClientHandler clientHandler, String username) {
            
            if(clients.size() >=5) {
                return false;
            } else {
                clients.put(clientHandler, username);
                
                if(clients.size() == 5) {
                    // Commence game
                }
                return true;
            }
        }
    
    private class ClientHandler extends Thread {
        
        private Socket clientSocket;
        private BufferedReader input;
        private PrintWriter output;
        
        private ClientHandler(Socket socket) {
            try {
                clientSocket = socket;
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                output = new PrintWriter(clientSocket.getOutputStream(), true);
            } catch (IOException ex) {
                System.out.println(ex.toString());
            }
        }
        
        
        @Override
        public void run() {
            
            String username;
            String password;
            while(true) {
                
                String command;
                
                try {
                    command = input.readLine();
                    
                    switch(command) {
                        case "cmd":
                            gameHandler.performAction(this, input.readLine());
                            break;
                            
                        case "login":                                                        
                            int validationResult;
                            boolean joinResult;
                            
                            username = input.readLine();
                            password = input.readLine();
                            synchronized(connection) {
                                validationResult = connection.validateCredentials(username, password);
                            }
                            
                            switch(validationResult) {
                                case 1:
                                    output.println("invalid_credentials");
                                    break;
                                case 2:
                                    output.println("db_error");
                                    break;
                                case 0:
                                    joinResult = addClient(this, username);
                                    if(joinResult) {
                                        output.println("join_game");
                                    } else {
                                        output.println("game_is_full");
                                    }
                            }
                            break;
                            
                        case "register":                            
                            int registrationResult;
                            
                            username = input.readLine();
                            password = input.readLine();
                            synchronized(connection) {
                                registrationResult = connection.registerUser(username, password);
                            }
                            
                            switch(registrationResult){
                                case 1:
                                    output.println("db_error");
                                    break;
                                case 2:
                                    output.println("user_exists");
                                    break;
                                case 0:
                                    output.println("reg_success");
                            }                                    
                            break;
                        default:
                    }
                } catch (IOException ex) {
                    System.out.println(ex.toString());
                }
            }
        }
    }
    
    
    private class GameHandler extends Thread {
        
        
        @Override
        public void run() {
            
        }
        
        public synchronized void performAction(ClientHandler clientHandler, String action) {
            
        } 
        
    }
}

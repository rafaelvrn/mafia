
package mafia.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import mafia.persistance.DatabaseConnection;


public class Server {
    
    private final int port = 50000;
    private ServerSocket serverSocket = null;
    
    private final ArrayList<ClientHandler> clientHandlers;
    
    private final HashMap<ClientHandler, String> connectedClients;
    
    private final DatabaseConnection connection;
    
    private GameHandler gameHandler;        
    
    private final int maxPlayers = 3;
    
    public Server() {                
        clientHandlers = new ArrayList<>();
        connectedClients = new HashMap<>();       
        connection = new DatabaseConnection();
    }
    
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
    
    
    private synchronized boolean addClientToGame(ClientHandler clientHandler, String username) {
            
            if(connectedClients.size() >= maxPlayers) {
                return false;
            } else {
                connectedClients.put(clientHandler, username);
                                
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
                            gameHandler.handleRequest(this, input.readLine());
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
                                    joinResult = addClientToGame(this, username);
                                    if(joinResult) {
                                        output.println("join_game");
                                        if(connectedClients.size() == maxPlayers) {
                                            gameHandler = new GameHandler(connectedClients);
                                        }
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
                                    output.println("user_exists");
                                    break;
                                case 2:
                                    output.println("db_error");
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
        
        public void sendMessage(String response) {
            output.println(response);
        }
    }
    
    
    private class GameHandler {
        
        private final HashMap<ClientHandler, String> playersAlive;
        private final Set<ClientHandler> players;
        private final ClientHandler assassin;
        
        private boolean isNighttime = false; 
                
        
        private GameHandler(HashMap<ClientHandler, String> clients) {
            ClientHandler[] handlers;
            
            players = clients.keySet();
            
            playersAlive = clients;
            
            handlers = playersAlive.keySet().toArray(new ClientHandler[0]);
            
            assassin = handlers[(new Random()).nextInt(playersAlive.size())];
            
            String playerList = "";
            for(String name : clients.values()) {
                playerList += name + " ";
            }
            
            broadcastInitialize(playerList);
            cycleToNight();
            
        }
        
        
        public synchronized void handleRequest(ClientHandler clientHandler, String request) {
            
            Scanner parser = new Scanner(request);
            String response;
            String head = parser.next();
            
            switch(head) {
                case "msg":                    
                    response = connectedClients.get(clientHandler) + ":";
                    response += parser.nextLine();
                    broadcastMessage(response);
                    break;
                default:
            }
        }
        
        private void broadcastMessage(String message) {
            if(!isNighttime) {
                for(ClientHandler player : players) {
                    player.sendMessage("msg");
                    player.sendMessage(message);
                }
            }
        }
        
        private void broadcastInitialize(String playerList) {
            for(ClientHandler player : players) {
                player.sendMessage("init");
                if(player == assassin) {
                    player.sendMessage("Assassin");
                } else {
                    player.sendMessage("Civillian");
                }
                player.sendMessage(playerList);
            }
        }
        
        private void cycleToNight() {
            broadcastMessage("It is now nighttime. The assassin will make his move.");
            isNighttime = true;
        }
        
        private void cycleToDay() {
            broadcastMessage("It is now daytime.");
            isNighttime = false;
        }
    }
}


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
import java.util.LinkedList;
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
    
    private final int maxPlayers = 4;
    
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
        
        private HashMap<String, Integer> voteList;
        private LinkedList<ClientHandler> voters;
        private int guiltyVotes;
        private int allVotes;
        private String acusee;
        
        private boolean isNighttime = false; 
        private boolean gameIsOver = false;
        private boolean sentenceIsNeeded = false;        
        private int kills = 0;
                
        
        private GameHandler(HashMap<ClientHandler, String> clients) {
            ClientHandler[] handlers;
            
            players = clients.keySet();            
            playersAlive = new HashMap<>(clients);            
            handlers = playersAlive.keySet().toArray(new ClientHandler[0]);            
            assassin = handlers[(new Random()).nextInt(playersAlive.size())];
            
            voteList = new HashMap<>();
            voters = new LinkedList<>();
            
            String playerList = "";
            for(String name : clients.values()) {
                playerList += name + " ";
            }
            
            broadcastInitialize(playerList);
            cycleToNight();
            
        }
        
        
        public synchronized void handleRequest(ClientHandler client, String request) {
            
            if(gameIsOver || !(playersAlive.containsKey(client))) {
                return;
            }
            
            Scanner parser = new Scanner(request);
            String content;
            String head = parser.next();
            
            switch(head) {
                case "msg":                    
                    content = connectedClients.get(client) + ":";
                    content += parser.nextLine();
                    if(!isNighttime && !sentenceIsNeeded) {
                        broadcastMessage(content);
                    }
                    break;
                case "click":
                    content = parser.next();
                    handleClickEvent(client, content);
                    break;
                default:
            }
        }
        
        private void broadcastMessage(String message) {            
            for(ClientHandler player : players) {
                player.sendMessage("msg");
                player.sendMessage(message);                
            }
        }
        
        private void broadcastInitialize(String playerList) {
            for(ClientHandler player : players) {
                player.sendMessage("init");                
                player.sendMessage(connectedClients.get(player));
                
                if(player == assassin) {
                    player.sendMessage("Assassin");
                } else {
                    player.sendMessage("Civillian");
                }
                
                player.sendMessage(playerList);
            }
        }
        
        private void broadcastKillCommand(String target) {
            for(ClientHandler player : players) {
                player.sendMessage("kill");
                player.sendMessage(target);                
            }
        }
        
        private void handleClickEvent(ClientHandler client, String target) {
            
            if(isNighttime) {
                if(client == assassin && playersAlive.containsValue(target)
                        && !(playersAlive.get(assassin).equals(target))) {
                    assassinate(target);
                }
            } else {
                if(sentenceIsNeeded) {
                    allVotes++;
                    if(target.equals("guilty")) {
                        guiltyVotes++;
                    }
                    if(allVotes == playersAlive.size()) {
                        passVerdict();
                    }
                } else {
                    if(!(voters.contains(client)) && playersAlive.containsValue(target)) {
                        voters.add(client);
                        
                        if(voteList.containsKey(target)) {
                            voteList.put(target, voteList.get(target) + 1);
                        } else {
                            voteList.put(target, 1);
                        }
                        
                        broadcastMessage("> " + playersAlive.get(client) 
                                + " voted for " + target);

                        if(voters.size() == playersAlive.size()) {
                            int maxVotes = 0;
                            String playerWithMostVotes = null;
                            
                            for(Map.Entry<String, Integer> entry : voteList.entrySet()) {
                                if(entry.getValue() > maxVotes) {
                                    maxVotes = entry.getValue();
                                    playerWithMostVotes = entry.getKey();
                                }
                            }
                            
                            startSentence(playerWithMostVotes);
                        }
                    }                                        
                }
                
            }
        }
        
        private void startSentence(String accusedPlayer) {
            
            sentenceIsNeeded = true;
            acusee = accusedPlayer;
            guiltyVotes = 0;
            allVotes = 0;
            
            for(ClientHandler player : playersAlive.keySet()) {
                player.sendMessage("vote");
                player.sendMessage(accusedPlayer);
            }
            
            voteList = new HashMap<>();
            voters = new LinkedList<>();
        }
        
        private void passVerdict() {
            sentenceIsNeeded = false;
            
            if(guiltyVotes <= playersAlive.size()/2) {
                broadcastMessage("> " + acusee + " was found NOT guilty.");
                cycleToNight();
            } else {
                execute(acusee);
            }
        }
        
        private void assassinate(String target) {
            kill(target);
            broadcastMessage("> " + target + " has been assassinated.");
            kills++;
            
            if(playersAlive.size() > 2) {
                cycleToDay();
            } else {
                endGame("assassin");
            }
        }
        
        private void execute(String target) {
            kill(target);
            broadcastMessage("> " + target + " has been exectuted.");
            
            if(target.equals(connectedClients.get(assassin))) {
                endGame("civillians");
            } else if(playersAlive.size() <= 2) {
                endGame("assassin");
            } else {
                cycleToNight();
            }
        }
        
        private void kill(String target) {
            for(Map.Entry<ClientHandler, String> entry : playersAlive.entrySet()) {
                if(entry.getValue().equals(target)) {
                    playersAlive.remove(entry.getKey());
                    break;
                }
            }
            broadcastKillCommand(target);            
        }
        
        private void endGame(String winner) {
            gameIsOver = true;
                        
            if(winner.equals("assassin")) {
                broadcastMessage("The assassin has won.");
                for(Map.Entry<ClientHandler, String> entry : connectedClients.entrySet()) {
                    int score = 0;
                    if(entry.getKey() == assassin) {
                        score = 5 + kills;
                    } else if(playersAlive.containsKey(entry.getKey())) {
                        score = 2;
                    }
                    entry.getKey().sendMessage("msg");
                    entry.getKey().sendMessage("You received " + score + " points.");                  
                }
            } else {
                broadcastMessage("The civillians have won.");
                for(Map.Entry<ClientHandler, String> entry : connectedClients.entrySet()) {
                    int score = 5;
                    if(entry.getKey() == assassin) {
                        score = kills;
                    } else if(playersAlive.containsKey(entry.getKey())) {
                        score = 7;
                    }
                    entry.getKey().sendMessage("msg");
                    entry.getKey().sendMessage("You received " + score + " points.");                  
                }
            }
        }
                        
        private void cycleToNight() {
            broadcastMessage("> It is now nighttime. The assassin will make his move.");
            isNighttime = true;
        }
        
        private void cycleToDay() {
            broadcastMessage("> It is now daytime. Vote who the killer is.");
            isNighttime = false;
        }
    }
}

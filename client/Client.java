
package mafia.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import mafia.playergi.PlayerGI;


public class Client {
    
    private final PlayerGI playergi;
    private final ServerListener listener;
    private final Socket clientSocket; 
    private final PrintWriter output;
    private final BufferedReader input;
    private final String ip;
    private final int port;
    private volatile boolean isRunning;
    
    public Client(PlayerGI playergi) throws IOException {
        
        this.playergi = playergi;
                        
        ip = "localhost";
        port = 50000;
        isRunning = true;
        
        clientSocket = new Socket(ip, port);
        output = new PrintWriter(clientSocket.getOutputStream(), true);
        input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        listener = new ServerListener();
        listener.start();
    }
    
    public void login(String username, String password) {
        output.println("login");
        output.println(username);
        output.println(password);
    }
    
    public void register(String username, String password) {
        output.println("register");
        output.println(username);
        output.println(password);
    }
    
    public void sendMessage(String msg) {
        output.println("cmd");
        output.println("msg " + msg);
    }
    
    public void clickEvent(String target) {
        output.println("cmd");
        output.println("click " + target);
    }
    
    public void stop() {
        isRunning = false;
        output.println("echo");
    }
    
    private class ServerListener extends Thread {
        
        @Override
        public void run() {
            String cmd;
            String message;
            while(isRunning) {
                try {
                    cmd = input.readLine();
                    
                    switch(cmd) {
                        case "msg":
                            message = input.readLine();
                            playergi.printMessage(message);
                            break;
                        case "kill":
                            String target = input.readLine();
                            playergi.killPlayer(target);
                            break;               
                        case "vote":
                            String accusedPlayer = input.readLine();
                            playergi.openVoteMenu("Is " + accusedPlayer + " guilty?");
                            break;
                        case "init":
                            String name = input.readLine();
                            String role = input.readLine();
                            String playerList = input.readLine();
                            playergi.commenceGame(name, role, playerList.split(" +"));
                            playergi.loadGame();
                            break;
                        case "invalid_credentials":
                            playergi.printLoginErrorMessage("Incorrect username "
                                    + "and/or password");
                            break;
                        case "db_error":
                            playergi.printLoginErrorMessage("Cannot connect to database");
                            break;
                        case "join_game":
                            playergi.showWaitScreen();
                            break;
                        case "game_is_full":
                            playergi.printLoginErrorMessage("Game is full");
                            break;
                        case "user_exists":
                            playergi.printLoginErrorMessage("Username already exists");
                            break;
                        case "reg_success":
                            playergi.printLoginErrorMessage("Registration successful");
                            break;
                        default:
                    }
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                    Client.this.stop();
                } 
            }
            output.println("stop");
            try {
                input.close();
                output.close();
                clientSocket.close();
            } catch(IOException ioex) {
                System.out.println(ioex.toString());
            }
        }
    }
}

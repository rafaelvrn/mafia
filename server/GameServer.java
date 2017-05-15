package server;
import static server.GameServer.con;
import java.io.* ;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import chatServerPackage.*;

import java.sql.*; 
import java.net.*;


public class GameServer {
    private static ServerSocket listener;
    
    private static int port=49477;
 
    public static Connection con;
    
    private ArrayList<Handler> connectedClientsHandlers;
    
    private ArrayList<Socket> connectedClientsSockets;
    
    private ArrayList<Socket> inQueueSockets;
    
    private ArrayList<Handler> inQueueHandlers;
    
    private static int uniqueID = 0;
    
    private int GameState; // daca este 1 inseamna ca nu este niciun  joc in desfasurare, iar daca este 0 inseamna ca este un joc(GameInstance) 
    
//    public GameServer()
//    	{
//    	try
//			{ Class.forName("com.mysql.jdbc.Driver"); 
//			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mafiabase", "george", "pass11");
//
//			}catch(Exception e){ e.printStackTrace(); } }
    	
    
  
    public static void main(String[] args) throws IOException {
        
        
        //ChatServerGUI CSG=new ChatServerGUI(1200);
        
    	try
			{ Class.forName("com.mysql.jdbc.Driver"); 
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mafiabase", "george", "pass11");

			}catch(Exception e){ e.printStackTrace(); }
        
       
      GameServer gs = new GameServer();
      gs.start();
      
        
      
        
    }
    
    
    synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < connectedClientsHandlers.size(); ++i) {
			Handler h = connectedClientsHandlers.get(i);
			Socket s = connectedClientsSockets.get(i);
			// found it
			if(h.id == id) {
				connectedClientsHandlers.remove(i);
				connectedClientsSockets.remove(i);
				return;
			}
		}
	}
    
    
    
    public void start()
    	{
    	   try {
			listener = new ServerSocket(51438);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
           
    	   connectedClientsHandlers = new ArrayList<Handler>();
    	   inQueueHandlers = new ArrayList<Handler>();
    	   connectedClientsSockets = new ArrayList<Socket>();
    	   inQueueSockets = new ArrayList<Socket>();
    	   GameState = 1;
           Socket socket = new Socket();
    	   
           
           try 
           {
           		boolean keepGoing = true;
               while (keepGoing) {
                   //new Handler(listener.accept()).start();
               	
            	   
            	   if(inQueueSockets.size()>=5)
            	   		{ 	Socket tempSocket = new Socket();
            		   		new GameInstance(connectedClientsHandlers,inQueueSockets);
            		   		
            		   		for(int i = 0; i<inQueueSockets.size();i++)
            		   			{ inQueueHandlers.get(i).SendMsg("1");  // jocul a inceput
            		   			inQueueHandlers.remove(i);
            		   			inQueueSockets.remove(i); }
            		   		
            		   		GameState = 0; // cand se incepe un joc se blocheaza posibilitatea de a da joingame pana cand se termina
            		   	}
            	   
            	   
				try 
				{
					socket = listener.accept();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				} 
				
				if(!keepGoing)
					break;
               	Handler h = new Handler(socket);
               	connectedClientsHandlers.add(h);
               	connectedClientsSockets.add(socket);
               	h.start();
                  
                   
               }
               
            
               
               }catch(Exception E){ E.printStackTrace(); }
   			
           
           
    	}
 
    public void GameServerClose()
	{
	try {
			listener.close();
			for(int i = 0; i < connectedClientsHandlers.size(); ++i) {
				Handler h = connectedClientsHandlers.get(i);
				Socket s = connectedClientsSockets.get(i);
				try {
				h.in.close();
				h.out.close();
				h.socket.close();
				s.close();
				}catch(Exception E) { E.printStackTrace(); }
				} }
			catch(Exception E) { E.printStackTrace(); }
	}
    
   
    class Handler extends Thread{
        Socket socket;
        BufferedReader in;
        PrintWriter out;
        private volatile boolean isRunning = true;
        private String user;
        private String pass;
        private int score;
        private int id;
        
      
        
        
        public Handler(Socket clientSocket) {
            try {
            	id = uniqueID++;
                socket = clientSocket;
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

            } catch (IOException e) {
                System.out.println(e);
            }
        }

     
        public void run(){
          
            while (isRunning)    
            {    
                try {
                	
                	String command;
    				command = in.readLine();
    				
    				switch(command)
    					{ case "login":	
    						{
    							String LoginData = in.readLine();
    							int ok = Login(LoginData);
    							
    							if(ok == 1)
    								{ out.println("1");
    								out.println(user);
    								out.println(pass);
    								out.println(ReturnScore());
    								out.flush();}
    							else
    								{ out.println("0");
    								out.flush(); }
    						} 
    						
    						
    						case "register":	
    						{
    							String RegisterData = in.readLine();
    							int ok = Register(RegisterData);
    							
    							if(ok == 1)
    								{ out.println("1");
    								out.flush();}
    							else
    								{ out.println("0");
    								out.flush(); }
    						} 
    						
    						
    						case "joingame":
    						{	
    							if(GameState == 1)
    								{ inQueueSockets.add(socket);
    								inQueueHandlers.add(this);
    								out.println("1");
    								out.flush(); }
    							else
    								{ out.println("0");
    								out.flush(); }
    						}
    						
    						
    						default :
    						{ 
    							LogOut();
    							break;
    						}
    					
    					}
                    
                	} catch (Exception ex) { Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex); }
               
            }
            
            try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            remove(id);
         
        }
     

        public int CheckIfUsernameExists(String username)
    	{ try
    		{ String sql = "select * from users";
    		
    		Statement stat = con.createStatement();
    		ResultSet result = stat.executeQuery(sql);
    		
    		while(result.next())
    			if(result.getString("username").equals(username))
    				return 1;
    		
    		}catch(Exception e){ e.printStackTrace(); System.out.println("Error in method CheckIfUsernameExists"); }
    	
    	return 0; }
        
        
        public int CheckIfPasswordExists(String username, String password)
    	{ try
    		{ String username2 = username;
    		username2 = "'" + username2 + "'";
    		String sql = "select * from users where username=" + username2;

    		Statement stat = con.createStatement();
    		ResultSet result = stat.executeQuery(sql);
    		
    		result.next();

    		if(result.getString("password").equals(password))
    				return 1;

    		}catch(Exception e){ e.printStackTrace(); System.out.println("Error in method CheckIfPasswordExists"); }

    	return 0; }
        
        
        public int Login(String data)
    	{ try
    		{ String [] Data = data.split(" "); 
    		String username = Data[0], password = Data[1];
    		
    		System.out.println(CheckIfUsernameExists(username) + " " +CheckIfPasswordExists(username, password) );
    		
    	
    		
    		
    		if(CheckIfUsernameExists(username) == 1 && CheckIfPasswordExists(username, password) == 1)
    			{ user = username; pass = password; return 1; } // in caz de login cu succes
    		else
    			if(CheckIfUsernameExists(username) == 0 && CheckIfPasswordExists(username, password) ==0)
    				return 0;  // in caz ca am2 sunt gresite
    		else
    			if(CheckIfUsernameExists(username) == 0)
    				return -1; // in caz de user gresit
    		else
    			if(CheckIfPasswordExists(username, password) ==0)
    				return -2; // in caz de pass gresit
    		
    		}catch(Exception e){ e.printStackTrace(); System.out.println("Error in method Login"); }
    	
    	return 0; }
        
        
        public int Register(String data)
        { try
    		{ String [] Data = data.split(" "); 
    		String username = Data[0], password = Data[1];
    		int score = 0;
    	
    		if(CheckIfUsernameExists(username) == 0)
    			{username = "'" + username + "'";
    			password = "'" + password + "'";
    			
    			String sql = "insert into users " + " (username, password, score)" + " values(" +  username + "," + password + "," + score + ")"; 
    			
    			Statement stat = con.createStatement();
    			stat.executeUpdate(sql);
    			
    			return 1;
    			}
    		
    		}catch(Exception e){ e.printStackTrace(); System.out.println("Error in method Login"); }

        return 0; }
        
        
        public int ReturnScore()
        { try
    		{ String username2 = user;
    		username2 = "'" + username2 + "'";
    		String sql = "select * from users where username=" + username2;

    		Statement stat = con.createStatement();
    		ResultSet result = stat.executeQuery(sql);
    	
    		result.next();

    		return result.getInt("score");

    		}catch(Exception e){ e.printStackTrace(); System.out.println("Error in method ReturnScore"); }

        return -1; }
        
        
        public int SendMsg(String msg)
        { try
			{ out.println(msg);
			
			return 1;
			
			}catch(Exception e){ e.printStackTrace(); System.out.println("Error in method SendMsg"); }

        return 0; }
        
        
        private void LogOut()
        {
        	try {
                out.close();
                in.close();
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(Handler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    
    
    class GameInstance
    	{ private ArrayList<Handler> connectedClientsHandlers;
    	private ArrayList<Socket> connectedClientsSockets;
    	
    		public GameInstance(ArrayList<Handler> cch, ArrayList<Socket> ccs)
    		{
    			connectedClientsHandlers = cch;
   				connectedClientsSockets = ccs;
    		}

    		
    		public void broadcast()
    		{
    			
    		}
    		
    		
    		synchronized void remove(int id) {
    			// scan the array list until we found the Id
    			for(int i = 0; i < connectedClientsHandlers.size(); ++i) {
    				Handler h = connectedClientsHandlers.get(i);
    				Socket s = connectedClientsSockets.get(i);
    				// found it
    				if(h.id == id) {
    					connectedClientsHandlers.remove(i);
    					connectedClientsSockets.remove(i);
    					return;
    				}
    			}
    		}
    		
    		
    	}
    
}

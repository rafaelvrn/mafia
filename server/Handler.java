package server;

import java.io.* ;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import static server.GameServer.con;


public class Handler extends Thread{
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean isRunning = true;
    private String user;
    private String pass;
    private int score;
  
    
    
    public Handler(Socket clientSocket) {
        try {
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
								out.flush();}
						} 
						
						
						case "joingame":
						{
							
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

		}catch(Exception e){ e.printStackTrace(); System.out.println("Error in method CheckIfPasswordExists"); }

    return -1; }
    
    
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

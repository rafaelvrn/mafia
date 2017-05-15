
import java.io.BufferedReader; 
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {

    private final Socket clientSocket; 
    private final PrintWriter out;
    private final BufferedReader in;
    private String user;
    private String pass;
    private int score;

    /**
     *
     * @param ip
     * @param port
     * @throws IOException
     */
    public GameClient(String ip,int port) throws IOException //Constructor
    {
        
        clientSocket = new Socket(ip,port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    /**
     *
     * @throws IOException
     */
    public void start() throws IOException
    {
        boolean flag=true;
       
        
        while(flag)
        	{
            
            break;
        	}
      
        
    }
    
    
    public int JoinGame()
    { try
		{ out.println("joingame");
		out.flush();
		
		int JoinState = Integer.parseInt(in.readLine());
		
		if(JoinState == 1)
		{ 
			new ListenFromServer(); // pornim jocul
			return 1;
		}
		else
			System.out.println("se afla deja un joc in desfasurare");
		
		
		}catch(Exception e){ e.printStackTrace(); }
    		
   	return 0; }
   
    
   public int Login(String data)
   	{ try
   		{ out.println("login");
   		out.println(data);
   		out.flush();
	
   		int LoginState = Integer.parseInt(in.readLine()); 

   		if(LoginState ==  1)
   			{ user = in.readLine(); pass = in.readLine(); score = Integer.parseInt(in.readLine()); return 1; }// in case of success

   		}catch(IOException e){ e.printStackTrace(); }

   	return -1; } // in case of failure
   
   
   public int Register(String data)
  	{ try
  		{ out.println("register");
  		out.println(data);
  		out.flush();
	
  		int RegisterState = Integer.parseInt(in.readLine()); 

  		if(RegisterState ==  1)
  			return 1; // in case of success

  		}catch(IOException e){ e.printStackTrace(); }

  	return 0; } // in case of failure

   
   public void Exit()
   	{ try
	   { in.close();
       out.close();
       clientSocket.close(); }
   	catch(Exception E){E.printStackTrace();}
   	
   	}
   
   
   class ListenFromServer extends Thread {

		public void run() 
		{
			while(true)
			{
				try 
				{
					String msg = in.readLine();
				}
				catch(IOException e)
				{
					System.out.println("server has close the connection" + e);
					break;
				}
				
			}
		}
   }
   
   
   
}
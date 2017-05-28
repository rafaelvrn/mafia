
package mafia.playergi;

import java.util.Scanner;



public class Tester extends Thread {
    
    private final PlayerGI gui;
    
    public Tester(PlayerGI gui) {
        this.gui = gui;
    }
    
    public void sendMsg(String msg) {
        System.out.println(msg);
    }
    
    @Override
    public void run() {
        
        Scanner input;
        String command;
        String param;
        boolean exit = false;
        
        while(exit == false) {
            
            input = new Scanner(System.in);
            
            command = input.next();
            
            switch(command) {
                case "kill":
                    param = input.nextLine();
                    param = param.substring(1);
                    gui.killPlayer(param);
                    break;
                case "print":
                    param = input.nextLine();
                    param = param.substring(1);
                    gui.printMessage(param);
                    break;
                case "startvote":
                    param = input.nextLine();
                    param = param.substring(1);
                    gui.openVoteMenu(param);
                    break;
                case "stopvote":
                    gui.closeVoteMenu();
                    break;
                case "error":
                    param = input.nextLine();
                    param = param.substring(1);
                    gui.printLoginErrorMessage(param);
                    break;
                case "begingame":
                    gui.loadGame();
                    break;
                case "wait":
                    gui.showWaitScreen();
                    break;
                case "exit":
                    exit = true;
                default:
            }
        }
        
    }
}

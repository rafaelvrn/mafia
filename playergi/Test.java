
package mafia.playergi;

import java.util.Random;


public class Test {
    
    public static void main(String[] args) {
        
        String s = "I am the only    boss around  ";
        String[] spl = s.split(" +");
        
        for(String str : spl) {
            System.out.println(str);
        }
    }
}


package playergi;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


/**
 * Class that stores information about player cards
 */
public class PlayerCard extends Rectangle {
    
    private String playerName;
    
    public PlayerCard() {
        super(0, 0, 200, 150);
        setFill(Color.KHAKI);
        setStroke(Color.BLACK);
    }
    
    /**
     * Returns the name of the player associated with this card
     * 
     * @return the name of the player associated with this card
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Sets the name of the player associated with this card
     * 
     * @param playerName the name of the player associated with this card
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    
}


package playergi;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Class that stores user messages
 */
public class Message extends Text {
    
    public Message(String text) {        
        super(text);        
        this.setWrappingWidth(590);
        this.setFont(Font.font(16));
    }
}

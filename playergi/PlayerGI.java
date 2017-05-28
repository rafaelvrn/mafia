
package mafia.playergi;

import mafia.client.GameClient;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import mafia.client.Client;


public class PlayerGI extends Application {

    private StackPane root;
    private VBox gamePane;
    
    private TilePane playerCardPane;
    private ArrayList<PlayerCard> playerCards;        
    
    private VBox chatPane;
    private VBox chatBox;
    private TextField chatInput;
    private ScrollPane chatScrollPane;
    
    private StackPane votePane;
    private Text voteMessage;
    private HBox voteButtonsPane;
    private Button voteGuilty;
    private Button voteNotGuilty;
    
    private StackPane loginPane;
    private VBox loginFields;
    private TextField loginUserField;
    private TextField loginPasswordField;
    private Text loginErrorMessage;
    private Button loginSubmit;
    private Button registerButton;
    
    private StackPane waitPane;
    private Text waitText;
    private ScaleTransition waitAnimation;
    
    private VBox namePanel;
    private Text name;
    private Text role;
    
    private Scene scene;
        
    Client client;               
    
    /**
     * Creates a list of player cards, containing the names of the players
     * 
     * @param playerNames the names of the players
     */
    public void createPlayerCards(String[] playerNames) {
        
        playerCards = new ArrayList<>();
        
        playerCardPane = new TilePane();
        playerCardPane.setPrefColumns(4);
        playerCardPane.setPrefTileWidth(250);
        playerCardPane.setPrefTileHeight(170);
        playerCardPane.setAlignment(Pos.CENTER);
        
        for(String playerName : playerNames) {
            
            StackPane tile = new StackPane();
            
            PlayerCard playerCard = new PlayerCard();
            playerCard.setPlayerName(playerName);
            playerCards.add(playerCard);
            
            Label label = new Label(playerName);
            label.setFont(Font.font(26));
            label.setMouseTransparent(true);
            
            tile.getChildren().addAll(playerCard, label);
            
            playerCardPane.getChildren().add(tile);
        }
        
    }
    
    
    /**
     * Creates a chat box and a chat input field
     */
    public void createChatBox() {
                       
        chatPane = new VBox();           
        chatPane.setAlignment(Pos.CENTER);        
                        
        chatBox = new VBox();           
        
        chatInput = new TextField();
        chatInput.setMaxWidth(600);
        
        chatScrollPane = new ScrollPane();
        chatScrollPane.setPrefHeight(250);
        chatScrollPane.setBorder(new Border(new BorderStroke(Color.BLACK, 
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        chatScrollPane.setMaxWidth(600);
        chatScrollPane.setContent(chatBox);
        chatScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        chatScrollPane.vvalueProperty().bind(chatBox.heightProperty());
               
        chatPane.getChildren().addAll(chatScrollPane, chatInput);
    }
    
    
    /**
     * Creates the message and the buttons needed for voting
     */
    public void createVoteMenu() {
        
        votePane = new StackPane();
        voteMessage = new Text();
        voteButtonsPane = new HBox();
        VBox container = new VBox();
               
        Rectangle overlay = new Rectangle(0, 0, 1000, 680);
        overlay.setFill(Color.BLACK);
        overlay.setOpacity(.9);
        
        container.setAlignment(Pos.CENTER); 
        container.setSpacing(10);
        
        voteButtonsPane.setAlignment(Pos.CENTER);
        voteButtonsPane.setSpacing(30);
        
        voteMessage.setFont(Font.font(25));
        voteMessage.setFill(Color.WHITE);
        
        voteGuilty = new Button("Guilty");
        voteGuilty.setPrefSize(150, 50);    
        voteGuilty.setFont(Font.font("" , FontWeight.BOLD, 16));
                
        voteNotGuilty = new Button("Not guilty");
        voteNotGuilty.setPrefSize(150, 50);
        voteNotGuilty.setFont(Font.font("" , FontWeight.BOLD, 16));
        
        voteButtonsPane.getChildren().addAll(voteGuilty, voteNotGuilty);
        
        container.getChildren().addAll(voteMessage, voteButtonsPane);
        
        votePane.getChildren().addAll(overlay, container);
    }
    
    
    /**
     * Creates the login page
     */
    public void createLoginPage() {
        
        Rectangle loginBG = new Rectangle(0, 0, 1000, 680);
        loginBG.setFill(Color.DARKGRAY);
                        
        loginFields = new VBox();
        loginFields.setSpacing(8);        
        loginFields.setMaxSize(250, 100);
        loginFields.setAlignment(Pos.CENTER);
        
        loginUserField = new TextField();
        loginPasswordField = new TextField();
        
        loginErrorMessage = new Text();
        loginErrorMessage.setFill(Color.RED);
        
        loginSubmit = new Button("Login");
        loginSubmit.setPrefWidth(80);
        
        registerButton = new Button("Register");
        registerButton.setPrefWidth(80);
        
        loginFields.getChildren().add(new HBox(new Text("Username "), loginUserField));
        loginFields.getChildren().add(new HBox(new Text("Password  "), loginPasswordField));
        loginFields.getChildren().addAll(loginErrorMessage, loginSubmit, registerButton);
        
        loginPane = new StackPane(loginBG, loginFields);
    }
    
    
    /**
     * Creates the waiting screen
     */
    public void createWaitScreen() {
        
        Rectangle waitBG = new Rectangle(0, 0, 1000, 680);
        waitBG.setFill(Color.DARKGRAY);
        
        waitPane = new StackPane();
        
        waitText = new Text("Waiting for other players...");
        waitText.setFont(Font.font("", FontWeight.BOLD, 24));
        
        waitPane.getChildren().addAll(waitBG, waitText);
        
        waitAnimation = new ScaleTransition(new Duration(1000), waitText);
        waitAnimation.setByX(.3f);
        waitAnimation.setByY(.3f);
        waitAnimation.setAutoReverse(true);
        waitAnimation.setCycleCount(Animation.INDEFINITE);        
    }
    
    
    public void createNamePanel() {
        
        namePanel = new VBox();
        namePanel.setAlignment(Pos.CENTER);
        namePanel.setBorder(new Border(new BorderStroke(Color.BLACK, 
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        namePanel.setMaxHeight(100);
        namePanel.setMaxWidth(170);
        namePanel.setTranslateX(-400);
        namePanel.setTranslateY(220);
        
        name = new Text("Name");
        name.setFont(Font.font(24));         
        
        role = new Text("Role");
        role.setFont(Font.font(24));
        
        namePanel.getChildren().addAll(name, role);
    }
    
    
    /**
     * Adds the voting menu to the scene
     * @param msg the message that will appear in the voting menu
     */
    public void openVoteMenu(String msg) {
        Platform.runLater(() -> {
            voteMessage.setText(msg);
            root.getChildren().add(votePane);
        });
    }
    
    
    /**
     * Removes the voting menu from the scene
     */
    public void closeVoteMenu() {
        Platform.runLater(() -> {
            root.getChildren().remove(votePane);
        });
    }
    
    
    /**
     * Opens the "waiting for players" screen
     */
    public void showWaitScreen() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            root.getChildren().add(waitPane);
            waitAnimation.play();
        });
    }
            
    
    /**
     * Loads game assets
     */
    public void loadGame() {
        Platform.runLater(() -> {
            root.getChildren().clear();
            root.getChildren().addAll(gamePane, namePanel);
            waitAnimation.stop();
        });
    }
    
    
    /**
     * Updates and removes functionality of a player card
     * 
     * @param player the name of the player
     */
    public void killPlayer(String player) {
        Platform.runLater(() -> {
            Pane tile;
        
            for(PlayerCard playerCard : playerCards) {

                if(playerCard.getPlayerName().toLowerCase().
                        equals(player.toLowerCase())) {                    
                    tile = (Pane)(playerCard.getParent());
                    tile.getChildren().add(new ImageView("mafia/playergi/images/dead.png"));
                }
            }
            
            if(player.equals(name.getText())) {
                lock();
            }
        });
    }
    
    
    /**
     * Prints text in the chat box
     * 
     * @param text the text that will be printed
     */
    public void printMessage(String text) {
        Platform.runLater(() -> {
            ObservableList<Node> messages = chatBox.getChildren();
        
            if(messages.size() >= 40) {
                messages.remove(0);
            }

            messages.add(new Message(text));                        
        });
    }
    
    
    /**
     * Prints a message on the login screen
     * 
     * @param msg the message that will be printed
     */
    public void printLoginErrorMessage(String msg) {
        loginErrorMessage.setText(msg);
    }
    
    
    public void lock() {
        
        chatInput.setDisable(true);

        for(PlayerCard playerCard : playerCards) {
            playerCard.setOnMouseClicked(null);
        }

        name.setFill(Color.RED);
        role.setFill(Color.RED);
    }
    
        
    /**
     * Creates the visual elements of the application
     */
    public void buildGUI() {                              
                    
        createVoteMenu();   
        createLoginPage();
        createWaitScreen();  
        createNamePanel();
                        
        root = new StackPane(loginPane);
        scene = new Scene(root, 1000, 680);
    }
    
    
    @Override
    public void start(Stage stage) {                 
        
        buildGUI();
        
        stage.setTitle("Mafia");
        stage.setScene(scene);                
        stage.show();
        
        try{                               
            client = new Client(this);   
            
            loginSubmit.setOnAction(ev -> {
                client.login(loginUserField.getText().trim(), 
                         loginPasswordField.getText().trim());
                flush();
            });
        
            registerButton.setOnAction(ev -> {
                client.register(loginUserField.getText(),
                        loginPasswordField.getText()); 
                flush();
            });
        } catch(IOException ex) {
            
            loginSubmit.setOnAction(ev -> {
                printLoginErrorMessage("Cannot connect to server");     
                flush();
            });
        
            registerButton.setOnAction(ev -> {
                printLoginErrorMessage("Cannot connect to server");     
                flush();
            });
        }                       
        
    }
    
    public void commenceGame(String name, String role, String[] players) {
        
        createPlayerCards(players);
        createChatBox(); 
        
        printMessage("> The game has started.");
        printMessage("> You are " + (role.equals("Assassin") ? "the " : "a ") + role + ".");
        
        this.name.setText(name);
        this.role.setText(role);
        
        gamePane = new VBox(30);
        gamePane.setPrefSize(1000, 300);            
        
        gamePane.getChildren().addAll(playerCardPane, chatPane);
        
        for(PlayerCard playerCard : playerCards) {
            playerCard.setOnMouseClicked(ev -> {
                client.clickEvent(playerCard.getPlayerName());
            });
        }
        
        chatInput.setOnAction(ev -> {
            client.sendMessage(chatInput.getText());
            chatInput.clear();
        });
        
        voteGuilty.setOnAction(ev -> {
            client.clickEvent("guilty");
            closeVoteMenu();
        });
        
        voteNotGuilty.setOnAction(ev -> {
            client.clickEvent("not_guilty");
            closeVoteMenu();
        });
    }
    
    public void flush() {
        loginUserField.clear();
        loginPasswordField.clear();
    }
    
    @Override
    public void stop() {
        client.stop();
    }
    
    public static void main(String[] args) {
                        
        launch(args);     
        
    }
}

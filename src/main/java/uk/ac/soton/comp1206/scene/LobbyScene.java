package uk.ac.soton.comp1206.scene;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The LobbyScene is the gateway to the multiplayer challenge. Players can create/join channels and send messages within those channels.
 * The host of each channel can start a new multiplayer game
 */
public class LobbyScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(LobbyScene.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Listener that handles incoming messages from the Communicator
     */
    private CommunicationsListener listener;

    /**
     * Timer used to periodically request the channel list from the Communicator
     */
    private Timer timer;

    /**
     * Holds the list of channels
     */
    private VBox channelList;

    /**
     * TextField to allow the user to enter the new channel name
     */
    private TextField nameInput;

    /**
     * Holds the outermost container for the channel name and box
     */
    private VBox currentChannelBox;

    /**
     * Holds the current channel name
     */
    private Text currentChannelName;

    /**
     * Holds the elements relating to the current channel; shown when a channel is joined
     */
    private VBox currentChannel;

    /**
     * Holds the list of users in the current channel
     */
    private HBox usersList;

    /**
     * Holds the message ScrollPane
     */
    private ScrollPane messageScroller;

    /**
     * Holds all the messages in the channel
     */
    private VBox messageBox;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public LobbyScene(GameWindow gameWindow) {
        super(gameWindow);
        setSceneName("Lobby");
        logger.info("Creating lobby scene");

        setListener(this::handleResponses);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                gameWindow.getCommunicator().send("LIST");
            }
        }, 0, 5000);
    }

    public void build() {
        logger.info("Building " + this.getClass().getName());
        gameWindow.getCommunicator().addListener(listener);

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var lobbyPane = new StackPane();
        lobbyPane.setMaxWidth(gameWindow.getWidth());
        lobbyPane.setMaxHeight(gameWindow.getHeight());
        lobbyPane.getStyleClass().add("menu-background");
        root.getChildren().add(lobbyPane);

        var mainPane = new BorderPane();
        lobbyPane.getChildren().add(mainPane);

        var title = new Text("Multiplayer Lobby");
        title.getStyleClass().add("bigtitle");
        var titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10,0,0,0));
        mainPane.setTop(titleBox);

        // -----------------------------------------------------------------------------------------

        //Holds both the channel list and messages
        var multiBoxes = new HBox();
        multiBoxes.setAlignment(Pos.CENTER);
        multiBoxes.setPadding(new Insets(50,10,10,10));
        multiBoxes.setSpacing(5);
        mainPane.setCenter(multiBoxes);

        //------------------------------------------------------------------------------------------

        //Holds all the UI elements relating to the channels
        var channelListBox = new VBox();
        channelListBox.setAlignment(Pos.TOP_CENTER);
        channelListBox.setSpacing(10);

        var channelTitle = new Text("Channel List");
        channelTitle.getStyleClass().add("heading");

        nameInput = new TextField();
        nameInput.setVisible(false);
        nameInput.setMaxWidth(200);
        nameInput.setPromptText("Enter Channel Name...");

        var newButton = new Button("New Channel");
        newButton.setOnAction(e -> {
            Multimedia.playAudio("rotate.wav");
            newChannel(nameInput);
        });

        channelList = new VBox();
        channelList.setAlignment(Pos.TOP_CENTER);
        channelList.setSpacing(5);

        var channelScroller = new ScrollPane(channelList);
        channelScroller.setFitToWidth(true);
        channelScroller.getStyleClass().add("scroller");
        channelScroller.setPrefHeight(350);

        var channelContent = new VBox(newButton, nameInput, channelScroller);
        channelContent.setAlignment(Pos.TOP_CENTER);
        channelContent.setSpacing(10);
        channelContent.getStyleClass().add("gameBox");

        channelListBox.getChildren().addAll(channelTitle, channelContent);

        //------------------------------------------------------------------------------------------

        //Holds the UI elements relating to the current game
        currentChannelBox = new VBox();
        currentChannelBox.setAlignment(Pos.TOP_CENTER);
        currentChannelBox.setSpacing(10);

        currentChannelName = new Text();
        currentChannelName.getStyleClass().add("heading");

        currentChannel = new VBox();
        currentChannel.setPadding(new Insets(5));
        currentChannel.setSpacing(10);
        currentChannel.getStyleClass().add("gameBox");

        usersList = new HBox();
        usersList.getStyleClass().add("playerBox");
        usersList.setSpacing(5);

        var welcome = new Text("Welcome to the lobby!\nType /nick NewName to change your nickname");
        welcome.getStyleClass().add("instructions");

        messageBox = new VBox();
        messageBox.getStyleClass().add("messages");

        messageScroller = new ScrollPane();
        messageScroller.setPrefHeight(350);
        messageScroller.setContent(messageBox);
        messageScroller.setFitToWidth(true);
        messageScroller.getStyleClass().add("scroller");

        //Listener that auto scrolls to the bottom of the scroll pane
        messageBox.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            messageScroller.setVvalue(messageScroller.getVmax());
        });

        var messageInput = new TextField();
        messageInput.setPromptText("Send a new message");
        messageInput.setOnAction(e -> {
            sendMessage(messageInput.getText());
            messageInput.clear();
        });

        var startButton = new Button("Start Game");
        startButton.setMinWidth(90);
        startButton.setOnAction(e -> startGame());
        var startPane = new StackPane(startButton);

        var leaveButton = new Button("Leave Game");
        leaveButton.setMinWidth(90);
        leaveButton.setOnAction(e -> {
            Multimedia.playAudio("lifelose.wav");
            exitChannel();
            root.requestFocus();
        });
        var leavePane = new StackPane(leaveButton);

        var buttonBox = new HBox(startPane, leavePane);
        buttonBox.setSpacing(350);
        HBox.setHgrow(startPane, Priority.ALWAYS);
        HBox.setHgrow(leavePane, Priority.ALWAYS);

        currentChannel.getChildren().addAll(usersList, welcome, messageScroller, messageInput, buttonBox);

        currentChannelBox.getChildren().addAll(currentChannelName, currentChannel);

        channelListBox.prefWidthProperty().bind(multiBoxes.widthProperty().multiply(0.4));
        currentChannelBox.prefWidthProperty().bind(multiBoxes.widthProperty().multiply(0.6));

        multiBoxes.getChildren().addAll(channelListBox, currentChannelBox);
        currentChannelBox.setVisible(false);
    }

    public void initialise() {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                exitChannel();
                timer.cancel();
                logger.info("Going back to the menu");
                gameWindow.startMenu();
            } else {
                root.requestFocus();
            }
        });

        scene.setOnMouseClicked(e -> {
            if (!nameInput.getBoundsInParent().contains(e.getX(), e.getY())) {
                nameInput.setVisible(false);
            }
        });

        root.requestFocus();
    }

    /**
     * Registers the CommunicationsListener attached to the ScoresScene
     * @param listener listener
     */
    private void setListener(CommunicationsListener listener) {
        this.listener = listener;
    }

    /**
     * Handles various responses from the Communicator
     * @param response response
     */
    private void handleResponses(String response) {
        if (response.contains("CHANNELS")) {
            // Update channels list
            String[] channelArr = response.replace("CHANNELS ", "").split("\\n");

            Platform.runLater(() -> {
                channelList.getChildren().clear();
                for (String string : channelArr) {
                    var channel = new Text(string);
                    channel.getStyleClass().add("channelItem");
                    if (string.equals(currentChannelName.getText())) channel.getStyleClass().add("selected");
                    channel.setOnMouseClicked(e -> joinChannel(string));
                    channelList.getChildren().add(channel);
                }
            });

        } else if (response.contains("USERS")) {
            //Update users list of current channel
            String[] userArr = response.replace("USERS ","").split("\n");

            Platform.runLater(() -> {
                usersList.getChildren().clear();
                for (String user : userArr) {
                    usersList.getChildren().add(new Text(user));
                }
            });

        } else if (response.contains("NICK") && response.contains(":")) {
            //Change user's nickname
            String[] nickArr = response.replace("NICK ","").split(":");

            var oldName = nickArr[0];
            var newName = nickArr[1];

            Platform.runLater(() -> {
                for (Node user : usersList.getChildren()) {
                    if (user instanceof Text && ((Text) user).getText().equals(oldName)) {
                        ((Text) user).setText(newName);
                    }
                }
            });

        } else if (response.contains("JOIN")) {
            //Join a channel and display channel UI
            var channelName = response.replace("JOIN ","");

            Platform.runLater(() -> {
                for (Node channel : channelList.getChildren()) {
                    if (channel instanceof Text && ((Text) channel).getText().equals(channelName)) {
                        channel.getStyleClass().add("selected");
                    }
                }
                messageBox.getChildren().clear();
                currentChannelName.setText(channelName);
                currentChannelBox.setVisible(true);
            });
            Multimedia.playAudio("lifegain.wav");

            gameWindow.getCommunicator().send("LIST");

        } else if (response.contains("MSG")) {
            //Handle incoming message from server and display in chat box
            String[] msgArr = response.replace("MSG ","").split(":",2);

            var player = msgArr[0];
            var message = msgArr[1];

            var newName = Pattern.compile("^/nick .+$");
            var emptyNick = Pattern.compile("^/nick$");

            if (emptyNick.matcher(message).find()) {
                //Don't allow empty nicknames
                Platform.runLater(() -> {
                    var info = new Alert(AlertType.WARNING);
                    info.setTitle("Warning");
                    info.setContentText("Cannot set nickname to empty");
                    info.showAndWait();
                });

            } else if (newName.matcher(message).find()) {
                //Change user's nickname
                gameWindow.getCommunicator().send(String.format("NICK %s", message.split("k ",2)[1]));

            } else {
                var messageToSend = new Text();
                messageToSend.setText(String.format("[%s] <%s>: %s", formatter.format(LocalDateTime.now()), player, message));

                Platform.runLater(() -> {
                    messageBox.getChildren().add(messageToSend);
                });
                Multimedia.playAudio("message.wav");
            }

        } else if (response.contains("PARTED")) {
            //Handle leaving the current channel
            currentChannelName.setText("");
            currentChannelBox.setVisible(false);
            gameWindow.getCommunicator().send("LIST");

        } else if (response.contains("ERROR")) {
            //Display error dialog with error message
            var errorMessage = response.replace("ERROR ","");

            Platform.runLater(() -> {
                var alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Oops!");
                alert.setContentText(errorMessage);
                alert.showAndWait();
            });

        } else if (response.contains("START")) {
            timer.cancel();
            Platform.runLater(gameWindow::startMulti);

        } else if (response.contains("DIE")) {
            gameWindow.getCommunicator().send("USERS");
        }
    }

    /**
     * Creates a new channel
     * @param field TextField for typing the new channel name
     */
    private void newChannel(TextField field) {
        field.clear();
        field.setVisible(true);
        field.setOnAction(e -> {
            gameWindow.getCommunicator().send(String.format("CREATE %s", field.getText()));
            field.setVisible(false);
        });
    }

    /**
     * Sends a chat message to the Comunicator
     * @param message message
     */
    private void sendMessage(String message) {
        gameWindow.getCommunicator().send(String.format("MSG %s", message));
    }

    /**
     * Requests to join a channel
     * @param channel channel name
     */
    private void joinChannel(String channel) {
        gameWindow.getCommunicator().send(String.format("JOIN %s", channel));
    }

    /**
     * Leaves the current channel
     */
    private void exitChannel() {
        Platform.runLater(() -> {
            for (Node channel : channelList.getChildren()) {
                if (channel instanceof Text && ((Text) channel).getText().equals(currentChannelName.getText())) {
                    channel.getStyleClass().remove("selected");
                }
            }
        });
        gameWindow.getCommunicator().send("PART");
    }

    /**
     * Requests to start the game
     */
    private void startGame() {
        gameWindow.getCommunicator().send("START");
    }
}

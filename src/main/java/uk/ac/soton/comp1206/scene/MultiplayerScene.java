package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.Leaderboard;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Multi Player challenge scene. Holds the custom UI for a multiplayer game.
 */
public class MultiplayerScene extends ChallengeScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Container for the messages between the players
     */
    private VBox messageBox;

    /**
     * TextField to allow user to send messages
     */
    private TextField chatInput;

    /**
     * Holds the leaderboard for the current game
     */
    private Leaderboard leaderboard;

    /**
     * Holds every player's scores for the current game
     */
    private ListProperty<Pair<String, Integer>> scores;

    /**
     * Creates a new MultiPlayer challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        setSceneName("Multiplayer");
        initialiseLeaderboard();
    }

    public void setupGame() {
        logger.info("Starting new multiplayer challenge");

        game = new MultiplayerGame(5,5);
        game.setNextPieceListener(this::upcomingPiece);
        game.setLineClearedListener(this::lineCleared);
        game.setGameLoopListener(this::timer);
    }

    /**
     * Populates the leaderboard with the players in the game
     */
    private void initialiseLeaderboard() {
        scores = new SimpleListProperty<>();

        List<Pair<String, Integer>> testScores = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            testScores.add(new Pair<>("Player " + i, 100));
        }

        scores.set(FXCollections.observableArrayList(testScores));

        leaderboard = new Leaderboard(scores);
    }

    public void initialise() {
        scene.setOnKeyPressed(this::keyEvents);

        /*
        Removes focus from the text field when somewhere else on the screen is clicked
        Allows keybindings to still work when text field isn't "active"
         */
        root.requestFocus();
        scene.setOnMouseClicked(e -> {
            if (!chatInput.getBoundsInParent().contains(e.getX(), e.getY())) {
                root.requestFocus();
            }
        });

        logger.info("Initialising multiplayer");
        game.start();
        Multimedia.stopMusic();
        Multimedia.playMusic("game_start.wav", "game.wav", true);
    }

    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var multiPane = new StackPane();
        multiPane.setMaxWidth(gameWindow.getWidth());
        multiPane.setMaxHeight(gameWindow.getHeight());
        multiPane.getStyleClass().add("menu-background");
        root.getChildren().add(multiPane);

        var mainPane = new BorderPane();
        multiPane.getChildren().add(mainPane);

        //------------------------------------------------------------------------------------------

        var scoreBox = new VBox();
        scoreBox.setAlignment(Pos.CENTER);
        var scoreHeading = new Text("Score");
        var scoreNumber = new Text("0");
        scoreNumber.textProperty().bind(game.scoreProperty().asString("%d"));
        scoreHeading.getStyleClass().add("heading");
        scoreNumber.getStyleClass().add("score");
        scoreBox.getChildren().addAll(scoreHeading,scoreNumber);

        var livesBox = new VBox();
        livesBox.setAlignment(Pos.CENTER);
        var livesHeading = new Text("Lives");
        livesNumber = new Text("3");
        livesNumber.textProperty().bind(game.livesProperty().asString("%d"));
        livesHeading.getStyleClass().add("heading");
        livesNumber.getStyleClass().add("lives");
        livesBox.getChildren().addAll(livesHeading,livesNumber);

        var title = new Text("Multiplayer Mode");
        title.getStyleClass().add("title");

        var topRow = new HBox(scoreBox,title,livesBox);
        topRow.setAlignment(Pos.CENTER);
        topRow.setSpacing(150);
        topRow.setPadding(new Insets(10,0,0,0));

        mainPane.setTop(topRow);

        //------------------------------------------------------------------------------------------

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/3,gameWindow.getWidth()/3);

        //------------------------------------------------------------------------------------------

        var infoPane = new VBox();
        infoPane.setAlignment(Pos.CENTER);
        infoPane.setSpacing(10);

        var levelHeading = new Text("Level");
        levelHeading.getStyleClass().add("heading");

        var levelNumber = new Text("0");
        levelNumber.textProperty().bind(game.levelProperty().asString("%d"));
        levelNumber.getStyleClass().add("level");

        var upcomingHeader = new Text("Upcoming");
        upcomingHeader.getStyleClass().add("heading");

        infoPane.getChildren().addAll(levelHeading,levelNumber,upcomingHeader,upcomingPiece,followingPiece);

        //------------------------------------------------------------------------------------------

        var chatBox = new VBox();
        chatBox.setPadding(new Insets(5));
        chatBox.setSpacing(10);
        chatBox.getStyleClass().add("gameBox");

        var chatTitle = new Text("Messages");
        chatTitle.getStyleClass().add("heading");

        messageBox = new VBox();
        messageBox.getStyleClass().add("messages");

        var messageScroller = new ScrollPane(messageBox);
        messageScroller.setPrefHeight(200);
        messageScroller.setFitToWidth(true);
        messageScroller.getStyleClass().add("scroller");

        messageBox.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            messageScroller.setVvalue(messageScroller.getVmax());
        });

        chatInput = new TextField();
        chatInput.setPromptText("Send a new message");
        chatInput.setOnAction(e -> {
            sendMessage(chatInput.getText());
            chatInput.clear();
        });

        chatBox.getChildren().addAll(chatTitle, messageScroller, chatInput);

        //------------------------------------------------------------------------------------------

        var onlineBox = new VBox(chatBox, leaderboard);
        onlineBox.setSpacing(10);
        onlineBox.setAlignment(Pos.CENTER);
        leaderboard.reveal();

        //------------------------------------------------------------------------------------------

        var mainHBox = new HBox(board, infoPane, onlineBox);
        mainHBox.setAlignment(Pos.CENTER);
        mainHBox.setPadding(new Insets(10));
        mainHBox.setSpacing(50);
        HBox.setHgrow(onlineBox, Priority.ALWAYS);

        mainPane.setCenter(mainHBox);

        //------------------------------------------------------------------------------------------

        timerBar = new Rectangle(gameWindow.getWidth(), 30, Color.GREEN);

        mainPane.setBottom(timerBar);

        //------------------------------------------------------------------------------------------

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        //Handle gameboard being right-clicked
        board.setOnRightClicked(this::blockRightClicked);
    }

    /**
     * Sends a chat message to the Communicator for displaying in the chat box
     * @param message message
     */
    private void sendMessage(String message) {
        gameWindow.getCommunicator().send(String.format("MSG %s", message));
    }

    protected void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }
}

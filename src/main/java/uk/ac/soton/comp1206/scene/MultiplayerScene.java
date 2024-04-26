package uk.ac.soton.comp1206.scene;

import static java.lang.Integer.compare;
import static java.lang.Integer.parseInt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import javafx.application.Platform;
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
import uk.ac.soton.comp1206.component.LeaderBoard;
import uk.ac.soton.comp1206.event.CommunicationsListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Multi Player challenge scene. Holds the custom UI for a multiplayer game.
 */
public class MultiplayerScene extends ChallengeScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Listener that handles incoming messages from the Communicator
     */
    private CommunicationsListener listener;

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
    private LeaderBoard leaderboard;

    /**
     * Holds every player's scores for the current game
     */
    private ListProperty<Pair<Pair<String, String>, String>> playerScores;

    /**
     * Timer used to periodically request a new GamePiece from the Communicator
     */
    private Timer timer;

    /**
     * Creates a new MultiPlayer challenge scene
     *
     * @param gameWindow the Game Window
     */
    public MultiplayerScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Multiplayer scene");
        setListener(this::handleComms);

        setSceneName("Multiplayer");

        playerScores = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    /**
     * Registers the CommunicationsListener attached to the scene
     * @param listener listener
     */
    public void setListener(CommunicationsListener listener) {
        this.listener = listener;
    }

    public void setupGame() {
        logger.info("Starting new multiplayer challenge");

        game = new MultiplayerGame(5,5);

        timer = new Timer();
        timerList.add(timer);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                gameWindow.getCommunicator().send("PIECE");
            }
        }, 0, 500);

        game.setNextPieceListener(this::upcomingPiece);
        game.setLineClearedListener(this::lineCleared);
        game.setGameLoopListener(this::timer);
    }

    public void initialise() {
        logger.info("Initialising multiplayer");
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

        //Delays the start of the game so a few GamePieces can be populated
        var pause = new Timer();
        timerList.add(pause);
        pause.schedule(new TimerTask() {
            public void run() {
                logger.info("Game started");
                game.start();
                Multimedia.stopMusic();
                Multimedia.playMusic("game_start.wav", "game.wav", true);
            }
        }, 500);
    }

    public void build() {
        logger.info("Building " + this.getClass().getName());
        gameWindow.getCommunicator().addListener(listener);
        gameWindow.getCommunicator().send("SCORES");

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

        scoreNumber.textProperty().addListener((observableValue, oldValue, newValue) -> updateScore(newValue));

        scoreHeading.getStyleClass().add("heading");
        scoreNumber.getStyleClass().add("score");
        scoreBox.getChildren().addAll(scoreHeading,scoreNumber);

        var livesBox = new VBox();
        livesBox.setAlignment(Pos.CENTER);
        var livesHeading = new Text("Lives");
        livesNumber = new Text("3");
        livesNumber.textProperty().bind(game.livesProperty().asString("%d"));

        livesNumber.textProperty().addListener((observableValue, oldValue, newValue) -> updateLives(newValue));

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
        messageScroller.setPrefHeight(100);
        messageScroller.setFitToWidth(true);
        messageScroller.getStyleClass().add("scroller");

        messageBox.heightProperty().addListener((observableValue, oldValue, newValue) -> messageScroller.setVvalue(messageScroller.getVmax()));

        chatInput = new TextField();
        chatInput.setPromptText("Send a new message");
        chatInput.setOnAction(e -> {
            sendMessage(chatInput.getText());
            chatInput.clear();
        });

        chatBox.getChildren().addAll(chatTitle, messageScroller, chatInput);

        //------------------------------------------------------------------------------------------

        leaderboard = new LeaderBoard();
        leaderboard.getScores().bind(playerScores);

        var leaderText = new Text("Leaderboard (Score:Lives)");
        leaderText.getStyleClass().add("heading");
        leaderText.getStyleClass().add("leader");

        //------------------------------------------------------------------------------------------

        var boardScroller = new ScrollPane(leaderboard);
        boardScroller.setFitToWidth(true);
        boardScroller.setPrefHeight(200);
        boardScroller.getStyleClass().add("scroller");
        boardScroller.setVvalue(0);

        //------------------------------------------------------------------------------------------

        var onlineBox = new VBox(chatBox, leaderText, boardScroller);
        onlineBox.setSpacing(10);
        onlineBox.setAlignment(Pos.CENTER);

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
     * Handles various responses from the Communicator
     * @param response response
     */
    private void handleComms(String response) {
        var score = Pattern.compile("^SCORE .+$");
        var scores = Pattern.compile("^SCORES .+$", Pattern.DOTALL);
        var piece = Pattern.compile("^PIECE \\d+$");
        var message = Pattern.compile("^MSG .+$");
        var die = Pattern.compile("^DIE .+$");

        if (score.matcher(response).find()) {
            logger.info("Handling SCORE");
            String[] scoreInfo = response.replace("SCORE ","").split(":");
            var name = scoreInfo[0];
            var playerScore = scoreInfo[1];

            //Replace old score with new score
            for (Pair<Pair<String, String>, String> player : playerScores) {
                if (player.getKey().getKey().equals(name)) {
                    playerScores.set(playerScores.indexOf(player), new Pair<>(new Pair<>(player.getKey().getKey(), playerScore), player.getValue()));
                    break;
                }
            }

        } else if (scores.matcher(response).find()) {
            logger.info("Handling SCORES");
            String[] scoreArr = response.replace("SCORES ","").split("\n");
            var loadedPlayers = new ArrayList<Pair<Pair<String, String>, String>>();

            for (String player : scoreArr) {
                String[] playerInfo = player.split(":");
                var name = playerInfo[0];
                var playerScore = playerInfo[1];
                var lives = playerInfo[2];

                loadedPlayers.add(new Pair<>(new Pair<>(name, playerScore), lives));
            }

            loadedPlayers.sort((o1, o2) -> compare(parseInt(o2.getKey().getValue()), parseInt(o1.getKey().getValue())));

            if (playerScores.isEmpty()) {
                playerScores.set(FXCollections.observableArrayList(loadedPlayers));
            } else {
                playerScores.clear();
                playerScores.addAll(FXCollections.observableArrayList(loadedPlayers));
            }

        } else if (piece.matcher(response).find()) {
            logger.info("Requesting PIECE");
            var value = response.replace("PIECE ","");
            getNextPiece(parseInt(value));

        } else if (message.matcher(response).find()) {
            logger.info("Handling MESSAGE");
            String[] msgArr = response.replace("MSG ","").split(":",2);

            var player = msgArr[0];
            var chat = msgArr[1];

            var messageToSend = new Text();
            messageToSend.setText(String.format("[%s] <%s>: %s", formatter.format(LocalDateTime.now()), player, chat));

            Platform.runLater(() -> messageBox.getChildren().add(messageToSend));
            Multimedia.playAudio("message.wav");

        } else if (die.matcher(response).find()) {
            gameWindow.getCommunicator().send("SCORES");
        }
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

    /**
     * Adds a GamePiece to the queue
     * @param value value of the next game piece
     */
    public void getNextPiece(int value) {
        ((MultiplayerGame) game).enqueuePiece(GamePiece.createPiece(value));
    }

    /**
     * Sends the player's current score to the Communicator
     * @param score score
     */
    public void updateScore(String score) {
        gameWindow.getCommunicator().send("SCORE " + score);
    }

    /**
     * Sends the player's current lives to the Communicator
     * @param lives lives
     */
    public void updateLives(String lives) {
        gameWindow.getCommunicator().send("LIVES " + lives);
    }

    protected void quitScene() {
        gameWindow.getCommunicator().send("DIE");
        ((MultiplayerGame) game).setPlayerScores(playerScores);
        super.quitScene();
        gameWindow.startMultiScores(game);
    }
}

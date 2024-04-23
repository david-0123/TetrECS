package uk.ac.soton.comp1206.scene;

import static java.lang.Integer.parseInt;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import javafx.animation.FillTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Holds the game linked to the challenge scene
     */
    protected Game game;

    /**
     * Holds the PieceBoard representation of the next piece
     */
    private PieceBoard upcomingPiece;

    /**
     * Holds the PieceBoard representation of the following piece
     */
    private PieceBoard followingPiece;

    /**
     * Holds the GameBoard UI element
     */
    private GameBoard board;

    /**
     * Holds the UI timer
     */
    private Rectangle timerBar;

    /**
     * Holds the current time remaining on the timer
     */
    private double currentTime;

    /**
     * Holds the Timeline object the UI timer bar uses
     */
    private Timeline timeline;

    /**
     * Holds the text representation of the current high score
     */
    private Text hiScoreNumber;

    /**
     * Holds the text representation of the amount of lives left
     */
    private Text livesNumber;

    /**
     * Creates a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        setSceneName("Challenge");
        logger.info("Creating Challenge Scene");
        upcomingPiece = new PieceBoard(3,3, gameWindow.getWidth()/6, gameWindow.getHeight()/5);
        followingPiece = new PieceBoard(3,3, gameWindow.getWidth()/7, gameWindow.getHeight()/6);
        upcomingPiece.setOnMouseClicked(this::pieceClicked);
        followingPiece.setOnMouseClicked(this::swapPieces);

        timeline = new Timeline(
            new KeyFrame(Duration.millis(1), e -> updateTimerBar()) //Update the timer bar every millisecond
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    /**
     * Builds the Challenge scene
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        /*
        For each UI element, the element's title and value are separate Text objects contained in a VBox
        The values are also bound to their respective properties in the Game class
         */

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
        var livesHeading = new Text("Lives Left");
        livesNumber = new Text("3");
        livesNumber.textProperty().bind(game.livesProperty().asString("%d"));
        livesHeading.getStyleClass().add("heading");
        livesNumber.getStyleClass().add("lives");
        livesBox.getChildren().addAll(livesHeading,livesNumber);

        var infoPane = new VBox();
        infoPane.setAlignment(Pos.CENTER);
        infoPane.setPadding(new Insets(0,20,0,0)); //Adds a right margin
        infoPane.setSpacing(10);

        var hiScoreBox = new VBox();
        hiScoreBox.setAlignment(Pos.CENTER);
        var hiScoreHeading = new Text("High Score");
        hiScoreHeading.getStyleClass().add("heading");
        getHighScore();
        hiScoreNumber.getStyleClass().add("hiscore");
        hiScoreBox.getChildren().addAll(hiScoreHeading,hiScoreNumber);

        var levelBox = new VBox();
        levelBox.setAlignment(Pos.CENTER);
        var levelHeading = new Text("Level");
        var levelNumber = new Text("0");
        levelNumber.textProperty().bind(game.levelProperty().asString("%d"));
        levelHeading.getStyleClass().add("heading");
        levelNumber.getStyleClass().add("level");
        levelBox.getChildren().addAll(levelHeading,levelNumber);

        var pieceBox = new VBox();
        pieceBox.setAlignment(Pos.CENTER);
        pieceBox.setSpacing(10);
        var upcomingHeader = new Text("Upcoming");
        upcomingHeader.getStyleClass().add("heading");
        pieceBox.getChildren().addAll(upcomingHeader,upcomingPiece,followingPiece);

        infoPane.getChildren().addAll(hiScoreBox,levelBox,pieceBox);

        var title = new Text("Challenge Mode");
        title.getStyleClass().add("title");

        var topRow = new HBox(scoreBox,title,livesBox);
        topRow.setAlignment(Pos.CENTER);
        topRow.setSpacing(150);
        topRow.setPadding(new Insets(10,10,0,0));

        timerBar = new Rectangle(gameWindow.getWidth(), 30, Color.GREEN);

        mainPane.setTop(topRow);
        mainPane.setRight(infoPane);
        mainPane.setBottom(timerBar);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        //Handle gameboard being right-clicked
        board.setOnRightClicked(this::blockRightClicked);
    }

    /**
     * Handles when a block is clicked
     * @param gameBlock the Game Block that was clicked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
        //Updates the high score as the user exceeds it
        if (game.getScore() > parseInt(hiScoreNumber.getText())) {
            hiScoreNumber.setText(String.valueOf(game.getScore()));
        }
    }

    /**
     * Handles when the GameBoard is right-clicked
     * @param block block that was clicked
     */
    private void blockRightClicked(GameBlock block) {
        game.rotateCurrentPiece();
        upcomingPiece.displayPiece(game.getCurrentPiece());
        upcomingPiece.paintIndicator();
    }

    /**
     * Handles when the current piece is left-clicked
     * @param event Mouse event
     */
    private void pieceClicked(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            logger.info("Current piece clicked");
            game.rotateCurrentPiece();
            upcomingPiece.displayPiece(game.getCurrentPiece());
            upcomingPiece.paintIndicator();
        }
    }

    /**
     * Defines all the key events attached to the scene
     * @param event key event
     */
    private void keyEvents(KeyEvent event) {
        if (event.getCode() == KeyCode.E || event.getCode() == KeyCode.C || event.getText().equals("]")) {
            logger.info("Rotate right");
            game.rotateCurrentPiece();
            upcomingPiece.displayPiece(game.getCurrentPiece());
            upcomingPiece.paintIndicator();
        } else if (event.getCode() == KeyCode.Q || event.getCode() == KeyCode.Z || event.getText().equals("[")) {
            logger.info("Rotate left");
            game.rotateCurrentPiece(3);
            upcomingPiece.displayPiece(game.getCurrentPiece());
            upcomingPiece.paintIndicator();
        } else if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.R) {
            game.swapCurrentPiece();
            displayPieces();
        } else if (event.getCode() == KeyCode.ESCAPE) {
            escape();
        }
    }

    /**
     * Handles leaving the challenge scene and going back to the menu
     */
    private void escape() {
        logger.info("Leaving Challenge scene");
        Multimedia.stopMusic();
        Multimedia.playMusic("menu.mp3", true);
        logger.info("Going back to the menu");
        gameWindow.startMenu();
    }

    /**
     * Handles when the following piece is clicked in order to swap the current and following pieces
     * @param event Mouse event
     */
    private void swapPieces(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            logger.info("Following piece clicked");
            game.swapCurrentPiece();
            displayPieces();
        }
    }

    /**
     * Sets up the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
        game.setNextPieceListener(this::upcomingPiece);
        game.setLineClearedListener(this::lineCleared);
        game.setGameLoopListener(this::timer);
    }

    /**
     * Initialises the scene and start the game
     */
    public void initialise() {
        scene.setOnKeyPressed(this::keyEvents);
        logger.info("Initialising Challenge");
        game.start();
        Multimedia.stopMusic();
        Multimedia.playMusic("game_start.wav", "game.wav", true);
    }

    /**
     * Handles the visual display of the current and following pieces
     * @param currentPiece current piece
     * @param followPiece following piece
     */
    private void upcomingPiece(GamePiece currentPiece, GamePiece followPiece) {
        displayPieces();
    }

    /**
     * Handles the visual fade out animation when a line is cleared
     * @param coords set of blocks to fade out
     */
    private void lineCleared(HashSet<GameBlockCoordinate> coords) {
        board.fadeOut(coords);
    }

    /**
     * Method linked to the Game timer via the GameLoopListener
     */
    private void timer() {
        //Stops the previous timeline before starting a new one
        if (timeline != null) {
            timeline.stop();
        }

        currentTime = game.getTimerDelay();
        logger.info("UI timer started");

        timeline.playFromStart();
    }

    /**
     * Called every millisecond to resize the timer bar and colour if necessary
     */
    private void updateTimerBar() {
        //Scales the width of the timer based on remaining time
        var ratio = currentTime / game.getTimerDelay();
        timerBar.setWidth(ratio * gameWindow.getWidth());

        Color targetColor;

        if (ratio > 0.5) {
            targetColor = Color.GREEN;
        } else if (ratio > 0.4) {
            targetColor = Color.GREENYELLOW;
        } else if (ratio > 0.3) {
            targetColor = Color.ORANGE;
        } else if (ratio > 0.2) {
            targetColor = Color.ORANGERED;
        }  else {
            targetColor = Color.RED;
        }

        //Start gradually changing colour once half the time has elapsed
        if (ratio <= 0.5) {
            var transition = new FillTransition(Duration.millis(200), timerBar, (Color) timerBar.getFill(), targetColor);
            transition.play();
        } else {
            timerBar.setFill(Color.GREEN);
        }

        currentTime -= 1;

        isGameOver();
    }

    /**
     * Checks if the user has run out of lives
     */
    private void isGameOver() {
        //Prevents -1 being shown on the screen for a split second
        if (game.getLives() - 1 < 0) livesNumber.textProperty().unbind();

        if (game.getLives() < 0) {
            timeline.stop();
            logger.info("Leaving Challenge scene");
            Multimedia.stopMusic();
            Multimedia.playMusic("menu.mp3", true);
            gameWindow.startScores(game);
        }
    }

    /**
     * Handles the UI rendering of the current and following PieceBoards
     */
    private void displayPieces() {
        upcomingPiece.displayPiece(game.getCurrentPiece());
        upcomingPiece.paintIndicator();
        followingPiece.displayPiece(game.getFollowingPiece());
    }

    /**
     * Gets the highest local score and displays it in the UI
     */
    public void getHighScore() {
        try{
            var reader = new BufferedReader(new FileReader("scores.txt"));
            var hiScore = reader.readLine();
            var score = hiScore.split(":")[1]; //Name:Score
            hiScoreNumber = new Text(String.format("%d", parseInt(score)));
        } catch (FileNotFoundException e) {
            logger.error("Scores file couldn't be found");
            hiScoreNumber = new Text("0");
        } catch (IOException e) {
            logger.error("Something went wrong trying to read the scores file");
            hiScoreNumber = new Text("0");
        }
    }
}

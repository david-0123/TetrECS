package uk.ac.soton.comp1206.scene;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    /**
     * Create a new Single Player challenge scene
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
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

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
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
        var livesHeading = new Text("Lives");
        var livesNumber = new Text("3");
        livesNumber.textProperty().bind(game.livesProperty().asString("%d"));
        livesHeading.getStyleClass().add("heading");
        livesNumber.getStyleClass().add("lives");
        livesBox.getChildren().addAll(livesHeading,livesNumber);

        var infoPane = new VBox();
        infoPane.setAlignment(Pos.CENTER);
        infoPane.setPadding(new Insets(0,10,0,0)); //Adds a right margin
        infoPane.setSpacing(10);

        var hiScoreBox = new VBox();
        hiScoreBox.setAlignment(Pos.CENTER);
        var hiScoreHeading = new Text("High Score");
        var hiScoreNumber = new Text("100");
        hiScoreHeading.getStyleClass().add("heading");
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
        var currentPiece = new PieceBoard(3,3, gameWindow.getWidth()/6, gameWindow.getHeight()/5);
        var nextPiece = new PieceBoard(3,3, gameWindow.getWidth()/7, gameWindow.getHeight()/6);
        pieceBox.getChildren().addAll(upcomingHeader,currentPiece,nextPiece);

        infoPane.getChildren().addAll(hiScoreBox,levelBox, pieceBox);

        var title = new Text("Challenge Mode");
        title.getStyleClass().add("title");

        var topRow = new HBox(scoreBox,title,livesBox);
        topRow.setAlignment(Pos.CENTER);
        topRow.setSpacing(150);
        topRow.setPadding(new Insets(10,0,0,0));

        mainPane.setTop(topRow);
        mainPane.setRight(infoPane);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);

        Multimedia.stopMusic();
        Multimedia.playMusic("game_start.wav", "game.wav", true);
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clocked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();
    }

}

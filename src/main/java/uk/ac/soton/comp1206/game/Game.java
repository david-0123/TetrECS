package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;

    /**
     * The current game piece the user has to place
     */
    private static GamePiece currentPiece;

    private IntegerProperty score;

    private IntegerProperty level;

    private IntegerProperty lives;

    private IntegerProperty multiplier;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);

        score = new SimpleIntegerProperty(0);
        level = new SimpleIntegerProperty(0);
        lives = new SimpleIntegerProperty(3);
        multiplier = new SimpleIntegerProperty(1);

    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        logger.info("Initialising first piece");
        currentPiece = spawnPiece();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        grid.playPiece(currentPiece, x, y);
        afterPiece();
    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Creates a random game piece
     * @return the created game piece
     */
    public static GamePiece spawnPiece() {
        int rnd = new Random().nextInt(GamePiece.PIECES);
        logger.info("Created {} piece", GamePiece.createPiece(rnd));
        return GamePiece.createPiece(rnd);
    }

    /**
     * Gets the next game piece the user has to place
     */
    public static void nextPiece() {
        logger.info("Getting new piece");
        currentPiece = spawnPiece();
    }

    /**
     * Handle the logic to clear any lines after a piece is played
     */
    public void afterPiece() {
        logger.info("Checking for lines to clear");
        int linesToClear = 0;
        HashSet<GameBlockCoordinate> blocksToClear = new HashSet<>();

        //Iterate through rows
        for (int i = 0; i < rows; i++) {
            int count = 0;
            for (int j = 0; j < cols; j++) {
                if (grid.get(j,i) >= 1) count++;
            }

            //If line is full...
            if (count == cols) {
                logger.info("Clearing rows...");
                linesToClear++;
                for (int j = 0; j < cols; j++) {
                    GameBlockCoordinate blockCoordinate = new GameBlockCoordinate(j,i);
                    blocksToClear.add(blockCoordinate);
                    logger.info("{} will be cleared", blockCoordinate);
                }
            }
        }

        //Iterate through columns
        for (int i = 0; i < rows; i++) {
            int count = 0;
            for (int j = 0; j < cols; j++) {
                if (grid.get(i,j) >= 1) count++;
            }

            //If line is full...
            if (count == cols) {
                logger.info("Clearing columns...");
                linesToClear++;
                for (int j = 0; j < cols; j++) {
                    GameBlockCoordinate blockCoordinate = new GameBlockCoordinate(i,j);
                    blocksToClear.add(blockCoordinate);
                    logger.info("{} will be cleared", blockCoordinate);
                }
            }
        }

        try {
            for (GameBlockCoordinate blockCoordinate : blocksToClear) {
                grid.set(blockCoordinate.getX(), blockCoordinate.getY(), 0);
            }
            logger.info("{} lines cleared", linesToClear);
        } catch (NullPointerException ignored) {}

        score(linesToClear, blocksToClear.size());
        multiplier(linesToClear);
        level();
    }

    /**
     * Calculates the player's score
     * @param lines the number of lines cleared by the piece
     * @param blocks the number of blocks cleared
     */
    public void score(int lines, int blocks) {
        int score = lines * blocks * 10 * getMultiplier();
        setScore(getScore() + score);
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * Calculates the user's current level
     */
    public void level() {
        setLevel(getScore() / 1000);
    }

    public void setLevel(int level) {
        this.level.set(level);
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public IntegerProperty livesProperty() {
        return lives;
    }

    /**
     * Calculates the current multiplier after a piece is played/expires
     * @param linesCleared
     */
    public void multiplier(int linesCleared) {
        if (linesCleared >= 1) {
            setMultiplier(getMultiplier() + 1);
        } else {
            setMultiplier(1);
        }
    }

    public void setMultiplier(int multiplier) {
        this.multiplier.set(multiplier);
    }

    public int getMultiplier() {
        return multiplier.get();
    }

    public IntegerProperty multiplierProperty() {
        return multiplier;
    }
}

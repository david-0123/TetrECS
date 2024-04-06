package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
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

    private static GamePiece currentPiece;

    private HashSet<GameBlockCoordinate> blocksToClear;

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

    public static GamePiece spawnPiece() {
        int rnd = new Random().nextInt(GamePiece.PIECES);
        logger.info("Created {} piece", GamePiece.createPiece(rnd).toString());
        return GamePiece.createPiece(rnd);
    }

    public static void nextPiece() {
        logger.info("Getting new piece");
        currentPiece = spawnPiece();
    }

    public void afterPiece() {
        logger.info("Checking for lines to clear");
        int linesToClear = 0;
        blocksToClear = new HashSet<>();

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
                    logger.info("{} will be cleared", blockCoordinate.toString());
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
                    logger.info("{} will be cleared", blockCoordinate.toString());
                }
            }
        }

        try {
            for (GameBlockCoordinate blockCoordinate : blocksToClear) {
                System.out.println(blockCoordinate);
            }
        } catch (NullPointerException e) {

        }
    }
}

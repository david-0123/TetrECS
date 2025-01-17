package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

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
    protected GamePiece currentPiece;

    /**
     * The game piece that comes after the current piece
     */
    protected GamePiece followingPiece;

    /**
     * The user's score
     */
    protected IntegerProperty score;

    /**
     * The user's current level
     */
    protected IntegerProperty level;

    /**
     * The number of lives the user has left
     */
    protected IntegerProperty lives;

    /**
     * The user's current score multiplier
     */
    protected IntegerProperty multiplier;

    /**
     * The listener to call when a new piece is generated by the game
     */
    protected NextPieceListener nextPieceListener;

    /**
     * The listener to call when a line is cleared
     */
    protected LineClearedListener lineClearedListener;

    /**
     * Holds how long until the next piece must be played
     */
    protected Timer gameTimer;

    /**
     * The listener to call while the timer is running
     */
    protected GameLoopListener gameLoopListener;

    /**
     * Creates a new game with the specified rows and columns. Creates a corresponding grid model.
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
     * Starts the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialises a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        followingPiece = spawnPiece();
        nextPiece();
        startTimer();
    }

    /**
     * Handles what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        boolean piecePlayed = grid.playPiece(currentPiece, x, y);
        if (piecePlayed) {
            nextPiece();
            afterPiece();
            restartTimer();
        } else Multimedia.playAudio("fail.wav");
    }

    /**
     * Gets the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Gets the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Gets the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Handles the swapping of the current piece and the following piece
     */
    public void swapCurrentPiece() {
        logger.info("Swapping current and following piece");
        Multimedia.playAudio("rotate.wav");
        var temp = currentPiece;
        currentPiece = followingPiece;
        followingPiece = temp;
    }

    /**
     * Creates a random game piece
     * @return the created game piece
     */
    protected GamePiece spawnPiece() {
        int rnd = new Random().nextInt(GamePiece.PIECES);
        logger.info("Created {} piece", GamePiece.createPiece(rnd));
        return GamePiece.createPiece(rnd);
    }

    /**
     * Gets the next game piece the user has to place
     */
    protected void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = spawnPiece();

        if (nextPieceListener != null) {
            nextPieceListener.nextPiece(currentPiece, followingPiece);
        }
    }

    /**
     * Handles the logic to clear any lines after a piece is played
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
                linesToClear++;
                for (int j = 0; j < cols; j++) {
                    GameBlockCoordinate blockCoordinate = new GameBlockCoordinate(j,i);
                    blocksToClear.add(blockCoordinate);
                }
            }
        }

        //Iterate through columns
        for (int i = 0; i < cols; i++) {
            int count = 0;
            for (int j = 0; j < rows; j++) {
                if (grid.get(i,j) >= 1) count++;
            }

            //If line is full...
            if (count == rows) {
                linesToClear++;
                for (int j = 0; j < rows; j++) {
                    GameBlockCoordinate blockCoordinate = new GameBlockCoordinate(i,j);
                    blocksToClear.add(blockCoordinate);
                }
            }
        }

        if (!blocksToClear.isEmpty()) {
            for (GameBlockCoordinate blockCoordinate : blocksToClear) {
                grid.set(blockCoordinate.getX(), blockCoordinate.getY(), 0);
            }

            if (lineClearedListener != null) {
                lineClearedListener.lineCleared(blocksToClear);
            }
            logger.info("{} lines cleared", linesToClear);
            Multimedia.playAudio("clear.wav");
        }

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

    /**
     * Sets the user's score
     * @param score score
     */
    public void setScore(int score) {
        this.score.set(score);
    }

    /**
     * Gets the user's score
     * @return the score
     */
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
        var oldLevel = getLevel();
        var newLevel = getScore() / 1000;
        if (newLevel > oldLevel) Multimedia.playAudio("level.wav");
        setLevel(newLevel);
    }

    /**
     * Sets the user's current level
     * @param level level
     */
    public void setLevel(int level) {
        this.level.set(level);
    }

    /**
     * Gets the user's current level
     * @return the level
     */
    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public IntegerProperty livesProperty() {
        return lives;
    }

    /**
     * Sets the user's remaining lives
     * @param lives lives
     */
    public void setLives(int lives) {
        this.lives.set(lives);
    }

    /**
     * Gets the user's remaining lives
     * @return lives
     */
    public int getLives() {
        return lives.get();
    }

    /**
     * Calculates the current multiplier after a piece is played/time expires
     * @param linesCleared the number of lines cleared by a piece
     */
    public void multiplier(int linesCleared) {
        if (linesCleared >= 1) {
            setMultiplier(getMultiplier() + 1);
        } else {
            setMultiplier(1);
        }
    }

    /**
     * Sets the current multiplier
     * @param multiplier multiplier
     */
    public void setMultiplier(int multiplier) {
        this.multiplier.set(multiplier);
    }

    /**
     * Gets the current multiplier
     * @return the multiplier
     */
    public int getMultiplier() {
        return multiplier.get();
    }

    /**
     * Sets the NextPieceListener attached to the Game
     * @param nextPieceListener listener
     */
    public void setNextPieceListener(NextPieceListener nextPieceListener) {
        this.nextPieceListener = nextPieceListener;
    }

    /**
     * Sets the LineClearedListener attached to the Game
     * @param lineClearedListener listener
     */
    public void setLineClearedListener(LineClearedListener lineClearedListener) {
        this.lineClearedListener = lineClearedListener;
    }

    /**
     * Sets the GameLoopListener attached to the Game
     * @param gameLoopListener listener
     */
    public void setGameLoopListener(GameLoopListener gameLoopListener) {
        this.gameLoopListener = gameLoopListener;
    }

    /**
     * Rotates the current piece once to the right
     */
    public void rotateCurrentPiece() {
        logger.info("Rotating current piece");
        currentPiece.rotate();
    }

    /**
     * Rotates the current piece a specified amount of times
     * @param rotations number of rotations
     */
    public void rotateCurrentPiece(int rotations) {
        logger.info("Rotating current piece");
        currentPiece.rotate(rotations);
    }

    /**
     * Gets the current piece
     * @return the current game piece
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Gets the following piece
     * @return the following game piece
     */
    public GamePiece getFollowingPiece() {
        return followingPiece;
    }

    /**
     * Calculates the length of the game timer
     * @return timer length
     */
    public double getTimerDelay() {
        return Math.max(2500, 12000-(500*getLevel()));
    }

    /**
     * Starts the game timer
     */
    private void startTimer() {
        logger.info("Game timer started");
        if (gameLoopListener != null) {
            gameLoopListener.setOnGameLoop();
        }

        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                gameLoop();
            }
        }, (long) getTimerDelay(), (long) getTimerDelay());
    }

    /**
     * Called when the timer reaches the end and deducts a life.<br>
     * Checks if the user has run out of lives in which case the game is over.<br>
     * Otherwise, get the next piece, reset the multiplier and restart the timer
     */
    private void gameLoop() {
        logger.info("Timer ran out");
        setLives(getLives() - 1);
        Multimedia.playAudio("lifelose.wav");
        if (getLives() < 0) {
            stopGame();
        } else {
            nextPiece();
            setMultiplier(1);
            restartTimer();
        }
    }

    /**
     * Cancels the timer and restarts it from the beginning
     */
    private void restartTimer() {
        stopGame();
        startTimer();
    }

    /**
     * Stops the game timer
     */
    public void stopGame() {
        gameTimer.cancel();
    }
}

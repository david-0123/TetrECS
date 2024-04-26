package uk.ac.soton.comp1206.game;

import java.util.LinkedList;
import java.util.Queue;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The MultiplayerGame extends the base Game and implements a queue that the players get their next pieces from
 */
public class MultiplayerGame extends Game {

    private static final Logger logger = LogManager.getLogger(MultiplayerGame.class);

    /**
     * Holds the queue of pieces to be given to players
     */
    Queue<GamePiece> pieceQueue;

    /**
     * Holds every player's scores for the current game
     */
    private ListProperty<Pair<Pair<String, String>, String>> playerScores;

    /**
     * Creates a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
        playerScores = new SimpleListProperty<>();
        pieceQueue = new LinkedList<>();
    }

    /**
     * Add a game piece to the queue
     * @param piece game piece
     */
    public void enqueuePiece(GamePiece piece) {
        pieceQueue.add(piece);
    }

    /**
     * Remove the next game piece from the queue
     * @return the next game piece
     */
    public GamePiece dequeuePiece() {
        return pieceQueue.remove();
    }

    protected GamePiece spawnPiece() {
        return dequeuePiece();
    }

    /**
     * Sets the list of player scores
     * @param playerScores ObservableList containing player scores
     */
    public void setPlayerScores(ListProperty<Pair<Pair<String, String>, String>> playerScores) {
        this.playerScores.set(playerScores);
    }

    /**
     * Gets the scores property for the game
     * @return ObservableList containing the scores
     */
    public ObservableList<Pair<Pair<String, String>, String>> getPlayerScores() {
        return playerScores.get();
    }
}

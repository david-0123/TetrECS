package uk.ac.soton.comp1206.game;

import java.util.LinkedList;
import java.util.Queue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

public class MultiplayerGame extends Game {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Holds the queue of pieces to be given to players
     */
    Queue<GamePiece> pieceQueue;

    /**
     * Creates a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param cols number of columns
     * @param rows number of rows
     */
    public MultiplayerGame(int cols, int rows) {
        super(cols, rows);
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
}

package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * The PieceBoard is a mini version of the GameBoard that shows the current and following pieces to
 * be played along with a small indicator circle to show where the piece is being played
 */
public class PieceBoard extends GameBoard {
    private static final Logger logger = LogManager.getLogger(PieceBoard.class);

    /**
     * Makes a new PieceBoard for showing upcoming pieces
     *
     * @param grid existing grid object
     * @param width width
     * @param height height
     */
    public PieceBoard(Grid grid, double width, double height) {
      super(grid, width, height);
    }

    /**
     * Makes a new PieceBoard for showing upcoming pieces
     *
     * @param cols number of columns
     * @param rows number of rows
     * @param width width
     * @param height height
     */
    public PieceBoard(int cols, int rows, double width, double height) {
      super(cols, rows, width, height);
    }

    /**
     * Handles the UI rendering of a game piece
     * @param piece game piece
     */
    public void displayPiece(GamePiece piece) {
      for (int x = 0; x < cols; x++) {
        for (int y = 0; y < rows; y++) {
          grid.set(x, y, 0);
          if (piece.getBlocks()[x][y] >= 1) {
            grid.set(x, y, piece.getValue());
          }
        }
      }
    }

    /**
     * Overrides the parent method so clicking on the piece board has no effect
     * @param event mouse event
     * @param block block clicked on
     */
    protected void blockClicked(MouseEvent event, GameBlock block) {}

    /**
     * Handles painting the indicator circle on the middle block
     */
    public void paintIndicator() {
      blocks[1][1].paintIndicator();
    }
}

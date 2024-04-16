package uk.ac.soton.comp1206.component;

import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;
import uk.ac.soton.comp1206.ui.GameWindow;

public class PieceBoard extends GameBoard {
    private static final Logger logger = LogManager.getLogger(GameWindow.class);

    public PieceBoard(Grid grid, double width, double height) {
      super(grid, width, height);
    }

    public PieceBoard(int cols, int rows, double width, double height) {
      super(cols, rows, width, height);
    }

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

    protected void blockClicked(MouseEvent event, GameBlock block) {}

    public void paintIndicator() {
      blocks[1][1].paintIndicator();
    }
}

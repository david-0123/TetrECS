package uk.ac.soton.comp1206.component;

import java.util.HashSet;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.BlockClickedListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Grid;

/**
 * A GameBoard is a visual component to represent the visual GameBoard.
 * It extends a GridPane to hold a grid of GameBlocks.
 *
 * The GameBoard can hold an internal grid of it's own, for example, for displaying an upcoming block. It also be
 * linked to an external grid, for the main game board.
 *
 * The GameBoard is only a visual representation and should not contain game logic or model logic in it, which should
 * take place in the Grid.
 */
public class GameBoard extends GridPane {

    private static final Logger logger = LogManager.getLogger(GameBoard.class);

    /**
     * Number of columns in the board
     */
    protected final int cols;

    /**
     * Number of rows in the board
     */
    protected final int rows;

    /**
     * The visual width of the board - has to be specified due to being a Canvas
     */
    protected final double width;

    /**
     * The visual height of the board - has to be specified due to being a Canvas
     */
    protected final double height;

    /**
     * The grid this GameBoard represents
     */
    final Grid grid;

    /**
     * The blocks inside the grid
     */
    GameBlock[][] blocks;

    /**
     * The listener to call when a specific block is clicked
     */
    protected BlockClickedListener blockClickedListener;

    /**
     * The listener to call when a block is right-clicked
     */
    protected RightClickedListener rightClickedListener;


    /**
     * Create a new GameBoard, based off a given grid, with a visual width and height.
     * @param grid linked grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(Grid grid, double width, double height) {
        this.cols = grid.getCols();
        this.rows = grid.getRows();
        this.width = width;
        this.height = height;
        this.grid = grid;

        //Build the GameBoard
        build();
    }

    /**
     * Create a new GameBoard with its own internal grid, specifying the number of columns and rows, along with the
     * visual width and height.
     *
     * @param cols number of columns for internal grid
     * @param rows number of rows for internal grid
     * @param width the visual width
     * @param height the visual height
     */
    public GameBoard(int cols, int rows, double width, double height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        this.grid = new Grid(cols,rows);

        //Build the GameBoard
        build();
    }

    /**
     * Get a specific block from the GameBoard, specified by its row and column
     * @param x column
     * @param y row
     * @return game block at the given column and row
     */
    public GameBlock getBlock(int x, int y) {
        return blocks[x][y];
    }

    /**
     * Build the GameBoard by creating a block at every x and y column and row
     */
    protected void build() {
        setMaxWidth(width);
        setMaxHeight(height);

        setGridLinesVisible(true);

        blocks = new GameBlock[cols][rows];

        for(var y = 0; y < rows; y++) {
            for (var x = 0; x < cols; x++) {
                createBlock(x,y);
            }
        }
    }

    /**
     * Create a block at the given x and y position in the GameBoard
     *
     * @param x column
     * @param y row
     */
    protected void createBlock(int x, int y) {
        var blockWidth = width / cols;
        var blockHeight = height / rows;

        //Create a new GameBlock UI component
        GameBlock block = new GameBlock(this, x, y, blockWidth, blockHeight);

        //Add to the GridPane
        add(block,x,y);

        //Add to our block directory
        blocks[x][y] = block;

        //Link the GameBlock component to the corresponding value in the Grid
        block.bind(grid.getGridProperty(x,y));

        //Add a mouse click handler to the block to trigger GameBoard blockClicked method
        block.setOnMouseClicked(e -> blockClicked(e, block));

        //Trigger hoverEnter method when mouse hovers a game block on the main game board
        if (!(this instanceof PieceBoard)) {
            block.setOnMouseEntered(e -> hoverEnter(e, block));
            block.setOnMouseExited(e -> hoverLeave(e, block));
        }
    }

    /**
     * Set the listener to handle an event when a block is clicked
     * @param listener listener
     */
    public void setOnBlockClick(BlockClickedListener listener) {
        this.blockClickedListener = listener;
    }

    /**
     * Set the listener to handle an event when a block is right-clicked
     * @param listener listener
     */
    public void setOnRightClicked(RightClickedListener listener) {
        this.rightClickedListener = listener;
    }

    /**
     * Triggered when a block is clicked. Call the attached listener.
     * @param event mouse event
     * @param block block clicked on
     */
    protected void blockClicked(MouseEvent event, GameBlock block) {
        if (blockClickedListener != null) {
            if (event.getButton() == MouseButton.SECONDARY) {
                logger.info("Block right-clicked: {}", block);
                rightClickedListener.blockRightClicked(block);
            } else {
                logger.info("Block clicked: {}", block);
                blockClickedListener.blockClicked(block);
            }
        }
    }

    /**
     * Method called when a block is hovered over
     * @param event mouse hover over the block
     * @param block game block
     */
    private void hoverEnter(MouseEvent event, GameBlock block) {
        block.paintHover();
    }

    /**
     * Method to reset block's painting when mouse exits it
     * @param event mouse leaves the block
     * @param block game block
     */
    private void hoverLeave(MouseEvent event, GameBlock block) {
        block.paint();
    }

    /**
     * Handles the fading out animation of the cleared lines
     * @param coords coordinates of blocks to fade out
     */
    public void fadeOut(HashSet<GameBlockCoordinate> coords) {
        for (GameBlockCoordinate coord : coords) {
            var x = coord.getX();
            var y = coord.getY();

            var block = blocks[x][y];
            block.fadeOut();
        }
    }
}

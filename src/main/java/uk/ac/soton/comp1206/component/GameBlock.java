package uk.ac.soton.comp1206.component;

import javafx.animation.AnimationTimer;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Visual User Interface component representing a single block in the grid.
 *
 * Extends Canvas and is responsible for drawing itself.
 *
 * Displays an empty square (when the value is 0) or a coloured square depending on value.
 *
 * The GameBlock value should be bound to a corresponding block in the Grid model.
 */
public class GameBlock extends Canvas {

    private static final Logger logger = LogManager.getLogger(GameBlock.class);

    /**
     * The set of colours for different pieces
     */
    public static final Color[] COLOURS = {
        //region Colours
        Color.TRANSPARENT,
        Color.DEEPPINK,
        Color.RED,
        Color.ORANGE,
        Color.YELLOW,
        Color.YELLOWGREEN,
        Color.LIME,
        Color.GREEN,
        Color.DARKGREEN,
        Color.DARKTURQUOISE,
        Color.DEEPSKYBLUE,
        Color.AQUA,
        Color.AQUAMARINE,
        Color.BLUE,
        Color.MEDIUMPURPLE,
        Color.PURPLE
        //endregion
    };

    /**
     * The GameBoard the block is a part of
     */
    private final GameBoard gameBoard;

    /**
     * The width of the block
     */
    private final double width;

    /**
     * The height of the block
     */
    private final double height;

    /**
     * The column this block exists as in the grid
     */
    private final int x;

    /**
     * The row this block exists as in the grid
     */
    private final int y;

    /**
     * The value of this block (0 = empty, otherwise specifies the colour to render as)
     */
    private final IntegerProperty value = new SimpleIntegerProperty(0);

    /**
     * Initial opacity used for the fade out animation
     */
    private static double opacity = 1;

    /**
     * Speed of the fade out animation
     */
    private final double fadeSpeed = 0.005;

    /**
     * Create a new single Game Block
     * @param gameBoard the board this block belongs to
     * @param x the column the block exists in
     * @param y the row the block exists in
     * @param width the width of the canvas to render
     * @param height the height of the canvas to render
     */
    public GameBlock(GameBoard gameBoard, int x, int y, double width, double height) {
        this.gameBoard = gameBoard;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;

        //A canvas needs a fixed width and height
        setWidth(width);
        setHeight(height);

        //Do an initial paint
        paint();

        //When the value property is updated, call the internal updateValue method
        value.addListener(this::updateValue);
    }

    /**
     * When the value of this block is updated,
     * @param observable what was updated
     * @param oldValue the old value
     * @param newValue the new value
     */
    private void updateValue(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        paint();
    }

    /**
     * Handle painting of the block canvas
     */
    public void paint() {
        //If the block is empty, paint as empty
        if(value.get() == 0) {
            paintEmpty();
        } else {
            //If the block is not empty, paint with the colour represented by the value
            paintColor(COLOURS[getValue()]);
        }
    }

    /**
     * Paint this canvas empty
     */
    private void paintEmpty() {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Fill slightly transparent
        gc.setGlobalAlpha(0.4);
        gc.setFill(Color.BLACK);
        gc.fillRect(0,0, width, height);
        gc.setGlobalAlpha(1);

        //Border
        gc.setLineWidth(1);
        gc.setStroke(Color.BLACK);
        gc.strokeRect(0,0,width,height);
        gc.setLineWidth(0.5);
        gc.setStroke(Color.WHITE);
        gc.strokeRect(0, 0, width, height);
    }

    /**
     * Paint this canvas with the given colour
     * @param colour the colour to paint
     */
    private void paintColor(Paint colour) {
        var gc = getGraphicsContext2D();

        //Clear
        gc.clearRect(0,0,width,height);

        //Colour fill
        gc.setFill(((Color) colour).saturate());
        gc.fillRect(0,0, width, height);

        //Lighter triangle
        double[] xPoints = {0, 0, width};
        double[] yPoints = {0, height, height};
        gc.setGlobalAlpha(0.15);
        gc.setFill(Color.WHITE);
        gc.fillPolygon(xPoints, yPoints, 3);

        //Border
        gc.setGlobalAlpha(0.4);
        gc.setLineWidth(6);
        gc.setStroke(Color.BLACK);
        gc.strokeLine(0, height, width, height);
        gc.strokeLine(width, 0, width, height);

        gc.setLineWidth(5);
        gc.setStroke(Color.WHITE);
        gc.strokeLine(0, 0, width, 0);
        gc.strokeLine(0, 0, 0, height);

        gc.setGlobalAlpha(1);
    }

    /**
     * Method to paint the small indicator circle on the piece board
     */
    public void paintIndicator() {
        var gc = getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.setGlobalAlpha(0.5);
        gc.fillOval(12,9,20,20);
        gc.setGlobalAlpha(1);
    }

    /**
     * Method to paint a faint white layer over the block
     */
    public void paintHover() {
        paint();

        var gc = getGraphicsContext2D();

        gc.setFill(Color.WHITE);
        gc.setGlobalAlpha(0.3);
        gc.fillRect(0,0,width,height);
        gc.setGlobalAlpha(1);
    }

    /**
     * Get the column of this block
     * @return column number
     */
    public int getX() {
        return x;
    }

    /**
     * Get the row of this block
     * @return row number
     */
    public int getY() {
        return y;
    }

    /**
     * Get the current value held by this block, representing it's colour
     * @return value
     */
    public int getValue() {
        return this.value.get();
    }

    /**
     * Bind the value of this block to another property. Used to link the visual block to a corresponding block in the Grid.
     * @param input property to bind the value to
     */
    public void bind(ObservableValue<? extends Number> input) {
        value.bind(input);
    }

    /**
     * Handles the animation for fading out a block when it's part of a cleared line
     */
    public void fadeOut() {
        opacity = 1;
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long l) {
                opacity -= fadeSpeed;
                paintEmpty();
                if (opacity <= 0) {
                    stopFade(this);
                } else {
                    fadePaint();
                }
            }
        };

        timer.start();
    }

    /**
     * Handles painting the block progressively more transparent
     */
    private void fadePaint() {
        var gc = getGraphicsContext2D();

        gc.setFill(Color.GREEN.deriveColor(0,1,1,opacity).saturate().brighter());
        gc.fillRect(0,0,width,height);
    }

    /**
     * Stops the animation
     * @param timer animation timer
     */
    private void stopFade(AnimationTimer timer) {
        timer.stop();
        gameBoard.grid.set(getX(), getY(), 0);
    }
}
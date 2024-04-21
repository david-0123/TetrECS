package uk.ac.soton.comp1206.event;


/**
 * Used to handle the game loop timer
 */
public interface GameLoopListener {

    /**
     * Register the start of a new game timer
     */
    public void setOnGameLoop();
}

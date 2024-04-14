package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.component.GameBlock;

/**
 * Used to handle the event when the user right-clicks on the GameBoard or left-clicks on the current piece PieceBoard
 */
public interface RightClickedListener {

    /**
     * Handle a right click event
     * @param block block that was clicked
     */
      public void blockRightClicked(GameBlock block);
}

package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Used to handle when a new piece is provided by the game so the corresponding UI element can be updated
 */
public interface NextPieceListener {

  /**
   * Handle generating the next game piece
   * @param piece the next game piece
   */
  public void nextPiece(GamePiece piece);
}

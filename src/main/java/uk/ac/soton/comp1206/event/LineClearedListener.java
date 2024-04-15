package uk.ac.soton.comp1206.event;

import java.util.HashSet;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * Used to handle the event when a line is cleared
 */
public interface LineClearedListener {

  /**
   * Handle adding the cleared coordinates to the HashSet
   * @param clearedCoords set of block coordinates that were cleared
   */
  public void lineCleared(HashSet<GameBlockCoordinate> clearedCoords);
}

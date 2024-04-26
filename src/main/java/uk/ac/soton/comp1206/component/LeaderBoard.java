package uk.ac.soton.comp1206.component;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom UI component that holds the leaderboard in a multiplayer challenge
 */
public class LeaderBoard extends ScoresList {

    private static final Logger logger = LogManager.getLogger(LeaderBoard.class);

    /**
     * Holds the list of scores of all the players in the game
     */
    private ListProperty<Pair<Pair<String, String>, String>> leaderBoardScores;

    public LeaderBoard() {
        setAlignment(Pos.CENTER);
        setSpacing(5);

        leaderBoardScores = new SimpleListProperty<>();
        leaderBoardScores.addListener((ListChangeListener.Change<? extends Pair<Pair<String, String>, String>> change) -> updateUI());
    }

    /**
     * Update the UI rendering of the leaderboard
     */
    private void updateUI() {
        logger.info("Updating UI leaderboard");
        Platform.runLater(() -> {
            getChildren().clear();

            //Pair <Pair<String, String>, String>
            //Name (Score:Lives)
            
            for (Pair<Pair<String, String>, String> player : leaderBoardScores) {
                var name = player.getKey().getKey();
                var score = player.getKey().getValue();
                var lives = player.getValue();

                var text = new Text(name + " (" + score  + ":" + lives + ")");
                text.getStyleClass().add("leaderboard");

                if (lives.equalsIgnoreCase("DEAD")) {
                    text.getStyleClass().add("deadscore");
                }

                if ((leaderBoardScores.indexOf(player) + 1) % 15 == 0) {
                    text.setFill(GameBlock.COLOURS[(leaderBoardScores.indexOf(player) + 2) % 15]);
                } else {
                    text.setFill(GameBlock.COLOURS[(leaderBoardScores.indexOf(player) + 1) % 15]);
                }

                getChildren().add(text);
            }
        });
    }

    public ListProperty<Pair<Pair<String, String>, String>> getLeaderBoardScores() {
        return leaderBoardScores;
    }
}

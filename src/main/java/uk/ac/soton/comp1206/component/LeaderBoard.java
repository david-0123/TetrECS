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
import uk.ac.soton.comp1206.scene.MenuScene;

/**
 * Custom UI component that holds the leaderboard in a multiplayer challenge
 */
public class LeaderBoard extends ScoresList {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    private ListProperty<Pair<Pair<String, String>, String>> scores;

    public LeaderBoard() {
        setAlignment(Pos.CENTER);
        setSpacing(5);

        scores = new SimpleListProperty<>();
        scores.addListener((ListChangeListener.Change<? extends Pair<Pair<String, String>, String>> change) -> updateUI());
    }

    /**
     * Update the UI rendering of the leaderboard
     */
    private void updateUI() {
        logger.info("Updating UI leaderboard");
        Platform.runLater(() -> {
            getChildren().clear();

            //Pair< Pair<String, String>, String >
            //Name (Score:Lives)
            
            for (Pair<Pair<String, String>, String> player : scores) {
                var name = player.getKey().getKey();
                var score = player.getKey().getValue();
                var lives = player.getValue();

                var text = new Text(name + " (" + score  + ":" + lives + ")");
                text.getStyleClass().add("leaderboard");

                if (lives.equalsIgnoreCase("DEAD")) {
                    text.getStyleClass().add("deadscore");
                }

                if ((scores.indexOf(player) + 1) % 15 == 0) {
                    text.setFill(GameBlock.COLOURS[(scores.indexOf(player) + 2) % 15]);
                } else {
                    text.setFill(GameBlock.COLOURS[(scores.indexOf(player) + 1) % 15]);
                }

                getChildren().add(text);
            }
        });
    }

    public ListProperty<Pair<Pair<String, String>, String>> getScores() {
        return scores;
    }
}

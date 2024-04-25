package uk.ac.soton.comp1206.component;

import javafx.beans.property.ListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

/**
 * Custom UI component that holds the leaderboard in a multiplayer challenge
 */
public class LeaderBoard extends ScoresList {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    protected ListProperty<Pair<Pair<String, String>, String>> scores;

    public LeaderBoard(ListProperty<Pair<Pair<String, String>, String>> sceneScores) {
        setAlignment(Pos.CENTER);
        setSpacing(5);

        if (!sceneScores.isEmpty()) {
            scores = sceneScores;
            maxItems = scores.size();

            scores.addListener((ListChangeListener.Change<? extends Pair<Pair<String, String>, String>> change) -> {
                updateUI();
            });

            updateUI();
        }
    }

    public void reveal() {}
    
    private void updateUI() {
        logger.info("Updating UI leaderboard");
        getChildren().clear();

        //Pair< Pair<String, String>, String>
        //Name (Score:Lives)
        for (int i = 0; i < maxItems; i++) {
            var label = new Label(scores.get(i).getKey().getKey() + " (" + scores.get(i).getKey().getValue() + ":" + scores.get(i).getValue() + ")");
            label.getStyleClass().add("leaderboard");
            if ((i + 1) % 15 == 0) label.setTextFill(GameBlock.COLOURS[(i + 2) % 15]);
            else label.setTextFill(GameBlock.COLOURS[(i + 1) % 15]);

            if (scores.get(i).getKey().getKey().equalsIgnoreCase("David")) {
                label.getStyleClass().add("myscore");
            }

            getChildren().add(label);
        }
    }
}

package uk.ac.soton.comp1206.component;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
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

    protected ListProperty<Pair<Pair<String, String>, String>> scores;

    public LeaderBoard(ListProperty<Pair<Pair<String, String>, String>> sceneScores) {
        setAlignment(Pos.CENTER);
        setSpacing(5);

        if (!sceneScores.isEmpty()) {
            scores = sceneScores;
            maxItems = scores.size();

            scores.addListener((ListChangeListener.Change<? extends Pair<Pair<String, String>, String>> change) -> updateUI());

            updateUI();
        }
    }
    
    private void updateUI() {
        logger.info("Updating UI leaderboard");
        Platform.runLater(() -> {
            getChildren().clear();

            //Pair< Pair<String, String>, String>
            //Name (Score:Lives)
            for (int i = 0; i < maxItems; i++) {
                var text = new Text(scores.get(i).getKey().getKey() + " (" + scores.get(i).getKey().getValue() + ":" + scores.get(i).getValue() + ")");
                text.getStyleClass().add("leaderboard");
                if (scores.get(i).getValue().equals("DEAD") || scores.get(i).getValue().equals("DEAD")) text.getStyleClass().add("deadscore");
                if ((i + 1) % 15 == 0) text.setFill(GameBlock.COLOURS[(i + 2) % 15]);
                else text.setFill(GameBlock.COLOURS[(i + 1) % 15]);

                if (scores.get(i).getKey().getKey().equalsIgnoreCase("David")) {
                    text.getStyleClass().add("myscore");
                }

                getChildren().add(text);
            }
        });
    }
}

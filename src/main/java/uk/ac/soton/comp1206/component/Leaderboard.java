package uk.ac.soton.comp1206.component;

import javafx.beans.property.ListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

/**
 * Custom UI component that holds the leaderboard in a multiplayer challenge
 */
public class Leaderboard extends ScoresList {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    public Leaderboard(ListProperty<Pair<String, Integer>> sceneScores) {
        super(sceneScores);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(0));
        setSpacing(5);
    }

    public void reveal() {
        for (int i = 0; i < getChildren().size(); i++) {
            var label = (Label) getChildren().get(i);
            label.getStyleClass().clear();
            label.getStyleClass().add("leaderboard");
            label.setOpacity(1);
        }
    }
}

package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.beans.property.ListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.scene.MenuScene;

public class ScoresList extends VBox {
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Holds the scores to display on the UI
     */
    private ListProperty<Pair<String, Integer>> scores;

    public ScoresList(ListProperty<Pair<String, Integer>> sceneScores) {
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);
        this.setPadding(new Insets(50,0,0,0));

        if (!sceneScores.isEmpty()) {
            scores = sceneScores;

            scores.addListener((ListChangeListener.Change<? extends Pair<String, Integer>> change) -> {
                updateUI();
            });

            updateUI();
        }

    }

    /**
     * Updates the UI component in the event that the list of scores changes
     */
    private void updateUI() {
        logger.info("Updating UI ScoresList");
        getChildren().clear();

        for (Pair<String, Integer> score : scores) {
            var label = new Label(score.getKey() + ": " + score.getValue());
            label.setOpacity(0);
            label.getStyleClass().add("scorelist");
            getChildren().add(label);
        }
    }

    /**
     * Animates the scores display
     */
    public void reveal() {
        logger.info("Revealing scores");
        // Iterate through each label in the VBox and apply a fade-in animation
        for (int i = 0; i < getChildren().size(); i++) {
            var label = (Label) getChildren().get(i);

            // Create a fade transition to gradually reveal the label
            FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), label);
            fadeTransition.setToValue(1); // Set final opacity to 1
            fadeTransition.setDelay(Duration.seconds(i * 0.1)); // Delay each label's animation
            fadeTransition.play(); // Start the animation
        }
    }
}

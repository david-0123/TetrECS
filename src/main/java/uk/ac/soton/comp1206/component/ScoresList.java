package uk.ac.soton.comp1206.component;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Custom UI component that holds the current high scores and displays them to the UI
 */
public class ScoresList extends VBox {
    private static final Logger logger = LogManager.getLogger(ScoresList.class);

    /**
     * Holds the scores to display on the UI
     */
    protected ListProperty<Pair<String, Integer>> scores;

    public ScoresList() {
        this.setAlignment(Pos.TOP_CENTER);
        this.setSpacing(10);
        this.setPadding(new Insets(20,0,0,0));

        scores = new SimpleListProperty<>();
        scores.addListener((ListChangeListener.Change<? extends Pair<String, Integer>> change) -> updateUI());
    }

    /**
     * Updates the UI component with the correct scores when the list of scores changes
     */
    private void updateUI() {
        logger.info("Updating UI ScoresList");
        Platform.runLater(() -> {
            getChildren().clear();

            for (int i = 0; i < 10; i++) {
                var label = new Text(scores.get(i).getKey() + ": " + scores.get(i).getValue());
                label.setOpacity(0);
                label.getStyleClass().add("scorelist");
                label.setFill(GameBlock.COLOURS[i+1]);
                if (scores.get(i).getKey().equalsIgnoreCase("David")) {
                    label.getStyleClass().add("myscore");
                }
                getChildren().add(label);
            }
        });
        reveal();
    }

    /**
     * Animates the scores display
     */
    public void reveal() {
        logger.info("Revealing scores");
        Platform.runLater(() -> {
            // Iterate through each label in the VBox and apply a fade-in animation
            for (int i = 0; i < getChildren().size(); i++) {
                var label = getChildren().get(i);

                // Create a fade transition to gradually reveal the label
                FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), label);
                fadeTransition.setToValue(1); // Set final opacity to 1
                fadeTransition.setDelay(Duration.seconds(i * 0.1)); // Delay each label's animation
                fadeTransition.play();
                label.setOpacity(1);
            }
        });
    }

    public ListProperty<Pair<String, Integer>> scoreListProperty() {
        return scores;
    }
}

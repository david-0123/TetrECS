package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The scores screen shown when the game ends
 */
public class ScoresScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Holds the state of the game when it ends
     */
    private Game game;

    /**
     * Holds the scores
     */
    private ListProperty<Pair<String, Integer>> localScores;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public ScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow);
        this.game = game;
        setSceneName("Scores");
        logger.info("Creating Scores scene");

        var pairList = new ArrayList<Pair<String, Integer>>();
        localScores = new SimpleListProperty<>(FXCollections.observableArrayList(pairList));
    }

    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("menu-background");
        root.getChildren().add(scoresPane);

        var mainPane = new BorderPane(new Label("Yo slime"));
        scoresPane.getChildren().add(mainPane);
    }

    public ListProperty<Pair<String, Integer>> localScoresProperty() {
        return localScores;
    }
}

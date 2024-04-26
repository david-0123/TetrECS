package uk.ac.soton.comp1206.scene;

import static uk.ac.soton.comp1206.game.Multimedia.rotateLogo;

import javafx.beans.property.ListProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.LeaderBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.MultiplayerGame;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The scores screen shown when the multiplayer game ends
 */
public class MultiScoresScene extends ScoresScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     * @param game game
     */
    public MultiScoresScene(GameWindow gameWindow, Game game) {
        super(gameWindow, game);
    }

    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var scoresPane = new StackPane();
        scoresPane.setMaxWidth(gameWindow.getWidth());
        scoresPane.setMaxHeight(gameWindow.getHeight());
        scoresPane.getStyleClass().add("menu-background");
        root.getChildren().add(scoresPane);

        var mainPane = new BorderPane();
        scoresPane.getChildren().add(mainPane);

        var tetrecs = new ImageView(new Image(this.getClass().getResource("/images/TetrECS.png").toExternalForm()));
        tetrecs.setPreserveRatio(true);
        tetrecs.setFitWidth(600);
        tetrecs.setRotate(-2.5);

        rotateLogo(tetrecs, 5);

        var gameOver = new Text("Game Over");
        gameOver.getStyleClass().add("bigtitle");

        var titleBox = new VBox(tetrecs, gameOver);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(10, 0, 0, 0));

        var leaderboard = new LeaderBoard(
            (ListProperty<Pair<Pair<String, String>, String>>) ((MultiplayerGame) game).getPlayerScores()
        );
        leaderboard.getStyleClass().add("finalLeaderboard");
        leaderboard.setAlignment(Pos.TOP_CENTER);

        mainPane.setTop(titleBox);
        mainPane.setCenter(leaderboard);
    }
}

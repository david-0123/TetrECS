package uk.ac.soton.comp1206.scene;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The "How-To" instructions scene. Holds the image of the game instructions and all available
 * game pieces
 */
public class InstructionsScene extends BaseScene {
    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     *
     * @param gameWindow the game window
     */
    public InstructionsScene(GameWindow gameWindow) {
      super(gameWindow);
      setSceneName("Instructions");
      logger.info("Creating Instructions Scene");
    }

    /**
     * Builds the instructions scene
     */
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var instructPane = new StackPane();
        instructPane.setMaxWidth(gameWindow.getWidth());
        instructPane.setMaxHeight(gameWindow.getHeight());
        instructPane.getStyleClass().add("menu-background");
        root.getChildren().add(instructPane);

        var mainPane = new VBox();
        mainPane.setAlignment(Pos.CENTER);
        instructPane.getChildren().add(mainPane);

        var title = new Text("Instructions");
        title.getStyleClass().add("heading");
        title.setTextAlignment(TextAlignment.CENTER);

        var instructImage = new ImageView(new Image(this.getClass().getResource("/images/Instructions.png").toExternalForm()));
        instructImage.setPreserveRatio(true);
        instructImage.setFitWidth(600);

        var piecesTitle = new Text("Game Pieces");
        piecesTitle.getStyleClass().add("heading");
        piecesTitle.setTextAlignment(TextAlignment.CENTER);

        var gamePieces = new GridPane();
        gamePieces.setHgap(10);
        gamePieces.setVgap(10);
        gamePieces.setAlignment(Pos.CENTER);
        int count = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                var piece = new PieceBoard(3,3,gameWindow.getWidth()/15,gameWindow.getHeight()/14);
                piece.displayPiece(GamePiece.createPiece(count));
                gamePieces.add(piece,i,j);
                piece.setOpacity(0);

                //Animate the reveal of the pieces
                var ft = new FadeTransition(Duration.millis(500), piece);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.setDelay(Duration.millis(count*100));
                ft.play();

                count++;
            }
        }


        mainPane.getChildren().addAll(title,instructImage,piecesTitle,gamePieces);
    }
}

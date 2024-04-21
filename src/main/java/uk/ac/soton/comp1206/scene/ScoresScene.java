package uk.ac.soton.comp1206.scene;

import static java.lang.Integer.compare;
import static java.lang.Integer.parseInt;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.ScoresList;
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
     * Holds the UI component displaying the scores
     */
    private ScoresList scoresList;

    /**
     * Holds the file object that contains the list of scores
     */
    private File scoresFile;

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

        localScores = new SimpleListProperty<>(FXCollections.observableArrayList());

        loadScores("scores.txt");
        scoresList = new ScoresList(localScores);

        checkForHiScore();
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

        MenuScene.rotateLogo(tetrecs, 5);

        var gameOver = new Text("Game Over");
        gameOver.getStyleClass().add("bigtitle");

        var titleBox = new VBox(tetrecs, gameOver);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setSpacing(10);
        titleBox.setPadding(new Insets(10, 0, 0, 0));

        mainPane.setTop(titleBox);
        mainPane.setCenter(scoresList);
    }

    /**
     * Loads a set of high scores from a config file to display on the screen
     * @param filePath file path
     */
    public void loadScores(String filePath) {
        List<Pair<String, Integer>> loadedScores = new ArrayList<>();

        try {
            scoresFile = new File(filePath);

            //If new file was just created or existing file is empty
            if (scoresFile.createNewFile() || scoresFile.length() <= 0) {
                writeDefaultScores();
            }

            var reader = new BufferedReader(new FileReader(scoresFile));
            var it = reader.lines().iterator();

            while (it.hasNext()) {
                var line = it.next().trim();
                if (!line.isEmpty()) {
                    var parts = line.split(":");
                    if (parts.length == 2) {
                        var name = parts[0].trim();
                        var score = parseInt(parts[1].trim());

                        loadedScores.add(new Pair<>(name,score));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("The file with the specified path couldn't be found");
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong - scores were not loaded");
        }

        loadedScores.sort((o1, o2) -> compare(o2.getValue(), o1.getValue()));

        localScores.set(FXCollections.observableArrayList(loadedScores));
    }

    /**
     * Writes the local scores onto a config file
     * @param filePath file path
     */
    public void writeScores(String filePath) {
        try {
            scoresFile = new File(filePath);

            if (scoresFile.createNewFile() || scoresFile.length() <= 0) {
                writeDefaultScores();
            }

            var writer = new BufferedWriter(new FileWriter(scoresFile));

            for (Pair<String, Integer> score : localScores) {
                writer.write(score.getKey() + ":" + score.getValue());
                writer.newLine();
            }

            writer.close();

        } catch (IOException e) {
            throw new RuntimeException("Something went wrong - scores were not written");
        }
    }

    /**
     * Writes a set of default scores to the file if it doesn't exist already, or it exists and is empty
     * @throws IOException if something goes wrong when opening the file
     */
    private void writeDefaultScores() throws IOException {
        logger.info("Writing default scores");
        var writer = new BufferedWriter(new FileWriter(scoresFile));

        String[] defaultScores = {
            "Player1:100",
            "Player2:90",
            "Player3:80",
            "Player4:70",
            "Player5:60",
            "Player6:50",
            "Player7:40",
            "Player8:30",
            "Player9:20",
            "Player10:10",
        };

        for (String score : defaultScores) {
            writer.write(score);
            writer.newLine();
        }

        writer.close();
    }

    /**
     * Checks if user's score beat any of the previous high scores and updates the scores list if so
     */
    private void checkForHiScore() {
        logger.info("Checking for high score");
        var newHiScore = false;

        for (Pair<String, Integer> score : localScores) {
            if (game.getScore() > score.getValue()) {
                newHiScore = true;
                break;
            }
        }

        if (newHiScore) {
            // Runs the dialog box on the JavaFX Application Thread when possible
            Platform.runLater(() -> {
                var dialog = new TextInputDialog();
                dialog.setTitle("New High Score!");
                dialog.setHeaderText("Congratulations! You achieved a new high score.");
                dialog.setContentText("Please enter your name:");
                dialog.setResizable(false);
                var image = new ImageView(new Image(this.getClass().getResource("/images/HighScore.png").toExternalForm()));
                image.setPreserveRatio(true);
                image.setFitWidth(200);
                dialog.setGraphic(image);

                // Prevents the user from closing the dialog until they've entered text
                dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(true);
                dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(true);

                // Re-enables the buttons when there's text in the text field
                var textField = dialog.getEditor();
                textField.textProperty().addListener((observable, oldValue, newValue) -> {
                      var isEmpty = newValue.trim().isEmpty();
                      dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setDisable(isEmpty);
                      dialog.getDialogPane().lookupButton(ButtonType.OK).setDisable(isEmpty);
                });

                var result = dialog.showAndWait();
                result.ifPresent(
                    name -> {
                    localScores.add(new Pair<>(name, game.getScore()));
                    localScores.sort((o1, o2) -> compare(o2.getValue(), o1.getValue()));

                    writeScores("scores.txt");
                    scoresList.reveal();
                });
            });
        } else scoresList.reveal();
    }

    public ListProperty<Pair<String, Integer>> localScoresProperty() {
        return localScores;
    }
}

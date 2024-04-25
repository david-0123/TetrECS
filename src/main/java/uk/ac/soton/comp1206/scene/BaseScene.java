package uk.ac.soton.comp1206.scene;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * A Base Scene used in the game. Handles common functionality between all scenes.
 */
public abstract class BaseScene {
    private static final Logger logger = LogManager.getLogger(GameWindow.class);

    /**
     * GameWindow linked to the scene
     */
    protected final GameWindow gameWindow;

    /**
     * Root element of every scene
     */
    protected GamePane root;

    protected Scene scene;

    /**
     * Holds the scene's designated name
     */
    protected String sceneName;

    /**
     * Create a new scene, passing in the GameWindow the scene will be displayed in
     * @param gameWindow the game window
     */
    public BaseScene(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    /**
     * Initialise this scene. Called after creation
     */
    public void initialise() {
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                logger.info("Escape key pressed");
                if (getSceneName().equalsIgnoreCase("Menu")) {
                    logger.info("Shutting down");
                    gameWindow.getCommunicator().send("QUIT");
                    gameWindow.exit();
                }
                logger.info("Going back to the menu");
                gameWindow.startMenu();
            }
        });
    }

    /**
     * Build the layout of the scene
     */
    public abstract void build();

    /**
     * Create a new JavaFX scene using the root contained within this scene
     * @return JavaFX scene
     */
    public Scene setScene() {
        var previous = gameWindow.getScene();
        Scene scene = new Scene(root, previous.getWidth(), previous.getHeight(), Color.BLACK);
        scene.getStylesheets().add(getClass().getResource("/style/game.css").toExternalForm());
        this.scene = scene;
        return scene;
    }

    /**
     * Get the JavaFX scene contained inside
     * @return JavaFX scene
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Set the name of the current scene
     * @param name scene name
     */
    public void setSceneName(String name) {
        sceneName = name;
    }

    /**
     * Get the name of the current scene
     * @return scene name
     */
    public String getSceneName() {
        return sceneName;
    }
}

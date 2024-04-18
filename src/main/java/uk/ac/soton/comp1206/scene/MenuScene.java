package uk.ac.soton.comp1206.scene;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.game.Multimedia;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        setSceneName("Menu");
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        var menuItems = new VBox();
        menuItems.setAlignment(Pos.BOTTOM_CENTER);
        menuItems.getStyleClass().add("menu");
        
        var tetrecs = new ImageView(new Image(this.getClass().getResource("/images/TetrECS.png").toExternalForm()));
        tetrecs.setPreserveRatio(true);
        tetrecs.setFitWidth(600);
        tetrecs.setRotate(-10);

        //Small gap between the title and menu buttons
        var spacer = new Region();
        spacer.setPrefHeight(150);

        var single = new Text("Single Player");
        var multi = new Text("Multi Player");
        var instruct = new Text("How to Play");
        var exit = new Text("Exit");
        single.getStyleClass().add("menuItem");
        multi.getStyleClass().add("menuItem");
        instruct.getStyleClass().add("menuItem");
        exit.getStyleClass().add("menuItem");

        menuItems.getChildren().addAll(tetrecs,spacer,single,multi,instruct,exit);

        mainPane.setCenter(menuItems);
        
        rotateLogo(tetrecs);

        single.setOnMouseClicked(this::startGame);
        instruct.setOnMouseClicked(this::showInstructions);
        exit.setOnMouseClicked(this::exitGame);

        if (!Multimedia.isMusicPlaying()) Multimedia.playMusic("menu.mp3", true);
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(MouseEvent event) {
        gameWindow.startChallenge();
    }

    /**
     * Handle when the Exit button is pressed
     * @param event event
     */
    private void exitGame(MouseEvent event) {
        gameWindow.exit();
    }

    /**
     * Handles when the How to Play button is pressed
     * @param event event
     */
    private void showInstructions(MouseEvent event) {
        Multimedia.playAudio("transition.wav");
        gameWindow.startInstructions();
    }

    /**
     * Plays an animation that continuously rotates the given node
     * @param node node to be rotated
     */
    public void rotateLogo(Node node) {
        RotateTransition rt = new RotateTransition(Duration.seconds(2), node);
        rt.setByAngle(20);
        rt.setCycleCount(Animation.INDEFINITE);
        rt.setAutoReverse(true);
        rt.play();
    }
}

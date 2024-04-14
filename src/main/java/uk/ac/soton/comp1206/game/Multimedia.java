package uk.ac.soton.comp1206.game;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Multimedia class handles the audio for the game
 */
public class Multimedia {
    private static final Logger logger = LogManager.getLogger(Game.class);
    /**
     * Stores the audio to be played
     */
    private static MediaPlayer audioPlayer;
    /**
     * Stores the background music
     */
    private static MediaPlayer musicPlayer;
    /**
     * Flag that determines whether audio can be played
     */
    private static BooleanProperty audioEnabled = new SimpleBooleanProperty(true);

    /**
     * Handles and plays the given audio file
     * @param file the audio file
     */
    public static void playAudio(String file) {
        if (!audioEnabled.get()) return;

        String audio = Multimedia.class.getResource("/sounds/" + file).toExternalForm();
        logger.info("Playing audio file: {}", file);

        try {
            Media play = new Media(audio);
            audioPlayer = new MediaPlayer(play);
            audioPlayer.play();
        } catch (Exception e) {
            audioEnabled.set(false);
            logger.error("Something went wrong :( - audio can't be played");
            logger.error("Disabling audio...");
        }
    }

    /**
     * Continuously plays the given audio file
     * @param file audio file being looped
     * @param loop determines whether the audio file will loop
     */
    public static void playMusic(String file, boolean loop) {
        if (!audioEnabled.get()) return;

        String audio = Multimedia.class.getResource("/music/" + file).toExternalForm();
        logger.info("Playing background music: {}", file);

        try {
            Media play = new Media(audio);
            musicPlayer = new MediaPlayer(play);
            if (loop) musicPlayer.setCycleCount(MediaPlayer.INDEFINITE); //Sets music to infinitely loop
            musicPlayer.play();
        } catch (Exception e) {
            audioEnabled.set(false);
            logger.error("Something went wrong :( - music can't be played");
            logger.error("Disabling background music...");
        }
    }

    /**
     * Overloaded playMusic method that plays 2 audio files sequentially
     * @param file1 audio file played first
     * @param file2 audio file played second
     * @param loop determines whether the 2nd audio file will loop
     */
    public static void playMusic(String file1, String file2, boolean loop) {
        if (!audioEnabled.get()) return;

        String audio = Multimedia.class.getResource("/music/" + file1).toExternalForm();
        logger.info("Playing background music: {}", file1);

        try {
            Media play = new Media(audio);
            musicPlayer = new MediaPlayer(play);
            musicPlayer.setOnEndOfMedia(() -> {
                musicPlayer.stop();
                playMusic(file2, loop);
            });
            musicPlayer.play();
        } catch (Exception e) {
            audioEnabled.set(false);
            logger.error("Something went wrong :( - music can't be played");
            logger.error("Disabling background music...");
        }
    }

    /**
     * Method that stops the current background music
     */
    public static void stopMusic() {
        logger.info("Stopping background music");
        if (musicPlayer != null) musicPlayer.stop();
    }

    /**
     * Checks if background music is currently playing
     * @return true if background is playing
     */
    public static boolean isMusicPlaying() {
        logger.info("Checking if music playing");
        if (musicPlayer != null) {
            return !musicPlayer.isMute();
        }
        return false;
    }
}

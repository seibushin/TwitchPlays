/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.soundBot;

import de.seibushin.interactiveBot.helper.GuiHelper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SoundBot {
    private final static String FXML_PATH = "/fxml/soundBot.fxml";

    private static SoundBot instance;

    private final HashMap<String, String> sounds;

    private Stage stage = new Stage();

    private AtomicBoolean playing = new AtomicBoolean(false);

    private AtomicInteger count = new AtomicInteger(-1);

    private final List<String> playList = new ArrayList<>();

    @FXML
    private Label text;
    @FXML
    private StackPane bubble;


    private SoundBot() {
        stage.setTitle("InteractiveBot - SoundBot");
        stage.setResizable(false);
        //stage.initStyle(StageStyle.UNDECORATED);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
            // use this class as controller
            fxmlLoader.setController(this);
            Scene scene = new Scene(fxmlLoader.load());

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        sounds = Sounds.getAll();
        sounds.forEach((s, s2) -> {
            System.out.println(s + " --- " + s2);
        });

        // start scheduler
        scheduler();
    }

    public synchronized static SoundBot getInstance() {
        if (instance == null) {
            instance = new SoundBot();
        }
        return instance;
    }

    public synchronized void addToPlayList(String soundName) {
        System.out.println("soundName " + soundName);
        if (sounds.containsKey(soundName)) {
            System.out.println("cointains " + soundName);
            playList.add(soundName);
            int c = count.get();
            count.compareAndSet(c, c + 1);
        }
    }

    public synchronized void playNextSound() {

    }

    private void scheduler() {
        Thread t = new Thread(() -> {
            while (true) {
                if (count.get() >= 0 && playing.compareAndSet(false, true)) {
                    System.out.println("Sound redy");
                    String soundName = playList.get(count.get());
                    int c = count.get();

                    Platform.runLater(() -> {
                        bubble.setVisible(true);
                        text.setText(soundName + "!");
                    });

                    System.out.println("Sound redy2");

                    // play sound
                    Media sound = new Media(new File(sounds.get(soundName)).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(sound);
                    mediaPlayer.setOnEndOfMedia(() -> {
                        playing.compareAndSet(true, false);
                        count.compareAndSet(c, c - 1);
                    });

                    System.out.println("Sound redy3");

                    mediaPlayer.play();
                    while (playing.get()) {
                        // wait
                    }

                    Platform.runLater(() -> {
                        bubble.setVisible(false);
                    });
                    System.out.println("end of file");
                }
            }
        });

        t.start();
        System.out.println("started scheduler");
    }

    public void start() {
        stage.show();
    }

    public synchronized void playSound(String soundName) {
        if (sounds.containsKey(soundName)) {
            text.setText(soundName);
            stage.show();
            try {

                Media sound = new Media(new File(sounds.get(soundName)).toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.play();

                mediaPlayer.setOnEndOfMedia(() -> {
                    System.out.println("end of media");
                    stage.hide();
                    // reset
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public void speak(String msg) {

    }
}

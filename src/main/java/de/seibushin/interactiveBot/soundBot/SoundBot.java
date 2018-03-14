/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.soundBot;

import de.seibushin.interactiveBot.helper.GuiHelper;
import de.seibushin.interactiveBot.pointBot.PointBot;
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
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.util.data.audio.AudioPlayer;

import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("Duplicates")
public class SoundBot {
    private final static String FXML_PATH = "/fxml/soundBot.fxml";
    private static final String SOUND_BASE = "res/sounds/";

    private static SoundBot instance;

    private MaryInterface mary;

    private  HashMap<String, Integer> sounds;

    private Stage stage;

    private AtomicBoolean playing = new AtomicBoolean(false);
    private AtomicBoolean running = new AtomicBoolean(false);

    private final List<String> playList = new ArrayList<>();

    private Thread scheduler;

    @FXML
    private Label text;
    @FXML
    private StackPane bubble;

    private SoundBot() {

    }

    private void init() {
        stage = new Stage();

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

        stage.setOnCloseRequest(event -> close());
    }

    public synchronized static SoundBot getInstance() {
        if (instance == null) {
            instance = new SoundBot();
        }
        return instance;
    }

    public boolean isRunning() {
        return running.get();
    }

    public synchronized void addToPlayList(String soundName, String user) {
        int cost = sounds.getOrDefault(soundName, -1);

        if (cost > -1) {
            int points = PointBot.getInstance().getPointsForViewer(user);

            if (points >= cost) {
                PointBot.getInstance().addPointToViewer(user, -cost);
                System.out.println("add to playlist " + soundName);
                playList.add(soundName);
            }
        }
    }

    private void setBubbleText(String msg) {
        Platform.runLater(() -> {
            bubble.setVisible(true);
            text.setText(msg + "!");
        });
    }

    private void hideBubble() {
        Platform.runLater(() -> {
            bubble.setVisible(false);
            playing.compareAndSet(true, false);
        });
    }

    public synchronized void playNextSound() {
        if (playList.size() > 0 && playing.compareAndSet(false, true)) {
            System.out.println("Sound ready");
            String soundName = playList.remove(0);

            setBubbleText(soundName);

            // play sound
            Media sound = new Media(new File(SOUND_BASE + soundName + ".mp3").toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.setOnEndOfMedia(() -> {
                hideBubble();
                    });

            mediaPlayer.play();
        }
    }

    private void scheduler() {
        scheduler = new Thread(() -> {
            try {
                mary = new LocalMaryInterface();
            } catch (MaryConfigurationException ex) {
                ex.printStackTrace();
            }

            sounds = Sounds.getAll();
            sounds.forEach((s, s2) -> {
                System.out.println(s + " --- " + s2);
            });
            
            playList.add("ready");

            while (running.get()) {
                while (playing.get()) {
                    // wait
                }
                playNextSound();
            }
        });

        scheduler.start();
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            init();

            stage.show();

            // start scheduler
            scheduler();
        }
    }

    public void speak(String msg, String user) {
        int cost = 5;
        int points = PointBot.getInstance().getPointsForViewer(user);

        if (points >= cost) {
            PointBot.getInstance().addPointToViewer(user, -cost);

            setBubbleText(msg);
            say(msg, "de");
            //say(msg, "de");
        }
    }

    private void say(String input, String voice) {
        try {
            while (!playing.compareAndSet(false,true)) {
                // wait
            }
            AudioPlayer ap = new AudioPlayer();

            try {
                if ("de".equals(voice)) {
                    System.out.println("set voice german");
                    mary.setLocale(Locale.GERMAN);
                    mary.setVoice("bits1-hsmm");
                } else {
                    System.out.println("set voice english");
                    mary.setLocale(Locale.US);
                    mary.setVoice("cmu-slt-hsmm");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*
            System.out.println(mary.getAudioEffects());
            System.out.println(mary.getVoice());
            System.out.println(mary.getAvailableVoices());
            System.out.println(mary.getAvailableLocales());
            for (AudioEffect e : AudioEffects.getEffects()) {
                System.out.println(e.getName());
                System.out.println(e.getHelpText());
                System.out.println();
            }*/

            // Volumne [0.0, 10.0]
            mary.setAudioEffects("Volume(amount:0.20)");

            //TractScaler [0.25, 4.0]
            //Creates a shortened or lengthened vocal tract effect by shifting the formants.
            //mary.setAudioEffects("TractScaler(amount:0.25)");

            //F0Scale [0.0, 3.0]
            //mary.setAudioEffects("F0Scale(f0Scale:2.0)");

            //F0Add [-300.0, 300.0]
            //mary.setAudioEffects("F0Add(f0Add:2.0)");

            //Rate [0.1, 3.0]
            //mary.setAudioEffects("Rate(durScale:2.0)");

            //Robot [0.0, 100.0]
            //mary.setAudioEffects("Robot(amount:100.0)");

            //Whisper [0.0, 100.0]
            //mary.setAudioEffects("Whisper(amount:100.0)");

            //Stadium [0.0, 200.0]
            //mary.setAudioEffects("Stadium(amount:100.0)");

            //Chorus
            //mary.setAudioEffects("Chorus(delay1:466;amp1:0.54;delay2:600;amp2:-0.10;delay3:250;amp3:0.30)");

            //FIRFilter
            //mary.setAudioEffects("FIRFilter(type:3;fc1:500.0;fc2:2000.0)");

            //JetPilot
            //mary.setAudioEffects("Volume(amount:0.03)+JetPilot()");

            AudioInputStream audio = mary.generateAudio(input);

            ap.setAudio(audio);
            ap.start();

            ap.join();
            hideBubble();
        } catch (Exception ex) {
            System.err.println("Error saying phrase.");
        }
    }


    private void close() {
        running.compareAndSet(true, false);

        stage.close();
    }

    @Deprecated
    public synchronized void playSound(String soundName) {
        if (sounds.containsKey(soundName)) {
            text.setText(soundName);
            try {

                Media sound = new Media(new File(SOUND_BASE + soundName + ".mp3").toURI().toString());
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
}

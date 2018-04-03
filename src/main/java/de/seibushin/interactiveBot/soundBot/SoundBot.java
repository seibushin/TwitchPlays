/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.soundBot;

import de.seibushin.interactiveBot.Config;
import de.seibushin.interactiveBot.pointBot.PointBot;
import de.seibushin.interactiveBot.soundBot.model.Sound;
import de.seibushin.interactiveBot.soundBot.model.Sounds;
import de.seibushin.interactiveBot.twitch.TwitchChatBot;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
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

public class SoundBot {
    private final static String FXML_PATH = "/fxml/soundBot.fxml";
    private static final String SOUND_BASE = "res/sounds/";

    // config
    private int COST_SPEAK;

    private static SoundBot instance;

    private MaryInterface mary;

    private Stage stage;

    private AtomicBoolean playing = new AtomicBoolean(false);
    private volatile BooleanProperty running = new SimpleBooleanProperty(false);

    private final List<Sound> playList = new ArrayList<>();

    private Thread scheduler;

    @FXML
    private StackPane wrapper;
    @FXML
    private Label text;
    @FXML
    private StackPane bubble;

    private SoundBot() {

    }

    private void init() {
        stage = new Stage();

        stage.setTitle("SeiBot - SoundBot");
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

        stage.show();

        useConfig();
    }

    private void useConfig() {
        wrapper.setStyle("-fx-background-color: " + Config.getInstance().getSoundBot_bg());
    }

    public synchronized static SoundBot getInstance() {
        if (instance == null) {
            instance = new SoundBot();
        }
        return instance;
    }

    public BooleanProperty isRunning() {
        return running;
    }

    public synchronized void addToPlayList(String soundName, String user) {
        Sound sound = Sounds.getSound(soundName);

        if (sound != null) {
            int points = PointBot.getInstance().getPointsForViewer(user);

            if (points >= sound.getCost()) {
                PointBot.getInstance().addPointToViewer(user, -sound.getCost());
                System.out.println("add to playlist " + soundName);
                playList.add(sound);
            } else {
                try {
                    TwitchChatBot.getInstance().sendMessage(user + " du hast " + points + " brauchst aber " + sound.getCost() + " Punkte!");
                } catch (Exception e) {
                    System.out.println(user + " du hast " + points + " brauchst aber " + sound.getCost() + " Punkte!");
                }
            }
        }
    }

    private void setBubbleText(String msg) {
        Platform.runLater(() -> {
            bubble.setVisible(true);
            text.setText(msg);
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
            Sound sound = playList.remove(0);

            setBubbleText(sound.getMsg());

            if (sound.getKey() == null) {
                // speak
                say(sound.getMsg(), "de");
            } else {
                // play sound
                Media media = new Media(new File(SOUND_BASE + sound.getKey() + ".mp3").toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnEndOfMedia(() -> {
                    hideBubble();
                });

                mediaPlayer.play();
            }
        }
    }

    private void scheduler() {
        scheduler = new Thread(() -> {
            try {
                mary = new LocalMaryInterface();

                mary.getAvailableLocales().forEach(locale -> {
                    System.out.println(locale);
                });

                mary.getAvailableVoices().forEach(s -> {
                    System.out.println(s);
                });

            } catch (MaryConfigurationException ex) {
                ex.printStackTrace();
            }

            while (running.get()) {
                while (playing.get()) {
                    // wait
                }
                playNextSound();
            }
        });

        scheduler.start();
    }

    public synchronized void start() {
        if (!running.get()) {
            running.set(true);

            getConfig();
            init();

            // start scheduler
            scheduler();
        }
    }

    private void getConfig() {
        COST_SPEAK = Config.getInstance().getSoundBot_speakCost();
    }

    public void addSpeak(String msg, String user) {
        if (!"".equals(msg)) {
            int points = PointBot.getInstance().getPointsForViewer(user);

            if (points >= COST_SPEAK) {
                PointBot.getInstance().addPointToViewer(user, -COST_SPEAK);
                System.out.println("add to playlist [speak]" + msg);
                playList.add(new Sound(msg));
            } else {
                try {
                    TwitchChatBot.getInstance().sendMessage(user + " du hast " + points + " brauchst aber " + COST_SPEAK + " Punkte!");
                } catch (Exception e) {
                    System.out.println(user + " du hast " + points + " brauchst aber " + COST_SPEAK + " Punkte!");
                }
            }
        }
    }

    private void speak(String msg, String user) {
        int points = PointBot.getInstance().getPointsForViewer(user);

        if (points >= COST_SPEAK) {
            PointBot.getInstance().addPointToViewer(user, -COST_SPEAK);

            setBubbleText(msg);
            say(msg, "de");
            //say(msg, "de");
        } else {
            TwitchChatBot.getInstance().sendMessage(user + " du hast " + points + " brauchst aber " + COST_SPEAK + " Punkte!");
        }
    }

    private void say(String input, String voice) {
        try {
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
            System.err.println(ex);
        }
    }

    public synchronized void close() {
        if (running.get()) {
            running.set(false);
            stage.close();
        }
    }
}

/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.apm;

import de.seibushin.interactiveBot.apm.model.Key;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ApmBot2 implements Runnable {
    private static final String FXML_PATH = "/fxml/apmKeyMouse.fxml";
    private static final int MAX_KEY_DUR = 5 * 1000; // 5 seconds
    private static final int KEY_COUNT = 10;

    private static ApmBot2 instance;

    private Stage stage;
    private volatile BooleanProperty running = new SimpleBooleanProperty(false);
    private GlobalKeyboardHook keyboardHook;
    private GlobalMouseHook mouseHook;

    private final HashMap<Integer, Integer> actionsKB = new HashMap<>(60);
    private final HashMap<Integer, Integer> actionsMouse = new HashMap<>(60);

    @FXML
    private ListView<Key> keys;
    @FXML
    private ListView<Key> mouse;
    @FXML
    private Label apm_kb;
    @FXML
    private Label apm_mouse;
    @FXML
    private Label apm;

    @FXML
    private ImageView key_q;

    @FXML
    private VBox wrapper;

    public ApmBot2() {

    }

    public synchronized static ApmBot2 getInstance() {
        if (instance == null) {
            instance = new ApmBot2();
        }

        return instance;
    }

    public synchronized BooleanProperty isRunning() {
        return running;
    }

    public synchronized void start() {
        if (!running.get()) {
            System.out.println("Start KeyBot2");
            running.set(true);
            init();

            new Thread(this).start();
        }
    }

    private void init() {
        try {
            keyboardHook = new GlobalKeyboardHook(); // use false here to switch to hook instead of raw input
            mouseHook = new GlobalMouseHook();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

        stage = new Stage();

        stage.setTitle("SeiBot - KeyBot2");
        stage.setResizable(false);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
            // use this class as controller
            fxmlLoader.setController(this);
            Scene scene = new Scene(fxmlLoader.load());

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setOnCloseRequest(event -> {
            close();
        });

        stage.show();
    }

    private void startListener() {
        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyPressed(GlobalKeyEvent event) {
                ImageView key = (ImageView) wrapper.lookup("#key" + event.getVirtualKeyCode());

                if (key != null) {

                    String keyImage = "key";
                    if (event.getVirtualKeyCode() == 32 || event.getVirtualKeyCode() == 9 || event.getVirtualKeyCode() == 20) {
                        keyImage = "space";
                    }

                    // todo change this
                    File f1 = new File("res/apm/" + keyImage + "_down.png");
                    File f2 = new File("res/apm/" + keyImage + ".png");

                    Image start = new Image(f1.toURI().toString());
                    Image stop = new Image(f2.toURI().toString());
                    // todo change above

                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().addAll(
                            new KeyFrame(Duration.ZERO,
                                    new KeyValue(key.imageProperty(), start)
                            ),
                            new KeyFrame(new Duration(100),
                                    new KeyValue(key.imageProperty(), stop)
                            ));
                    timeline.play();
                }
            }

            @Override
            public void keyReleased(GlobalKeyEvent event) {
                int currentSecond = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60;
                actionsKB.put(currentSecond, actionsKB.getOrDefault(currentSecond, 0) + 1);
            }
        });

        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mousePressed(GlobalMouseEvent event) {
                int currentSecond = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60;
                actionsMouse.put(currentSecond, actionsMouse.getOrDefault(currentSecond, 0) + 1);

                ImageView mouse = (ImageView) wrapper.lookup("#mouse" + event.getButton());

                String mouseImage = "middle";
                if (event.getButton() == 1) {
                    mouseImage = "left";
                } else if (event.getButton() == 2) {
                    mouseImage = "right";
                }

                // todo change this
                File f1 = new File("res/apm/" + mouseImage + "_down.png");
                File f2 = new File("res/apm/" + mouseImage + ".png");

                Image start = new Image(f1.toURI().toString());
                Image stop = new Image(f2.toURI().toString());
                // todo change above

                Timeline timeline = new Timeline();
                timeline.getKeyFrames().addAll(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(mouse.imageProperty(), start)
                        ),
                        new KeyFrame(new Duration(100),
                                new KeyValue(mouse.imageProperty(), stop)
                        ));
                timeline.play();
            }
        });
    }

    @Override
    public void run() {
        startListener();

        while (running.get()) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            int currentSecond = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60;
            actionsKB.put((currentSecond + 1) % 60, 0);
            actionsMouse.put((currentSecond + 1) % 60, 0);

            int apmKB = actionsKB.values().stream().reduce((i1, i2) -> i1 + i2).get();
            int apmMouse = actionsMouse.values().stream().reduce((i1, i2) -> i1 + i2).get();

            Platform.runLater(() -> {
                apm_kb.setText(apm_kb.getText().replaceAll("\\d+", "") + apmKB);
                apm_mouse.setText(apm_mouse.getText().replaceAll("\\d+", "") + apmMouse);
                apm.setText(apm.getText().replaceAll("\\d+", "") + (apmKB + apmMouse));
            });
        }
    }

    public synchronized void close() {
        if (running.get()) {
            System.out.println("Close KeyBot2");
            running.set(false);
            keyboardHook.shutdownHook();
            mouseHook.shutdownHook();
        }
    }
}

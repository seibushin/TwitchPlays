/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.apm;

import de.seibushin.interactiveBot.Config;
import de.seibushin.interactiveBot.apm.model.Key;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.CacheHint;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

public class ApmBot implements Runnable {
    private static final String FXML_PATH_OLD = "/fxml/keyBot.fxml";

    private static final String FXML_PATH_VISUAL = "/fxml/apmKeyMouse.fxml";

    private static final int MAX_KEY_DUR = 5 * 1000; // 5 seconds
    private static final int KEY_COUNT = 10;

    private static ApmBot instance;

    private Stage stage;
    private volatile BooleanProperty running = new SimpleBooleanProperty(false);
    private GlobalKeyboardHook keyboardHook;
    private GlobalMouseHook mouseHook;

    private volatile ObservableList<Key> keyList = FXCollections.observableArrayList();
    private volatile ObservableList<Key> mouseList = FXCollections.observableArrayList();
    private final HashMap<Integer, Integer> actionsKB = new HashMap<>(60);
    private final HashMap<Integer, Integer> actionsMouse = new HashMap<>(60);

    private boolean visual = true;

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
    private VBox wrapper;
    @FXML
    private HBox apm_wrapper;
    @FXML
    private VBox apm_mouseWrapper;
    @FXML
    private VBox apm_keyboardWrapper;

    public ApmBot() {

    }

    public synchronized static ApmBot getInstance() {
        if (instance == null) {
            instance = new ApmBot();
        }

        return instance;
    }

    public synchronized BooleanProperty isRunning() {
        return running;
    }

    public synchronized void start(boolean visual) {
        this.visual = visual;
        if (!running.get()) {
            System.out.println("Start ApmBot");
            running.set(true);
            init();

            new Thread(this).start();
        }
    }

    private void init() {
        try {
            keyboardHook = new GlobalKeyboardHook();
            mouseHook = new GlobalMouseHook();
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

        stage = new Stage();

        stage.setTitle("SeiBot - ApmBot");
        stage.setResizable(false);

        try {
            String fxml = FXML_PATH_VISUAL;
            if (!visual) {
                fxml = FXML_PATH_OLD;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxml));
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

        if (!visual) {
            keys.setItems(keyList);
            keys.setCellFactory(param -> new KeyCell());
            keys.setPrefHeight(KEY_COUNT * 50);

            mouse.setItems(mouseList);
            mouse.setCellFactory(param -> new KeyCell());
            mouse.setPrefHeight(KEY_COUNT * 50);
        }

        stage.show();

        useConfig();
    }

    private void useConfig() {
        // show hide keyboard
        apm_keyboardWrapper.setVisible(Config.getInstance().isApm_showKey());
        apm_keyboardWrapper.setManaged(Config.getInstance().isApm_showKey());

        // recolor the keys
        if (Config.getInstance().isApm_showKey()) {
            apm_wrapper.lookupAll(".apm-img-key").forEach(node -> {
                ImageView view = (ImageView) node;
                recolor(view);
            });
        }

        // show hide mouse
        apm_mouseWrapper.setVisible(Config.getInstance().isApm_showMouse());
        apm_mouseWrapper.setManaged(Config.getInstance().isApm_showMouse());

        // recolor mouse
        if (Config.getInstance().isApm_showMouse()) {
            if (Config.getInstance().isApm_showKey()) {
                apm_wrapper.lookupAll(".apm-img-mouse").forEach(node -> {
                    ImageView view = (ImageView) node;
                    recolor(view);
                });
            }
        }

        // set background color
        wrapper.setStyle("-fx-background-color: " + Config.getInstance().getApm_bg());

        // set mainTextColor
        wrapper.lookupAll(".apm-main-label").forEach(node -> {
            Label label = (Label) node;
            label.setStyle("-fx-text-fill: " + Config.getInstance().getApm_mainTextColor());
        });

        apm_wrapper.lookupAll(".apm-key-label").forEach(node -> {
            Label label = (Label) node;
            label.setStyle("-fx-text-fill: " + Config.getInstance().getApm_textColor());
        });

        // recalc scene size
        stage.sizeToScene();
    }

    private void recolor(ImageView view) {
        ColorAdjust monochrome = new ColorAdjust();
        monochrome.setSaturation(-1.0);

        ImageView clone = new ImageView(view.getImage());
        clone.setFitWidth(view.getFitWidth());
        clone.setFitHeight(view.getFitHeight());
        view.setClip(clone);

        Blend color = new Blend(
                BlendMode.MULTIPLY,
                monochrome,
                new ColorInput(
                        0,
                        0,
                        view.getFitWidth(),
                        view.getFitHeight(),
                        Color.web(Config.getInstance().getApm_color())
                )
        );

        view.setEffect(color);
        view.setCache(true);
        view.setCacheHint(CacheHint.SPEED);
    }

    private void startListener() {
        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyPressed(GlobalKeyEvent event) {
                if (visual) {
                    ImageView key = (ImageView) apm_wrapper.lookup("#key" + event.getVirtualKeyCode());

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
            }

            @Override
            public void keyReleased(GlobalKeyEvent event) {
                int currentSecond = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60;
                actionsKB.put(currentSecond, actionsKB.getOrDefault(currentSecond, 0) + 1);

                if (!visual) {
                    long eventTime = System.currentTimeMillis();
                    Platform.runLater(() -> {
                        if (keyList.size() > 9) {
                            keyList.remove(0);
                        }

                        keyList.add(new Key((event.getKeyChar() + "").toUpperCase(), event.getVirtualKeyCode(), eventTime, 0));
                    });
                }
            }
        });

        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mousePressed(GlobalMouseEvent event) {
                int currentSecond = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60;
                actionsMouse.put(currentSecond, actionsMouse.getOrDefault(currentSecond, 0) + 1);

                if (!visual) {
                    long eventTime = System.currentTimeMillis();

                    Platform.runLater(() -> {
                        if (mouseList.size() > 9) {
                            mouseList.remove(0);
                        }
                        mouseList.add(new Key(event.getButton() + "", event.getButton(), eventTime, 0));
                    });
                } else {
                    ImageView mouse = (ImageView) apm_wrapper.lookup("#mouse" + event.getButton());

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

            if (!visual) {
                for (int i = 0; i < keyList.size(); i++) {
                    Key key = keyList.get(i);

                    if ((key.getEventTime() + MAX_KEY_DUR) < System.currentTimeMillis()) {
                        Platform.runLater(() -> {
                            keyList.remove(key);
                        });
                    }
                }

                for (int i = 0; i < mouseList.size(); i++) {
                    Key key = mouseList.get(i);

                    if ((key.getEventTime() + MAX_KEY_DUR) < System.currentTimeMillis()) {
                        Platform.runLater(() -> {
                            mouseList.remove(key);
                        });
                    }
                }
            }

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
            System.out.println("Close ApmBot");
            running.set(false);
            keyboardHook.shutdownHook();
            mouseHook.shutdownHook();
        }
    }
}

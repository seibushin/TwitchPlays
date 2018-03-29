/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.apm;

import de.seibushin.interactiveBot.apm.model.Key;
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
import javafx.stage.Stage;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;
import lc.kra.system.mouse.GlobalMouseHook;
import lc.kra.system.mouse.event.GlobalMouseAdapter;
import lc.kra.system.mouse.event.GlobalMouseEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ApmBot implements Runnable {
    private static final String FXML_PATH = "/fxml/keyBot.fxml";
    private static final int MAX_KEY_DUR = 5 * 1000; // 5 seconds
    private static final int KEY_COUNT = 10;

    private static ApmBot instance;

    private Stage stage;
    private volatile BooleanProperty running = new SimpleBooleanProperty(false);
    private GlobalKeyboardHook keyboardHook;
    private GlobalMouseHook mouseHook;

    private volatile ObservableList<Key> keyList = FXCollections.observableArrayList();
    private volatile ObservableList<Key> mouseList = FXCollections.observableArrayList();
    private long lastKeyPressed;
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
        if (!running.get()) {
            System.out.println("Start KeyBot");
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

        stage.setTitle("SeiBot - APMBot");
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

        keys.setItems(keyList);
        keys.setCellFactory(param -> new KeyCell());
        keys.setPrefHeight(KEY_COUNT * 50);

        mouse.setItems(mouseList);
        mouse.setCellFactory(param -> new KeyCell());
        mouse.setPrefHeight(KEY_COUNT * 50);

        stage.show();
    }

    private void startListener() {
        lastKeyPressed = System.currentTimeMillis();

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyPressed(GlobalKeyEvent event) {
                long eventTime = System.currentTimeMillis();
                long timeDelta = eventTime - lastKeyPressed;
                lastKeyPressed = eventTime;

                int currentSecond = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60;
                actionsKB.put(currentSecond, actionsKB.getOrDefault(currentSecond, 0) + 1);

                Platform.runLater(() -> {
                    if (keyList.size() > 9) {
                        keyList.remove(0);
                    }

                    keyList.add(new Key((event.getKeyChar() + "").toUpperCase(), event.getVirtualKeyCode(), eventTime, timeDelta));
                });
            }
        });

        mouseHook.addMouseListener(new GlobalMouseAdapter() {
            @Override
            public void mousePressed(GlobalMouseEvent event) {
                long eventTime = System.currentTimeMillis();

                int currentSecond = (int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % 60;
                actionsMouse.put(currentSecond, actionsMouse.getOrDefault(currentSecond, 0) + 1);

                Platform.runLater(() -> {
                    if (mouseList.size() > 9) {
                        mouseList.remove(0);
                    }
                    mouseList.add(new Key(event.getButton() + "", event.getButton(), eventTime, 0));
                });
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
            actionsMouse.put((currentSecond + 1) %60, 0);

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

            int apmKB = actionsKB.values().stream().reduce((i1, i2) -> i1 + i2).get();
            int apmMouse = actionsMouse.values().stream().reduce((i1, i2) -> i1 + i2).get();

            Platform.runLater(() -> {
                apm_kb.setText(apmKB + "");
                apm_mouse.setText(apmMouse + "");
                apm.setText((apmKB + apmMouse) + "");
            });
        }
    }

    public synchronized void close() {
        if (running.get()) {
            System.out.println("Close KeyBot");
            running.set(false);
            keyboardHook.shutdownHook();
            mouseHook.shutdownHook();
        }
    }
}

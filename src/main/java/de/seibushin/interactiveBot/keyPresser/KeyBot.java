/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.keyPresser;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import lc.kra.system.keyboard.GlobalKeyboardHook;
import lc.kra.system.keyboard.event.GlobalKeyAdapter;
import lc.kra.system.keyboard.event.GlobalKeyEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeyBot {
    private static final String FXML_PATH = "/fxml/keyBot.fxml";

    private static KeyBot instance;

    private Stage stage;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private GlobalKeyboardHook keyboardHook;

    @FXML
    private ListView<Key> keys;

    public KeyBot() {

    }

    public synchronized static KeyBot getInstance() {
        if (instance == null) {
            instance = new KeyBot();
        }

        return instance;
    }

    public void start() {
        if (running.compareAndSet(false, true)) {
            init();

            startListener();
        }
    }

    private void init() {
        try {
            keyboardHook = new GlobalKeyboardHook(true); // use false here to switch to hook instead of raw input
        } catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }

        stage = new Stage();

        stage.setTitle("SeiBot - KeyBot");
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
            if (running.compareAndSet(true, false)) {
                keyboardHook.shutdownHook();
            }
        });

        keys.setItems(list);
        keys.setCellFactory(param -> {
            return new KeyCell();
        });

        keys.setPrefHeight(10 * 50);

        stage.show();
    }

    private ObservableList<Key> list = FXCollections.observableArrayList();

    private long lastKeyPressed;

    private void startListener() {
        for (Map.Entry<Long, String> keyboard : GlobalKeyboardHook.listKeyboards().entrySet())
            System.out.format("%d: %s\n", keyboard.getKey(), keyboard.getValue());

        keyboardHook.addKeyListener(new GlobalKeyAdapter() {
            @Override
            public void keyPressed(GlobalKeyEvent event) {
                long timeDelta = System.currentTimeMillis() - lastKeyPressed;
                lastKeyPressed = System.currentTimeMillis();

                System.out.println(event.getKeyChar() + " " + event.getVirtualKeyCode() + " " + event.getTransitionState());

                Platform.runLater(() -> {
                    if (list.size() > 9) {
                        list.remove(0);
                    }
                    list.add(new Key(event.getKeyChar() + "", event.getVirtualKeyCode(), timeDelta));
                });

            }
        });
    }
}

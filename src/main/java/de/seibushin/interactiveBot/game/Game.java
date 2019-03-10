/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.game;

import de.seibushin.interactiveBot.game.character.Bulbasaur;
import de.seibushin.interactiveBot.game.character.CharacterInterface;
import de.seibushin.interactiveBot.game.character.Venusaur;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

import java.io.IOException;

public class Game implements Runnable {
    private static final String FXML_PATH = "/fxml/game.fxml";


    private static Game instance;

    private Stage stage;
    private volatile BooleanProperty running = new SimpleBooleanProperty(false);

    @FXML
    private Canvas canvas;
    private GraphicsContext graphicsContext;

    public Game() {

    }

    public synchronized static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }

        return instance;
    }

    public synchronized BooleanProperty isRunning() {
        return running;
    }

    public synchronized void start() {
        if (!running.get()) {
            System.out.println("Start Game");
            running.set(true);
            init();

            new Thread(this).start();
        }
    }

    private void init() {
        stage = new Stage();

        stage.setTitle("SeiBot - Game");
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
            //close();
        });

        stage.show();
    }

    @FXML
    private void initialize() {
        graphicsContext = canvas.getGraphicsContext2D();
        char1 = new Bulbasaur(true, canvas.getWidth(), canvas.getHeight());
        char2 = new Venusaur(false, canvas.getWidth(), canvas.getHeight());
    }

    private CharacterInterface char1;
    private CharacterInterface char2;

    @Override
    public void run() {
        //char1.changeDirection();

        char1.setCommand("abcabc");
        char1.changeDirection();

        char2.setCommand("aaaaaa");

        gameLoop();
    }

    private void gameLoop() {
        long before = System.currentTimeMillis();
        long now;
        long sleep;

        while (running.get()) {
            collision();
            draw();

            now = System.currentTimeMillis();
            // 30 frames per second
            sleep = Math.max(0, 40 - (now - before));
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException e) {
                // do nothing
            }
            before = System.currentTimeMillis();
        }
    }

    private void collision() {
        char1.intersects(char2);
        char2.intersects(char1);
    }

    private void draw() {
        graphicsContext.clearRect(0,0,600,200);

        char1.draw(graphicsContext);
        char2.draw(graphicsContext);
    }

    // todo close()
}

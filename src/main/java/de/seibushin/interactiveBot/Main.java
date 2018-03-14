package de.seibushin.interactiveBot;
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import de.seibushin.interactiveBot.keyPresser.KeyBot;
import de.seibushin.interactiveBot.lol.LoL;
import de.seibushin.interactiveBot.oMeter.OMeter;
import de.seibushin.interactiveBot.pointBot.PointBot;
import de.seibushin.interactiveBot.soundBot.SoundBot;
import de.seibushin.interactiveBot.twitch.TwitchChatBot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    private final static String FXML_PATH = "/fxml/main.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("InteractiveBot v0.1");
        stage.setResizable(false);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
        // use this class as controller
        fxmlLoader.setController(this);

        Scene scene = new Scene(fxmlLoader.load());

        // show stage
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> close());
    }

    @FXML
    private void startKeyBot() {
        KeyBot.getInstance().start();
    }

    @FXML
    private void startPointBot() {
        PointBot.getInstance().start();
    }

    @FXML
    private void startChatBot() {
        TwitchChatBot.getInstance().start();
    }

    @FXML
    private void startLoL() {
        LoL.getInstance().next();
    }

    @FXML
    private void startOMeter() {
        OMeter.getInstance().start();
    }

    @FXML
    private void startSound() {
        // start audio
        SoundBot.getInstance().start();
    }

    private void close() {
        PointBot.getInstance().close();

        Platform.exit();
        // terminate process
        System.exit(0);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        close();
    }
}

package de.seibushin.interactiveBot;
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import de.seibushin.interactiveBot.apm.ApmBot;
import de.seibushin.interactiveBot.apm.ApmBot2;
import de.seibushin.interactiveBot.lol.LolBot;
import de.seibushin.interactiveBot.oMeter.OMeter;
import de.seibushin.interactiveBot.pointBot.PointBot;
import de.seibushin.interactiveBot.soundBot.SoundBot;
import de.seibushin.interactiveBot.twitch.TwitchChatBot;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class Main extends Application {
    private final static String FXML_PATH = "/fxml/main.fxml";

    @FXML
    private RadioButton chatBotStatus;
    @FXML
    private RadioButton pointBotStatus;
    @FXML
    private RadioButton lolStatus;
    @FXML
    private RadioButton oMeterStatus;
    @FXML
    private RadioButton soundBotStatus;
    @FXML
    private RadioButton APMBotStatus;
    @FXML
    private RadioButton APMBot2Status;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("SeiBot v0.1");
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
    private void initialize() {
        chatBotStatus.selectedProperty().bind(TwitchChatBot.getInstance().isRunning());
        pointBotStatus.selectedProperty().bind(PointBot.getInstance().isRunning());
        APMBotStatus.selectedProperty().bind(ApmBot.getInstance().isRunning());
        APMBot2Status.selectedProperty().bind(ApmBot2.getInstance().isRunning());
        soundBotStatus.selectedProperty().bind(SoundBot.getInstance().isRunning());
        oMeterStatus.selectedProperty().bind(OMeter.getInstance().isRunning());

        //lolStatus.selectedProperty().bind(PointBot.getInstance().isRunning());
    }

    @FXML
    private void startChatBot() {
        if (!TwitchChatBot.getInstance().isRunning().get()) {
            TwitchChatBot.getInstance().start();
        } else {
            TwitchChatBot.getInstance().close();
        }
    }

    @FXML
    private void startPointBot() {
        if (!PointBot.getInstance().isRunning().get()) {
            PointBot.getInstance().start();
        } else {
            PointBot.getInstance().close();
        }
    }


    @FXML
    private void startAPMBot() {
        if (!ApmBot.getInstance().isRunning().get()) {
            ApmBot.getInstance().start(true);
        } else {
            ApmBot.getInstance().close();
        }
    }

    @FXML
    private void startAPMBot2() {
        if (!ApmBot2.getInstance().isRunning().get()) {
            ApmBot2.getInstance().start();
        } else {
            ApmBot2.getInstance().close();
        }
    }

    @FXML
    private void startLoL() {
        LolBot.getInstance().next();
    }

    @FXML
    private void startOMeter() {
        if (!OMeter.getInstance().isRunning().get()) {
            OMeter.getInstance().start();
        } else {
            OMeter.getInstance().close();
        }
    }

    @FXML
    private void startSound() {
        if (!SoundBot.getInstance().isRunning().get()) {
            // start audio
            SoundBot.getInstance().start();
        } else {
            SoundBot.getInstance().close();
        }
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
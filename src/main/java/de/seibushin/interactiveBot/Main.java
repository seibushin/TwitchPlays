package de.seibushin.interactiveBot;
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import de.seibushin.interactiveBot.apm.ApmBot;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
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

    // Config
    @FXML
    private TextField channel;
    @FXML
    private TextField disEveryMin;
    @FXML
    private TextField pointsPerDis;
    @FXML
    private TextField speakCost;
    @FXML
    private TextField oMeter_factor;
    @FXML
    private TextField oMeter_normSleep;
    @FXML
    private TextField oMeter_normFactor;
    @FXML
    private TextField oMeter_min;
    @FXML
    private TextField oMeter_max;
    @FXML
    private ColorPicker apm_color;
    @FXML
    private ColorPicker apm_bg;
    @FXML
    private CheckBox apm_showKey;
    @FXML
    private CheckBox apm_showMouse;



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        try {
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
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        chatBotStatus.selectedProperty().bind(TwitchChatBot.getInstance().isRunning());
        pointBotStatus.selectedProperty().bind(PointBot.getInstance().isRunning());
        APMBotStatus.selectedProperty().bind(ApmBot.getInstance().isRunning());
        soundBotStatus.selectedProperty().bind(SoundBot.getInstance().isRunning());
        oMeterStatus.selectedProperty().bind(OMeter.getInstance().isRunning());
        //lolStatus.selectedProperty().bind(PointBot.getInstance().isRunning());

        // config bindings
        channel.textProperty().bindBidirectional(Config.getInstance().channelProperty());
        pointsPerDis.textProperty().bindBidirectional(Config.getInstance().pointsPerDisProperty());
        disEveryMin.textProperty().bindBidirectional(Config.getInstance().disEveryMinProperty());
        speakCost.textProperty().bindBidirectional(Config.getInstance().speakCostProperty());

        oMeter_factor.textProperty().bindBidirectional(Config.getInstance().oMeter_factorProperty());
        oMeter_normSleep.textProperty().bindBidirectional(Config.getInstance().oMeter_normSleepProperty());
        oMeter_normFactor.textProperty().bindBidirectional(Config.getInstance().oMeter_normFactorProperty());
        oMeter_min.textProperty().bindBidirectional(Config.getInstance().oMeter_minProperty());
        oMeter_max.textProperty().bindBidirectional(Config.getInstance().oMeter_maxProperty());

        apm_color.valueProperty().bindBidirectional(Config.getInstance().apm_colorProperty());
        apm_bg.valueProperty().bindBidirectional(Config.getInstance().apm_bgProperty());
        apm_showKey.selectedProperty().bindBidirectional(Config.getInstance().apm_showKeyProperty());
        apm_showMouse.selectedProperty().bindBidirectional(Config.getInstance().apm_showMouseProperty());
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
        Config.getInstance().close();
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
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.oMeter;

import de.seibushin.interactiveBot.Config;
import de.seibushin.interactiveBot.helper.GuiHelper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class OMeter {
    private static final String FXML_PATH = "/fxml/oMeter.fxml";

    // config
    private double FACTOR;
    private int NORMALIZER_SLEEP;
    private double NORMALIZER_FACTOR;
    private double NORMALIZER_MIN;
    private int NORMALIZER_MAX;

    private static OMeter instance;
    private Stage stage;

    private volatile DoubleProperty value = new SimpleDoubleProperty();
    private volatile BooleanProperty running = new SimpleBooleanProperty(false);

    private Thread normalizer;

    @FXML
    private ImageView pointer;
    @FXML
    private ImageView drag;
    @FXML
    private Group hover;
    @FXML
    private Group controls;

    public OMeter() {

    }

    private void init() {
        stage = new Stage();

        stage.setTitle("SeiBot - OMeter");
        stage.setResizable(false);
        //stage.initStyle(StageStyle.UNDECORATED);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
            // use this class as controller
            fxmlLoader.setController(this);
            Scene scene = new Scene(fxmlLoader.load());

            // create rotate transform to define the pivot of the rotation
            Rotate rotate = new Rotate(0, pointer.getBoundsInParent().getWidth() / 2, pointer.getBoundsInParent().getHeight() - 50);
            // bind the value to it
            rotate.angleProperty().bind(value);
            // add the rotate to the pointers transforms
            pointer.getTransforms().add(rotate);

            // addFade and drag
            GuiHelper.addFade(controls);
            /*
            GuiHelper.addFade(hover);

            // check if this is ok
            new GuiHelper.DragStage(stage, drag);
            */

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setOnCloseRequest(event -> {
            close();
        });

        stage.show();

        value.setValue(0);
    }

    /**
     * Singleton
     *
     * @return
     */
    public synchronized static OMeter getInstance() {
        if (instance == null) {
            instance = new OMeter();
        }
        return instance;
    }

    public BooleanProperty isRunning() {
        return running;
    }

    /**
     * Start the widget
     */
    public synchronized void start() {
        if (!running.get()) {
            System.out.println("start oMeter");
            running.set(true);

            getConfig();
            init();

            startNormalizer();
        }
    }

    private void getConfig() {
        FACTOR = Config.getInstance().getoMeter_factor();
        NORMALIZER_SLEEP = Config.getInstance().getoMeter_normSleep();
        NORMALIZER_FACTOR = Config.getInstance().getoMeter_factor();
        NORMALIZER_MIN = Config.getInstance().getoMeter_min();
        NORMALIZER_MAX = Config.getInstance().getoMeter_max();
    }

    /**
     * Create a new Instance of the normalizer Thread and start it
     */
    private void startNormalizer() {
        normalizer = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(NORMALIZER_SLEEP);
                    update(0, true);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        // start normalizer Thread
        try {
            normalizer.start();
        } catch (IllegalThreadStateException e) {
            e.printStackTrace();
        }
    }

    private synchronized void update(double add, boolean normalize) {
        if (normalize) {
            int times = Math.abs((int)((value.get() * NORMALIZER_FACTOR) / NORMALIZER_MIN));

            if (times > NORMALIZER_MAX) {
                times = NORMALIZER_MAX;
            } else if (Math.abs(value.get()) > 0 && times == 0) {
                times = 1;
            }

            if (value.get() > 0) {
                times *= -1;
            }
            add = NORMALIZER_MIN * times;
        }

        if (value.get() + add > 90.0) {
            value.set(90.0);
        } else if (value.get() + add < -90.0) {
            value.set(-90.0);
        } else {
            value.set(value.get() + add);
        }
    }

    /**
     *
     * @param times
     */
    public void update(int times) {
        update(times * FACTOR, false);
    }

    /**
     * Increase the current value by {@link #FACTOR}
     */
    @FXML
    private void increase() {
        System.out.println("oMeter change: " + FACTOR);
        update(FACTOR, false);
    }

    /**
     * Decrease the current value by {@link #FACTOR}
     */
    @FXML
    private void decrease() {
        System.out.println("oMeter change: " + FACTOR);
        update(-FACTOR, false);
    }

    @FXML
    public synchronized void close() {
        if (running.get()) {
            System.out.println("close oMeter");
            running.set(false);

            try {
                normalizer.interrupt();
                // Wait for the thread to die
                normalizer.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // close the stage
            stage.close();
        }
    }
}
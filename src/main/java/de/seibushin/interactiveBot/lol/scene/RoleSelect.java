package de.seibushin.interactiveBot.lol.scene;
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import de.seibushin.interactiveBot.lol.LolBot;
import de.seibushin.interactiveBot.lol.model.Roles;
import de.seibushin.interactiveBot.helper.Pos;
import de.seibushin.interactiveBot.helper.TPMath;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class RoleSelect {
    private Stage stage;

    private static final String FXML_PATH = "/fxml/roleSelect.fxml";
    private static final String LOCK_CLOSE = "/roleSelect/lock_close.png";

    private HashMap<String, Integer> roles = new HashMap<>();

    private BooleanProperty selecting = new SimpleBooleanProperty(false);

    @FXML
    private ImageView lock;
    @FXML
    private ImageView top;
    @FXML
    private ImageView jgl;
    @FXML
    private ImageView mid;
    @FXML
    private ImageView bot;
    @FXML
    private ImageView sup;
    @FXML
    private ImageView fill;
    @FXML
    private ImageView pos1;
    @FXML
    private ImageView pos2;

    public RoleSelect(Stage stage) {
        this.stage = stage;
    }

    public void start() {
        init();
        startChecker();

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
            // use this class as controller
            fxmlLoader.setController(this);
            Scene scene = new Scene(fxmlLoader.load());

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the role selection
     */
    private void init() {
        //stage.setTitle("Role Selection");

        for (Roles role : Roles.values()) {
            roles.put(role.name(), 0);
        }
    }

    /**
     * Increase the vote for the given pos
     *
     * @param pos
     */
    public void pick(String pos) {
        if (roles.containsKey(pos)) {
            roles.put(pos, roles.get(pos) + 1);
        }
    }

    private void startChecker() {
        selecting.setValue(true);

        new Thread(() -> {
            while (selecting.get()) {
                try {
                    update();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    AtomicInteger max1 = new AtomicInteger();
    AtomicInteger max2 = new AtomicInteger();
    AtomicReference<String> s1 = new AtomicReference<>("");
    AtomicReference<String> s2 = new AtomicReference<>("");

    /**
     * Update the GUI
     */
    private void update() {
        roles.forEach((s, i) -> {
            if (i > max1.get()) {
                if (!s.equals(s1.get())) {
                    max2.set(max1.get());
                    s2.set(s1.get());
                }
                max1.set(i);
                s1.set(s);
            } else if (i > max2.get() && !s.equals(s1.get())) {
                max2.set(i);
                s2.set(s);
            }
        });

        updatePos(max1.get());

        try {
            pos1.setImage(new Image("roleSelect/" + s1.get() + "_s.png"));
        } catch (IllegalArgumentException e) {

        }

        try {
            pos2.setImage(new Image("roleSelect/" + s2.get() + "_s.png"));
        } catch (IllegalArgumentException e) {

        }
    }

    private void updatePos(int max) {
        if (max > 0) {
            roles.forEach((s, i) -> {
                ImageView role = getImageView(s);
                if (role != null) {
                    double degVote = 180.0 / max;
                    double deg = degVote * i;

                    // calculate new position on the circle
                    Pos p = TPMath.semicirclePos(212, deg);
                    //System.out.println(s + " " + deg + " :" + p.getX() + " | " + p.getY());

                    // update Position
                    role.setTranslateX(p.getX());
                    role.setTranslateY(p.getY());
                }
            });
        }
    }

    /**
     * Get the ImageView for the given Position
     *
     * @param role
     * @return
     */
    private ImageView getImageView(String role) {
        ImageView iv = null;

        switch (role) {
            case "top":
                iv = top;
                break;
            case "jgl":
                iv = jgl;
                break;
            case "mid":
                iv = mid;
                break;
            case "bot":
                iv = bot;
                break;
            case "sup":
                iv = sup;
                break;
            case "fill":
                iv = fill;
                break;
        }

        return iv;
    }

    @FXML
    private void lock() {
        selecting.setValue(false);
        lock.setImage(new Image(LOCK_CLOSE));

        new Thread(() -> {
            try {
                Thread.sleep(1000);
                // execute on FX Thread
                Platform.runLater(() -> {
                    LolBot.getInstance().next();
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void inc(Event e) {
        String pick = ((ImageView) e.getSource()).getId();
        pick(pick);
    }

    /**
     * Stop Selecting
     */
    public void stop() {
        selecting.setValue(false);
    }

    @FXML
    private void close() {
        stage.close();
    }
}

package de.seibushin.interactiveBot.lol.scene;
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import de.seibushin.interactiveBot.lol.model.ChampVote;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ChampSelect {
    private static final String FXML_PATH = "/fxml/champSelect.fxml";

    private Stage stage;

    @FXML
    private ImageView splash;
    @FXML
    private ImageView one;
    @FXML
    private ImageView two;
    @FXML
    private ImageView three;
    @FXML
    private ImageView four;
    @FXML
    private ImageView five;

    private HashMap<String, Double> champs = new HashMap<>();

    private BooleanProperty selecting = new SimpleBooleanProperty(false);

    private List<ChampVote> picko = new ArrayList<>();


    public ChampSelect(Stage stage) {
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
            scene.setFill(Color.TRANSPARENT);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the champion selection
     */
    private void init() {
        File f = new File("images/champs");
        for (String s : f.list((dir, name) -> dir.isDirectory())) {
            //System.out.println(s);
            champs.put(s.toLowerCase(), 0.0);
        }

        for (int i = 0; i < 6; i++) {
            picko.add(i, new ChampVote("", 0));
        }
    }

    /**
     * Stop Selection
     */
    @FXML
    public void lockIn() {
        selecting.setValue(false);
    }

    public void pick(String name) {
        if (champs.containsKey(name)) {
            champs.put(name, champs.get(name) + 1);
        }
    }

    private void startChecker() {
        selecting.setValue(true);

        new Thread(() -> {
            while (selecting.get()) {
                try {
                    update();
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("lock in");
        }).start();
    }

    private void update() {
        List<Map.Entry<String, Double>> l = new ArrayList<>(champs.entrySet());
        Collections.sort(l, (o1, o2) -> (int) (o2.getValue() * 100 - o1.getValue() * 100));

        for (int i = 0; i < 6; i++) {
            double vote = l.get(i).getValue();
            String champ = l.get(i).getKey();

            for (int j = 0; j < 6; j++) {
                double currentVote = picko.get(j).getVote();
                String currentChamp = picko.get(j).getChamp();

                if (vote > currentVote) {
                    champs.put(champ, Math.floor(vote) + (6 - j) * 0.01);
                    picko.get(j).setChamp(champ);
                    picko.get(j).setVote(Math.floor(vote) + (6 - j) * 0.01);
                    j = 6;
                } else if (champ.equals(currentChamp)) {
                    j = 6;
                }
            }
        }

        loadChampSelectInfo();
    }

    /**
     * Load Champion info
     */
    private void loadChampSelectInfo() {
        for (int i = 1; i <= 5; i++) {
            loadIcon(i);
        }

        loadSplash();
    }

    private void loadIcon(int i) {
        // todo only update if necessary
        try {
            String name = picko.get(i).getChamp();

            double perVote = 1.0 / Math.floor(picko.get(0).getVote());
            double x = perVote * Math.floor(picko.get(i).getVote()) * 400;
            ImageView icon = getIconById(i);

            // reposition
            if (icon != null) {
                icon.setTranslateX(x);
            }


            File f = new File("images/champs/" + name + "/" + name + "_icon.png");
            if (f.exists()) {
                loadImage(icon, f.toURI().toString());
            }

            //picked.set(i, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Load Champion info
     */
    private void loadSplash() {
        //todo only update if necessary
        try {
            String name = picko.get(0).getChamp();
            File f = new File("images/champs/" + name + "/" + name + ".jpg");
            if (f.exists()) {
                loadImage(splash, f.toURI().toString());
            }
            //picked.set(0, name);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadImage(ImageView imageView, String uri) {
        Image image = new Image(uri, true);
        imageView.setImage(image);
    }

    private ImageView getIconById(int i) {
        switch (i) {
            case 1:
                return one;
            case 2:
                return two;
            case 3:
                return three;
            case 4:
                return four;
            case 5:
                return five;
        }
        return null;
    }

    private void addClip(int i) {
        ImageView imageView = getIconById(i);

        Circle clip = new Circle();
        clip.radiusProperty().bind(imageView.fitWidthProperty().multiply(0.5));
        clip.centerXProperty().bind(imageView.fitWidthProperty().multiply(0.5));
        clip.centerYProperty().bind(imageView.fitHeightProperty().multiply(0.5));

        imageView.setClip(clip);

        //imageView.setTranslateX(i *100);
    }

    @FXML
    private void initialize() {
        System.out.println("init");

        for (int i = 1; i < 6; i++) {
            addClip(i);
        }
    }

    public void stop() {
        selecting.setValue(false);
    }
}

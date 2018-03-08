package de.seibushin.interactiveBot.lol;
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import de.seibushin.interactiveBot.lol.scene.ChampSelect;
import de.seibushin.interactiveBot.lol.scene.Ingame;
import de.seibushin.interactiveBot.lol.scene.RoleSelect;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoL {
    private Stage stage = new Stage();
    private static LoL instance;

    private static final int X = 0;
    private static final int Y = 0;
    private static final int WIDTH = 805;
    private static final int HEIGHT = 475;

    private RoleSelect roleSelect = new RoleSelect(stage);
    private ChampSelect champSelect = new ChampSelect(stage);
    private Ingame ingame = new Ingame(stage);

    private List<Steps> steps = new ArrayList<>();
    private int step = -1;

    public LoL() {
        // no decoration
        stage.setTitle("InteractiveBot - LoL");
        //stage.initStyle(StageStyle.UNDECORATED);
        //stage.setAlwaysOnTop(true);
        //stage.setX(X);
        //stage.setY(Y);
        //stage.setWidth(WIDTH);
        //stage.setHeight(HEIGHT);

        Arrays.stream(Steps.values()).forEach(step -> {
            System.out.println(step.name());
            steps.add(step);
        });

        stage.setOnCloseRequest(event -> {
            step = -1;
            roleSelect.stop();
            champSelect.stop();
        });
    }

    /**
     * Singleton
     * @return
     */
    public synchronized static LoL getInstance() {
        if (instance == null) {
            instance = new LoL();
        }
        return instance;
    }

    /**
     * delegate call to fragment
     * @param pick
     */
    public void pick(String pick) {
        System.out.println(steps.get(step) + "pick " + pick);
        switch (steps.get(step)) {
            case role:
                roleSelect.pick(pick);
                break;
            case champ:
                // todo: create lockup Table
                champSelect.pick(pick);
                break;
        }
    }

    /**
     * Switch to next Scene
     */
    public synchronized void next() {
        System.out.println("next");
        step++;

        switch (steps.get(step)) {
            case role:
                roleSelect.start();
                break;
            case champ:
                champSelect.start();
                break;
            case ingame:
                ingame.start();
                break;
        }
        stage.show();
    }
}

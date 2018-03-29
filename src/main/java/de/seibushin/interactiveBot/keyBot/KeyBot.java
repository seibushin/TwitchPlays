package de.seibushin.interactiveBot.keyBot;
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import java.awt.*;
import java.awt.Robot;

public class KeyBot {
    private static KeyBot instance;

    private Robot robot;

    public KeyBot() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static synchronized KeyBot getInstance() {
        if (instance == null) {
            instance = new KeyBot();
        }
        return instance;
    }

    public void execute(String s) {
        System.out.println("Press " + s);
        // for the correct keycode use the uppercase
        char[] chars = s.toUpperCase().toCharArray();

        for (char aChar : chars) {
            robot.keyPress(aChar);
            robot.keyRelease(aChar);
        }
    }
}

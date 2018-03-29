/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.apm.model;

import java.util.HashMap;

public class KeyTranslate {
    private static HashMap<Integer, String> translate = new HashMap<>();

    static {
        translate.put(160, "\u21E7"); // shift LEFT
        translate.put(161, "\u21E7"); // shift RIGHT
        translate.put(38, "\u2191"); // arrow up
        translate.put(40, "\u2193"); // arrow down
        translate.put(37, "\u2190"); // arrow left
        translate.put(39, "\u2192"); // arrow right
        translate.put(162, "STRG"); // strg left
        translate.put(164, "ALT"); // alt left
        translate.put(9, "\u21B9"); // tab
        translate.put(20, "\u21E9"); // caps
        translate.put(13, "\u21B5"); // enter
        translate.put(8, "\u21D0"); // back
        translate.put(219, "ß"); // ß
        translate.put(220, "^"); // ^

        // mouse keys
        translate.put(1, "L"); // LM
        translate.put(2, "R"); // RM
        translate.put(16, "M"); // MM
    }

    public static String getTranslation(int keyCode, String keyChar) {
        return translate.getOrDefault(keyCode, keyChar);
    }
}

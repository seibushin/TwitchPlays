/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.soundBot.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class Sounds {
    private static final String SOUND_BASE = "res/sounds";
    private static final String SOUND_FILE = SOUND_BASE + "/sounds.txt";

    private static HashMap<String, Sound> sounds = new HashMap<>();

    static {
        try {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(SOUND_FILE)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(";");

                    if (parts.length >= 3) {
                        sounds.put(parts[0], new Sound(parts));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Sound getSound(String key) {
        return sounds.get(key);
    }
}

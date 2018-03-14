/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.soundBot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

public class Sounds {
    private static final String SOUND_BASE = "res/sounds";
    private static final String SOUND_FILE = SOUND_BASE + "/sounds.txt";

    public static HashMap<String, Integer> getAll() {
        HashMap<String, Integer> sounds = new HashMap<>();

        try {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(SOUND_FILE)))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(" ");

                    if (parts.length >= 2) {
                        sounds.put(parts[0], Integer.parseInt(parts[1]));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sounds;
    }
}

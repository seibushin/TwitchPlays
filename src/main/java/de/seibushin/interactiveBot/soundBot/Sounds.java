/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.soundBot;

import java.io.File;
import java.util.HashMap;

public class Sounds {
    private static final String SOUND_DIR = "res/sounds";

    public static HashMap<String, String> getAll() {
        HashMap<String, String> sounds = new HashMap<>();

        try {
            File dir = new File(SOUND_DIR);

            if (dir.exists() && dir.isDirectory()) {
                for (String f : dir.list()) {
                    sounds.put(f.replaceAll("(.*?)\\..*", "$1"), SOUND_DIR + "/" + f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sounds;
    }
}

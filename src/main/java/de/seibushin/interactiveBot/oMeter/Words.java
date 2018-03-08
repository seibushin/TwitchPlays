/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.oMeter;

import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

public class Words {
    private static final String NEG = "res/words/neg.txt";
    private static final String POS = "res/words/pos.txt";

    public static final HashMap<String, Integer> neg = new HashMap<>();
    public static final HashMap<String, Integer> pos = new HashMap<>();

    static {
        init();
    }

    private static void init() {
        try {
            File file = new File(NEG);
            Files.readLines(file, Charset.forName("UTF-8")).forEach(s -> {
                System.out.println(s);
                neg.put(s, 0);
            });

            file = new File(POS);
            Files.readLines(file, Charset.forName("UTF-8")).forEach(s -> {
                System.out.println(s);
                pos.put(s, 0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

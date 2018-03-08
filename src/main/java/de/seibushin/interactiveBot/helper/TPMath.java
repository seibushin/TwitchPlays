/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.helper;

import java.io.File;

public class TPMath {
    /*
     * max value is right
     */
    public static Pos semicirclePos(double radius, double deg) {
        double x = radius * Math.cos(Math.toRadians(deg)) * -1;
        double y = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2)) * -1;

        return new Pos(x, y);
    }

    public static void lowerCaseFileRename(File f) {
        for (File ch : f.listFiles()) {
            if (ch.isDirectory()) {
                String dir = ch.getName();
                for (File c : ch.listFiles()) {
                    if (c.getName().toLowerCase().startsWith(dir)) {
                        c.renameTo(new File("images/champs/" + dir + "/" + c.getName().toLowerCase()));
                    }
                }
            }
        }
    }

}

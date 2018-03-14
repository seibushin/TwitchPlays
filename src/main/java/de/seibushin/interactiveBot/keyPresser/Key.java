/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.keyPresser;

import lc.kra.system.keyboard.event.GlobalKeyEvent;

import java.util.concurrent.TimeUnit;

public class Key {
    private int keycode;
    private String keychar;
    private long time;

    public Key() {

    }

    public Key(String keychar, int keycode, long time) {
        this.keycode = keycode;
        this.keychar = keychar;
        this.time = time;
    }

    public Key(GlobalKeyEvent event) {
        this.keycode = event.getVirtualKeyCode();
        this.keychar = event.getKeyChar() + "";
        this.time = System.nanoTime();
    }

    public int getKeycode() {
        return keycode;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public String getKeychar() {
        return keychar;
    }

    public void setKeychar(String keychar) {
        this.keychar = keychar;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

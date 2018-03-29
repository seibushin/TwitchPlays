/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.apm.model;

import java.io.UnsupportedEncodingException;

public class Key {
    private int keycode;
    private String keychar;
    private long eventTime;
    private long delta;

    public Key() {
    }

    public Key(String keychar, int keycode, long eventTime, long delta) {
        this.keycode = keycode;
        this.keychar = keychar;
        this.eventTime = eventTime;
        this.delta = delta;
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

    public long getDelta() {
        return delta;
    }

    public void setDelta(long delta) {
        this.delta = delta;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
}

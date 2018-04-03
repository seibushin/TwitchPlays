/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.io.*;

public class Config {
    private static final String CONFIG_FILE = "res/config.txt";

    private static Config instance;

    // global
    // chatBot
    private StringProperty channel = new SimpleStringProperty("");
    // pointBot
    private StringProperty disEveryMin = new SimpleStringProperty("5");
    private StringProperty pointsPerDis = new SimpleStringProperty("1");
    // oMeter
    private StringProperty oMeter_factor = new SimpleStringProperty("5");
    private StringProperty oMeter_normSleep = new SimpleStringProperty("100");
    private StringProperty oMeter_normFactor = new SimpleStringProperty("0.005");
    private StringProperty oMeter_max = new SimpleStringProperty("8");
    private StringProperty oMeter_min = new SimpleStringProperty("0.025");
    // SoundBot
    private StringProperty speakCost = new SimpleStringProperty("10");
    // apmBot
    private ObjectProperty<Color> apm_color = new SimpleObjectProperty<>(Color.web("#ff0000"));
    private ObjectProperty<Color> apm_bg = new SimpleObjectProperty<>(Color.web("#000000"));
    private BooleanProperty apm_showKey = new SimpleBooleanProperty(true);
    private BooleanProperty apm_showMouse = new SimpleBooleanProperty(true);

    public Config() {
        init();
    }

    public static synchronized Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }

        return instance;
    }

    private void init() {
        try(BufferedReader br = new BufferedReader(new FileReader(new File(CONFIG_FILE)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length > 1) {
                    switch (parts[0]) {
                        case "channel":
                            channel.set(parts[1]);
                            break;
                        case "disEveryMin":
                            disEveryMin.set(parts[1]);
                            break;
                        case "pointsPerDis":
                            pointsPerDis.set(parts[1]);
                            break;
                        case "speakCost":
                            speakCost.set(parts[1]);
                            break;
                        case "oMeter_factor":
                            oMeter_factor.set(parts[1]);
                            break;
                        case "oMeter_normSleep":
                            oMeter_normSleep.set(parts[1]);
                            break;
                        case "oMeter_normFactor":
                            oMeter_normFactor.set(parts[1]);
                            break;
                        case "oMeter_max":
                            oMeter_max.set(parts[1]);
                            break;
                        case "oMeter_min":
                            oMeter_min.set(parts[1]);
                            break;
                        case "apm_color":
                            apm_color.set(Color.web(parts[1]));
                            break;
                        case "apm_bg":
                            apm_bg.set(Color.web(parts[1]));
                            break;
                        case "apm_showKey":
                            apm_showKey.set(Boolean.valueOf(parts[1]));
                            break;
                        case "apm_showMouse":
                            apm_showMouse.set(Boolean.valueOf(parts[1]));
                            break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getChannel() {
        return channel.get();
    }

    public StringProperty channelProperty() {
        return channel;
    }

    public int getDisEveryMin() {
        try {
            return Integer.parseInt(disEveryMin.get());
        } catch (NumberFormatException e) {
            // ignore
        }
        return 0;
    }

    public StringProperty disEveryMinProperty() {
        return disEveryMin;
    }

    public int getPointsPerDis() {
        try {
            return Integer.parseInt(pointsPerDis.get());
        } catch (NumberFormatException e) {
            // ignore
        }
        return 0;
    }

    public StringProperty pointsPerDisProperty() {
        return pointsPerDis;
    }

    public int getSpeakCost() {
        try {
            return Integer.parseInt(speakCost.get());
        } catch (NumberFormatException e) {
            // ignore
        }
        return 0;
    }

    public StringProperty speakCostProperty() {
        return speakCost;
    }

    public double getoMeter_factor() {
        try {
            return Double.parseDouble(oMeter_factor.get());
        } catch (NumberFormatException e) {
            //ignore
        }
        return 0;
    }

    public StringProperty oMeter_factorProperty() {
        return oMeter_factor;
    }

    public int getoMeter_normSleep() {
        try {
            return Integer.parseInt(oMeter_normSleep.get());
        } catch (NumberFormatException e) {
            //ignore
        }
        return 0;
    }

    public StringProperty oMeter_normSleepProperty() {
        return oMeter_normSleep;
    }

    public double getoMeter_normFactor() {
        try {
            return Double.parseDouble(oMeter_normFactor.get());
        } catch (NumberFormatException e) {
            //ignore
        }
        return 0;
    }

    public StringProperty oMeter_normFactorProperty() {
        return oMeter_normFactor;
    }

    public int getoMeter_max() {
        try {
            return Integer.parseInt(oMeter_max.get());
        } catch (NumberFormatException e) {
            //ignore
        }
        return 0;
    }

    public StringProperty oMeter_maxProperty() {
        return oMeter_max;
    }

    public double getoMeter_min() {
        try {
            return Double.parseDouble(oMeter_min.get());
        } catch (NumberFormatException e) {
            //ignore
        }
        return 0;
    }

    public StringProperty oMeter_minProperty() {
        return oMeter_min;
    }

    public String getApm_color() {
        return apm_color.get().toString();
    }

    public ObjectProperty<Color> apm_colorProperty() {
        return apm_color;
    }

    public String getApm_bg() {
        return apm_bg.get().toString();
    }

    public ObjectProperty<Color> apm_bgProperty() {
        return apm_bg;
    }

    public boolean isApm_showKey() {
        return apm_showKey.get();
    }

    public BooleanProperty apm_showKeyProperty() {
        return apm_showKey;
    }

    public boolean isApm_showMouse() {
        return apm_showMouse.get();
    }

    public BooleanProperty apm_showMouseProperty() {
        return apm_showMouse;
    }

    public void close() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(CONFIG_FILE)))) {
            bw.write("channel=" + channel.get() + "\r\n");
            bw.write("disEveryMin=" + disEveryMin.get() + "\r\n");
            bw.write("pointsPerDis=" + pointsPerDis.get() + "\r\n");
            bw.write("speakCost=" + speakCost.get() + "\r\n");
            bw.write("oMeter_factor=" + oMeter_factor.get() +"\r\n");
            bw.write("oMeter_normSleep=" + oMeter_normSleep.get() +"\r\n");
            bw.write("oMeter_normFactor=" + oMeter_normFactor.get() +"\r\n");
            bw.write("oMeter_max=" + oMeter_max.get() +"\r\n");
            bw.write("oMeter_min=" + oMeter_min.get() +"\r\n");
            bw.write("apm_color=" + apm_color.get() + "\r\n");
            bw.write("apm_bg=" + apm_bg.get().toString() + "\r\n");
            bw.write("apm_showKey=" + apm_showKey.get() + "\r\n");
            bw.write("apm_showMouse=" + apm_showMouse.get() + "\r\n");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

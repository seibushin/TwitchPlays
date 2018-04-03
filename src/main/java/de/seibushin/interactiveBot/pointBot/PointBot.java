/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.pointBot;

import de.seibushin.interactiveBot.Config;
import de.seibushin.interactiveBot.helper.JSONParser;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PointBot implements Runnable {
    private long INTERVAL;
    private int ADD_PER_INT;
    private String CHANNEL;

    private static final String SAVEFILE = "res/points.txt";
    private static final String SPLITTER = ";";

    private volatile BooleanProperty running = new SimpleBooleanProperty(false);
    private static PointBot instance;

    private volatile HashMap<String, Integer> pointMap = new HashMap<>();

    public PointBot() {

    }

    /**
     * Get the singleton instance of the bot
     *
     * @return
     */
    public static synchronized PointBot getInstance() {
        if (instance == null) {
            instance = new PointBot();
        }

        return instance;
    }

    public synchronized BooleanProperty isRunning() {
        return running;
    }

    /**
     * Start the pointBot in a seperate thread
     */
    public synchronized void start() {
        if (!running.get()) {
            System.out.println("Start PointBot");
            running.set(true);

            getConfig();

            new Thread(this).start();
        }
    }

    private void getConfig() {
        INTERVAL = Config.getInstance().getDisEveryMin();
        ADD_PER_INT = Config.getInstance().getPointsPerDis();
        CHANNEL = Config.getInstance().getChannel();
    }

    @Override
    public void run() {
        load();

        while (running.get()) {
            try {
                TimeUnit.MINUTES.sleep(INTERVAL);

                addPoints();
                //save();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Load the savefile
     */
    private void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(SAVEFILE)))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(SPLITTER);

                if (parts.length >= 2) {
                    System.out.println(parts[0] + "->" + parts[1]);
                    pointMap.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        } catch (FileNotFoundException e) {
            // ignore
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        if (running.get()) {
            System.out.println("Close PointBot");
            running.set(false);
            save();
        }
    }

    /**
     * Save the points into the savefile
     */
    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter((new File(SAVEFILE))))) {
            for (Map.Entry<String, Integer> entry : pointMap.entrySet()) {
                bw.write(entry.getKey() + SPLITTER + entry.getValue() + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Add 1 point to every user who is online in the chat
     */
    private void addPoints() {
        try {
            // get the json object
            JSONObject json = new JSONObject(JSONParser.readUrl(String.format("https://tmi.twitch.tv/group/user/%s/chatters", CHANNEL)));
            // get the viewers
            JSONArray viewers = json.getJSONObject("chatters").getJSONArray("viewers");
            for (int i = 0; i < viewers.length(); i++) {
                addPointToViewer(viewers.getString(i), ADD_PER_INT);
            }

            JSONArray mods = json.getJSONObject("chatters").getJSONArray("moderators");
            for (int i = 0; i < mods.length(); i++) {
                addPointToViewer(mods.getString(i), ADD_PER_INT);
            }

            pointMap.forEach((s, integer) -> {
                System.out.println(s + " --> " + integer);
            });
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Add @add Points to the given @viewer
     *
     * @param viewer
     * @param add
     */
    public synchronized void addPointToViewer(String viewer, int add) {
        int points = pointMap.getOrDefault(viewer, 0);

        pointMap.put(viewer, points + add);
    }

    /**
     * Get the current points for @viewer
     *
     * @param viewer
     * @return
     */
    public int getPointsForViewer(String viewer) {
        return pointMap.getOrDefault(viewer, 0);
    }
}
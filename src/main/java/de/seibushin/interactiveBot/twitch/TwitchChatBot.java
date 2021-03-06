package de.seibushin.interactiveBot.twitch;
/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

import de.seibushin.interactiveBot.lol.LolBot;
import de.seibushin.interactiveBot.oMeter.Words;
import de.seibushin.interactiveBot.helper.JSONParser;
import de.seibushin.interactiveBot.oMeter.OMeter;
import de.seibushin.interactiveBot.pointBot.PointBot;
import de.seibushin.interactiveBot.soundBot.SoundBot;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.json.JSONArray;
import org.json.JSONObject;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PingEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TwitchChatBot extends ListenerAdapter implements Runnable {
    private static TwitchChatBot instance;

    public static final String BOTNAME = "LoLPlayBot";
    // http://twitchapps.com/tmi/
    public static final String OAUTH = "oauth:qi4mmux6m2q9cxt0ch4fkggr9l1rc9";
    public static final String CHANNEL = "seibushin";

    public static PircBotX bot;

    private volatile BooleanProperty running = new SimpleBooleanProperty(false);

    //Mods usernames must be lowercase
    private List<String> mods = new ArrayList<>();

    public TwitchChatBot() {
        Configuration config = new Configuration.Builder()
                .setName(BOTNAME)
                .setServer("irc.chat.twitch.tv", 6667)
                .setServerPassword(OAUTH)
                .addListener(this)
                .addAutoJoinChannel("#" + CHANNEL)
                .buildConfiguration();

        bot = new PircBotX(config);

        mods.add("seibushin");
    }

    /**
     * Singleton
     *
     * @return
     */
    public synchronized static TwitchChatBot getInstance() {
        if (instance == null) {
            instance = new TwitchChatBot();
        }
        return instance;
    }

    public synchronized BooleanProperty isRunning() {
        return running;
    }

    /**
     * PircBotx will return the exact message sent and not the raw line
     */
    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        if (event.getMessage().startsWith("!")) {
            runCommand(event);
        } else {
            runAction(event);
        }
    }

    /**
     * perform the commands
     * @param event
     */
    private void runCommand(GenericMessageEvent event) {
        String[] parts = event.getMessage().split(" ");

        String user = event.getUser().getNick();

        switch (parts[0]) {
            case "!points":
                System.out.println("point");
                int points = PointBot.getInstance().getPointsForViewer(user);

                sendMessage(user + " you have " + points + " points!");
                break;
            case "!sound":
                if (SoundBot.getInstance().isRunning().get()) {
                    System.out.println("sound");

                    // add to queue
                    // remove
                    if (parts.length > 1) {
                        SoundBot.getInstance().addToPlayList(parts[1], user);
                    }
                }
                break;
            case "!speak":
                System.out.println("speak");
                if (parts.length > 1) {
                    SoundBot.getInstance().addSpeak(event.getMessage().replaceAll(".*? (.*)", "$1"), user);
                }
                break;
            case "!pick":
                System.out.println("pick");
                if (parts.length > 1) {
                    LolBot.getInstance().pick(parts[1]);
                }
                break;
            case "!raffle":
                System.out.println("raffle");
                if (mods.contains(event.getUser().getNick())) {
                    System.out.println("allowed");
                    try {
                        List<String> temp = new ArrayList<>();
                        JSONObject json = new JSONObject(JSONParser.readUrl(String.format("https://tmi.twitch.tv/group/user/%s/chatters", CHANNEL)));
                        JSONArray viewers = json.getJSONObject("chatters").getJSONArray("viewers");
                        for (int j = 0; j < viewers.length(); j++) {
                            temp.add(viewers.getString(j));
                        }
                        JSONArray mods = json.getJSONObject("chatters").getJSONArray("moderators");
                        for (int j = 0; j < mods.length(); j++) {
                            temp.add(mods.getString(j));
                        }

                        Random random = new Random();
                        sendMessage(String.format("Congratulations %s! You won the raffle!", temp.get(random.nextInt(temp.size()))));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case "!q":
                //@todo finish Keyboard robot
                /*
                System.out.println("Q");
                if (parts.length > 1) {
                    System.out.println(event.getMessage().replaceFirst(".*? ", ""));
                    LolRobot.getInstance().execute(parts[1]);
                }
                break;
                 */
        }
    }

    /**
     * Perform non command actions
     *
     * @param event
     */
    private void runAction(GenericMessageEvent event) {
        if (OMeter.getInstance().isRunning().get()) {
            System.out.println("OMeter check");
            String[] parts = event.getMessage().split(" ");

            int times = 0;
            for (String s : parts) {
                if (Words.neg.containsKey(s)) {
                    times--;
                } else if (Words.pos.containsKey(s)) {
                    times++;
                }
            }

            System.out.println("oMeter change: " + times);
            OMeter.getInstance().update(times);
        }
    }

    /**
     * We MUST respond to this or else we will get kicked
     */
    @Override
    public void onPing(PingEvent event) throws Exception {
        bot.sendRaw().rawLineNow(String.format("PONG %s\r\n", event.getPingValue()));
    }

    public void sendMessage(String message) {
        bot.sendIRC().message("#" + CHANNEL, message);
    }

    public synchronized void start() {
        if (!running.get()) {
            System.out.println("Start PointBot");
            running.set(true);

            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        try {
            System.out.println("start bot");
            bot.startBot();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void close() {
        if (running.get()) {
            System.out.println("Close PointBot");
            bot.stopBotReconnect();
            bot.sendIRC().quitServer("bye");
            running.set(false);
        }
    }
}
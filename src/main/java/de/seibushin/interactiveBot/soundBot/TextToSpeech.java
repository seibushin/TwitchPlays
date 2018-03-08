/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.soundBot;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.signalproc.effects.AudioEffect;
import marytts.signalproc.effects.AudioEffects;
import marytts.util.data.audio.AudioPlayer;

import javax.sound.sampled.AudioInputStream;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class TextToSpeech {
    private MaryInterface mary;

    AtomicBoolean speaking = new AtomicBoolean(false);

    public TextToSpeech() {
        try {
            mary = new LocalMaryInterface();
        } catch (MaryConfigurationException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TextToSpeech textToSpeech = new TextToSpeech();

        String text = "Hallo Seibushin. Sick gameplay. Lass ma rushen. Nice feed lol.";
        textToSpeech.say(text, "de");

        textToSpeech.say(text, "en");
    }

    public void say(String input, String voice) {
        try {
            while (!speaking.compareAndSet(false,true)) {
                // wait
            }
            AudioPlayer ap = new AudioPlayer();

            try {
                if ("de".equals(voice)) {
                    System.out.println("set voice german");
                    mary.setLocale(Locale.GERMAN);
                    mary.setVoice("bits1-hsmm");
                } else {
                    System.out.println("set voice english");
                    mary.setLocale(Locale.US);
                    mary.setVoice("cmu-slt-hsmm");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            /*
            System.out.println(mary.getAudioEffects());
            System.out.println(mary.getVoice());
            System.out.println(mary.getAvailableVoices());
            System.out.println(mary.getAvailableLocales());
            for (AudioEffect e : AudioEffects.getEffects()) {
                System.out.println(e.getName());
                System.out.println(e.getHelpText());
                System.out.println();
            }*/

            // Volumne [0.0, 10.0]
            mary.setAudioEffects("Volume(amount:0.20)");

            //TractScaler [0.25, 4.0]
            //Creates a shortened or lengthened vocal tract effect by shifting the formants.
            //mary.setAudioEffects("TractScaler(amount:0.25)");

            //F0Scale [0.0, 3.0]
            //mary.setAudioEffects("F0Scale(f0Scale:2.0)");

            //F0Add [-300.0, 300.0]
            //mary.setAudioEffects("F0Add(f0Add:2.0)");

            //Rate [0.1, 3.0]
            //mary.setAudioEffects("Rate(durScale:2.0)");

            //Robot [0.0, 100.0]
            //mary.setAudioEffects("Robot(amount:100.0)");

            //Whisper [0.0, 100.0]
            //mary.setAudioEffects("Whisper(amount:100.0)");

            //Stadium [0.0, 200.0]
            //mary.setAudioEffects("Stadium(amount:100.0)");

            //Chorus
            //mary.setAudioEffects("Chorus(delay1:466;amp1:0.54;delay2:600;amp2:-0.10;delay3:250;amp3:0.30)");

            //FIRFilter
            //mary.setAudioEffects("FIRFilter(type:3;fc1:500.0;fc2:2000.0)");

            //JetPilot
            //mary.setAudioEffects("Volume(amount:0.03)+JetPilot()");

            AudioInputStream audio = mary.generateAudio(input);

            ap.setAudio(audio);
            ap.start();

            ap.join();


            speaking.compareAndSet(true, false);

        } catch (Exception ex) {
            System.err.println("Error saying phrase.");
        }
    }
}
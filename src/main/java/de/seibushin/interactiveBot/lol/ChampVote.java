/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.lol;

public class ChampVote {
    private double vote;
    private String champ;

    public ChampVote(String champ, double vote) {
        this.champ = champ;
        this.vote = vote;
    }

    public double getVote() {
        return vote;
    }

    public void setChamp(String champ) {
        this.champ = champ;
    }

    public void setVote(double vote) {
        this.vote = vote;
    }

    public String getChamp() {
        return champ;
    }
}

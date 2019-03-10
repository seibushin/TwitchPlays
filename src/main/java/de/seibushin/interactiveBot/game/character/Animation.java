package de.seibushin.interactiveBot.game.character;

public class Animation {
    private int everyFrame;
    private int frames;
    private int rLeft;
    private int rRight;

    /**
     * Constructor for a new animation
     *
     * @param everyFrame every x frame the animation triggers
     * @param frames     how many frames the animation has
     * @param rRight     row of the right animation
     * @param rLeft      row of the left animation
     */
    public Animation(int everyFrame, int frames, int rRight, int rLeft) {
        this.everyFrame = everyFrame;
        this.frames = frames;
        this.rRight = rRight;
        this.rLeft = rLeft;
    }

    /**
     * Returns every x frame the animation triggers
     *
     * @return every x frame the animation triggers
     */
    public int getEveryFrame() {
        return everyFrame;
    }

    /**
     * Returns the amount of frames
     *
     * @return amount of frames
     */
    public int getFrames() {
        return frames;
    }
}

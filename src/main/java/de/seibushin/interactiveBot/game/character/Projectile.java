/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.game.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Projectile {
    private static final Image IMG = new Image("/game/projectile.png");

    protected Image sprite;

    protected int col = 0;
    protected int row = 0;

    protected int spriteWidth;
    protected int spriteHeight;

    protected int xpos;
    protected int ypos;

    protected int speed;
    protected  boolean left;
    protected boolean active = true;

    public Projectile(int speed, boolean left, int xpos, int ypos, int spriteWidth) {
        this.speed = speed;
        this.left = left;

        this.spriteWidth = 5;
        this.spriteHeight = 5;

        if (this.left) {
            this.xpos = xpos + spriteWidth;
            this.ypos = ypos;
        } else {
            this.xpos = xpos - this.spriteWidth;
            this.ypos = ypos;
            this.speed *= -1;
        }

        sprite = IMG;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return this.active;
    }

    public int getXpos() {
        return xpos;
    }

    public void draw(GraphicsContext gc) {
        if (active) {
            gc.drawImage(sprite,
                    col * spriteWidth,
                    row * spriteHeight,
                    spriteWidth,
                    spriteHeight,
                    xpos,
                    ypos,
                    spriteWidth,
                    spriteHeight);

            xpos += speed;
        }
    }
}

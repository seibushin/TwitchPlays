/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.game.character;

import javafx.scene.image.Image;

public class Venusaur extends Character {
    private static final Image IMG = new Image("/game/venusaur.png");

    public Venusaur(boolean left, double canvasWidth, double canvasHeight) {
        super(IMG, left);
        idle = new Animation(5, 3, 0, 1);
        walk = new Animation(5, 5, 2, 3);

        spriteWidth = 83;
        spriteHeight = 74;
        this.canvasWidth = (int) canvasWidth;
        this.canvasHeight = (int) canvasHeight;

        // baseStats
        speed = 1;

        if (left) {
            this.xpos = 0 - spriteWidth;
        } else {
            this.xpos = this.canvasWidth;
        }

        this.ypos = this.canvasHeight - spriteHeight;

        state = walk;
    }
}

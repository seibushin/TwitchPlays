/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.game.character;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

public class Character implements CharacterInterface {
    private static int START_POS = 200;

    private int frame = 0;
    protected Animation idle;
    protected Animation walk;
    protected Animation state;
    protected Image sprite;

    protected int col = 0;
    protected int row = 2; // 0

    protected int spriteWidth;
    protected int spriteHeight;
    protected String command = "";

    protected int moveCounter = 0;
    protected boolean useMove = false;
    private long counter = 0;

    private String mode = "walk";//"idle";

    private boolean left;

    protected int xpos;
    protected int ypos;

    protected int canvasWidth;

    protected List<Projectile> projectiles = new ArrayList<>();
    protected int canvasHeight;

    protected int speed;


    // stats
    /*
    hp = 0 - 1000
    atk = 0 - 100
    def = 0 - 100 -> 0% dmg reduction
    speed = 1.0 sec max
    block = 2.0 sec max
     */


    public Character(Image sprite, boolean left) {
        this.left = left;
        this.sprite = sprite;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    private void animate() {
        // check if we need to get the next frame
        if (frame >= state.getEveryFrame()) {
            col++;

            // if we did the hole animation reset it to the beginning
            if (col >= state.getFrames()) {
                col = 0;
            }

            // reset the frame to 0
            frame = 0;
        }
        // next frame
        frame++;
    }

    private void perform(GraphicsContext gc) {
        if (useMove && command.length() > moveCounter) {
            switch (command.charAt(moveCounter)) {
                case 'a':
                    projectiles.add(new Projectile(speed, left, xpos, ypos + (spriteHeight / 2), spriteWidth));
                    break;
                case 'b':
                    mode = "block";
                    break;
                case 'c':
                    break;
            }

            useMove = false;
            moveCounter++;
        }

        projectiles.forEach(projectile -> {
            projectile.draw(gc);
        });

        if (mode.equals("block")) {
            gc.drawImage(new Image("/game/block.png"),
                    0,
                    0,
                    50,
                    50,
                    xpos,
                    ypos,
                    50,
                    50);
        }
    }

    private int walkIn = 0;

    public void draw(GraphicsContext gc) {
        if (mode.equals("walk")) {
            if (walkIn < START_POS) {
                xpos += left ? 1 : -1;
                walkIn++;
            } else {
                mode = "idle";
                state = idle;
                row -= 2;
            }
        } else {
            if (counter >= 30) {
                useMove = true;
                counter = 0;

                if (!"idle".equals(mode)) {
                    mode = "idle";
                    state = idle;
                }
            }
        }

        perform(gc);
        animate();

        gc.drawImage(sprite,
                col * spriteWidth,
                row * spriteHeight,
                spriteWidth,
                spriteHeight,
                xpos,
                ypos,
                spriteWidth,
                spriteHeight);

        counter++;
    }

    @Override
    public void intersects(CharacterInterface char2) {
        char2.getProjectiles().forEach(projectile -> {
            if (projectile.isActive() && ((left && projectile.xpos < xpos + spriteWidth) || (!left && projectile.xpos + projectile.spriteWidth > xpos))) {
                // todo calc dmg
                // reduce hp
                // block? -> no dmg
                System.out.println("Collision " + System.currentTimeMillis());
                projectile.setActive(false);
            }
        });
    }

    public void changeDirection() {
        if (row % 2 == 0) {
            row += 1;
        } else {
            row -= 1;
        }
    }
}

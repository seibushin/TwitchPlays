/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.game.character;

import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public interface CharacterInterface {
    void setCommand(String command);
    void changeDirection();
    void draw(GraphicsContext gc);
    void intersects(CharacterInterface char2);
    List<Projectile> getProjectiles();
}

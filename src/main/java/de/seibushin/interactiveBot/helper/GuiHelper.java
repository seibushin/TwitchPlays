/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.helper;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GuiHelper {
    public static void addFade(Node node) {
        node.setOnMouseEntered(event -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
            fadeTransition.setToValue(1);
            fadeTransition.play();
        });

        node.setOnMouseExited(event -> {
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), node);
            fadeTransition.setToValue(0);
            fadeTransition.play();
        });
    }

    public static class DragStage {
        private double xOffset = 0;
        private double yOffset = 0;

        private Stage stage;

        public DragStage(Stage stage, Node node) {
            System.out.println("new DragStage");
            this.stage = stage;

            node.setOnMousePressed(event -> {
                xOffset = this.stage.getX() - event.getScreenX();
                yOffset = this.stage.getY() - event.getScreenY();
            });

            node.setOnMouseDragged(event -> {
                this.stage.setX(event.getScreenX() + xOffset);
                this.stage.setY(event.getScreenY() + yOffset);
            });
        }
    }
}

/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.keyPresser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class KeyCellController {
    private static final String FXML_PATH = "/fxml/keyCell.fxml";

    @FXML
    private HBox cell;
    @FXML
    private Label keyChar;
    @FXML
    private Label time;

    public KeyCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXML_PATH));
        // use this class as controller
        fxmlLoader.setController(this);
        try {
            Scene scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setInfo(Key key) {
        keyChar.setText(key.getKeychar());
        time.setText(key.getTime() + "");
    }

    public Node getCell() {
        return cell;
    }
}

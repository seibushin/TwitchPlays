/* Copyright 2018 Sebastian Meyer (seibushin.de)
 *
 * NO LICENSE
 * YOU MAY NOT REPRODUCE, DISTRIBUTE, OR CREATE DERIVATIVE WORKS FROM MY WORK
 *
 */

package de.seibushin.interactiveBot.apm;

import de.seibushin.interactiveBot.apm.model.Key;
import javafx.scene.control.ListCell;


public class KeyCell extends ListCell<Key> {

    private final KeyCellController keyCellController = new KeyCellController();

    @Override
    protected void updateItem(Key key, boolean empty) {
        super.updateItem(key, empty);

        if (empty) {
            setGraphic(null);
        } else {
            keyCellController.setInfo(key);
            setGraphic(keyCellController.getCell());
        }
    }
}

package sagalabsmanagerclient;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TableUtils {

    public static <T> void handleRightClickCopy(TableView<T> tableView) {
        tableView.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                T selectedItem = tableView.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    TableColumn<T, ?> selectedColumn = tableView.getFocusModel().getFocusedCell().getTableColumn();
                    Object value = selectedColumn.getCellData(selectedItem);
                    String stringValue = value == null ? "" : value.toString();

                    final Clipboard clipboard = Clipboard.getSystemClipboard();
                    final ClipboardContent content = new ClipboardContent();
                    content.putString(stringValue);
                    clipboard.setContent(content);
                }
            }
        });
    }

}

package sagalabsmanagerclient.controllers;

import javafx.beans.value.ObservableValue;

import java.sql.SQLException;

public interface Refreshable {
    public void refresh() throws SQLException;
}

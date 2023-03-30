package sagalabsmanagerclient;

import javafx.beans.value.ObservableValue;

import java.sql.SQLException;

public interface Refreshable {
    public void addRefreshThread();
    public void refresh() throws SQLException;
    public void stopRefreshing();
}

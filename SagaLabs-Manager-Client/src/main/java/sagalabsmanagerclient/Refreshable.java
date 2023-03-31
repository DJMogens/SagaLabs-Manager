package sagalabsmanagerclient;

import java.sql.SQLException;

public interface Refreshable {
    int milliSecondsBetweenRefresh = 10000;
    void initialize() throws SQLException;
    void addRefreshThread();
    void refresh() throws SQLException;
    void stopRefreshing();
}

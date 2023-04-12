package sagalabsmanagerclient;

import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Main extends javafx.application.Application {
    private static ViewSwitcher viewSwitcher;
    @Override
    public void start(Stage stage) throws IOException {
        viewSwitcher = new ViewSwitcher(stage);
    }

    public static void main(String[] args) {
        launch();
        Machines.stopRefreshing();
        viewSwitcher.closeThreads();
        try {
            Database.conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
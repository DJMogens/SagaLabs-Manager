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
        try {
            Database.conn.close();
            viewSwitcher.closeThreads();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
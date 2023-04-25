package sagalabsmanagerclient;

import javafx.stage.Stage;
import org.apache.commons.io.output.TeeOutputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;

public class Main extends javafx.application.Application {
    private static ViewSwitcher viewSwitcher;

    @Override
    public void start(Stage stage) throws IOException {
        viewSwitcher = new ViewSwitcher(stage);
    }

    public static void main(String[] args) {
        try {
            PrintStream logFile = new PrintStream(new FileOutputStream("./log.txt", true));
            System.setOut(new PrintStream(new TeeOutputStream(System.out, logFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        launch();
        Machines.stopRefreshing();
        viewSwitcher.closeThreads();
        try {
            Database.conn.close();
        } catch (SQLException e) {
            System.out.println("Error closing database connection: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

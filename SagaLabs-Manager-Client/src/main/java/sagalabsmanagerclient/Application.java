package sagalabsmanagerclient;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        //Creates the initial scene
        Scene scene = new Scene(new Pane());

        ViewSwitcher.setScene(scene);
        ViewSwitcher.switchView(View.LOGIN);
        Image icon = new Image(getClass().getResource("Images/FDCA_icon.png").openStream());
        stage.getIcons().add(icon);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("SagaLabs-Manager");
        stage.show();
    }

    public static void main(
            String[] args) {
        launch();
        try {
            Database.conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
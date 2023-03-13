package sagalabsmanagerclient;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewSwitcher {

    private static Scene scene;

    public static void setScene(Scene scene) {
        ViewSwitcher.scene = scene;
    }
    public static void switchView(View view) {
        Parent root = null;
        try {
            root = FXMLLoader.load(ViewSwitcher.class.getResource(view.getFileName()));
            scene.setRoot(root);

            Platform.runLater(() -> {
                // Check if the scene is attached to a stage
                if (scene.getWindow() instanceof Stage) {
                    Stage stage = (Stage) scene.getWindow();
                    stage.setAlwaysOnTop(true);
                    stage.requestFocus(); // Bring the window to front
                    stage.setAlwaysOnTop(false);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}

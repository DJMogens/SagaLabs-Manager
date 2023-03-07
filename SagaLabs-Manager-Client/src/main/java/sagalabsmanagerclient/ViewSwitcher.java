package sagalabsmanagerclient;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}

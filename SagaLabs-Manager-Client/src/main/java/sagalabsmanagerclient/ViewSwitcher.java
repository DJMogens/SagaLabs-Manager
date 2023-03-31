package sagalabsmanagerclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import sagalabsmanagerclient.controllers.Controller;

import java.io.IOException;

public class ViewSwitcher {

    private Controller controller;
    private Scene scene;

    public ViewSwitcher(Stage stage) throws IOException {
        //Creates the initial scene
        Scene scene = new Scene(new Pane());

        setScene(scene);
        switchView(View.LOGIN);

        Image icon = new Image(getClass().getResource("Images/FDCA_icon.png").openStream());
        stage.getIcons().add(icon);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setTitle("SagaLabs-Manager");
        stage.show();
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }
    public void switchView(View view) {
        try {
            if(controller != null) {
                controller.closeRefreshingThreads();
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(view.getFileName()));
            Parent root = loader.load();
            controller = loader.getController();
            controller.getViewSimpleObjectProperty().addListener(new ChangeListener<View>() {
                @Override
                public void changed(ObservableValue<? extends View> observable, View oldValue, View newValue) {
                    switchView(newValue);
                }
            });
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void closeThreads() {
        controller.closeRefreshingThreads();
    }
}
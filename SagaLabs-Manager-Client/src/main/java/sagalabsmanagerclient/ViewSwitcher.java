package sagalabsmanagerclient;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import sagalabsmanagerclient.controllers.CaptureLabController;
import sagalabsmanagerclient.controllers.Controller;

import java.io.IOException;
import java.util.Objects;

public class ViewSwitcher {

    private Controller controller;
    private Scene scene;

    public ViewSwitcher(Stage stage) throws IOException {
        //Creates the initial scene
        Scene scene = new Scene(new Pane());

        setScene(scene);
        switchView(View.LOGIN);

        Image icon = new Image(Objects.requireNonNull(getClass().getResource("Images/FDCA_icon.png")).openStream());
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
            controller.getViewSimpleObjectProperty().addListener((observable, oldValue, newValue) -> switchView(newValue));
            scene.setRoot(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void closeThreads() {
        controller.closeRefreshingThreads();
    }

    public static Stage openCaptureLabWindow(String labName) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewSwitcher.class.getResource(View.CAPTURE_LAB_WINDOW.getFileName()));
            Parent root = loader.load();

            // Pass the lab data to the CaptureLabController
            CaptureLabController captureLabController = loader.getController();
            captureLabController.setLabData(labName);

            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setTitle("Capture: " + labName);
            newStage.setScene(new Scene(root));
            newStage.show();

            return newStage;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
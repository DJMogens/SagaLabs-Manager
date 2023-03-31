package sagalabsmanagerclient.controllers;

import sagalabsmanagerclient.AzureLogin;
import sagalabsmanagerclient.Database;
import sagalabsmanagerclient.View;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.SQLException;


public class LoginController extends Controller {
    @FXML
    private static Button LoginButton;
    @FXML
    private Label welcomeText;

    @FXML
    protected void loginClick() throws SQLException {
        welcomeText.setText("You are being redirected to Azure for Login");
        System.out.println();
        if(AzureLogin.login()) {
            setView(View.HOME);
        }
    }
    @FXML
    public void changeScene() throws SQLException {
        System.out.println("CHANGING SCENE");
        Thread databaseThread = new Thread(() -> {
            try {
                Database.login();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        Thread switchView = new Thread(() -> {
            Platform.runLater(() -> {
                this.setView(View.HOME);
            });

        });
        databaseThread.start();
        switchView.start();
    }

    public static void changeButtonTryAgain() {
        Platform.runLater(new Runnable() {
            public void run() {
                LoginButton.setText("Try again");
            }
        });
    }
}
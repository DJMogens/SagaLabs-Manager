package sagalabsmanagerclient.controllers;

import sagalabsmanagerclient.AzureLogin;
import sagalabsmanagerclient.Database;
import sagalabsmanagerclient.View;
import sagalabsmanagerclient.ViewSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.SQLException;


public class LoginController {
    @FXML
    private static Button LoginButton;
    @FXML
    private Label welcomeText;

    @FXML
    protected void loginClick() {
        welcomeText.setText("You are being redirected to Azure for Login");
        AzureLogin.login();
    }
    @FXML
    public static void changeScene() throws SQLException {
        Thread databaseThread = new Thread(() -> {
            try {
                Database.login();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        Thread switchView = new Thread(() -> {
            ViewSwitcher.switchView(View.HOME);
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
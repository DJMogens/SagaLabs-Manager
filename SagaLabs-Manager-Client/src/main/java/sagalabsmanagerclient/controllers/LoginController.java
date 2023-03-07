package sagalabsmanagerclient.controllers;

import sagalabsmanagerclient.AzureLogin;
import sagalabsmanagerclient.View;
import sagalabsmanagerclient.ViewSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


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
    public static void changeScene() {
        ViewSwitcher.switchView(View.HOME);
    }

    public static void changeButtonTryAgain() {
        Platform.runLater(new Runnable() {
            public void run() {
                LoginButton.setText("Try again");
            }
        });
    }
}
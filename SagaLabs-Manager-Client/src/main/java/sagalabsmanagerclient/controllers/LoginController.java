package sagalabsmanagerclient.controllers;

import sagalabsmanagerclient.AzureLogin;
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
    protected void loginClick() {
        welcomeText.setText("You are being redirected to Azure for Login");
        System.out.println();
        if(AzureLogin.login()) {
            setView(View.HOME);
        }
    }

    public static void changeButtonTryAgain() {
        Platform.runLater(() -> LoginButton.setText("Try again"));
    }
}
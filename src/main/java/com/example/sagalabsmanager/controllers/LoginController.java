package com.example.sagalabsmanager.controllers;

import com.azure.resourcemanager.AzureResourceManager;
import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.View;
import com.example.sagalabsmanager.ViewSwitcher;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


public class LoginController {
    @FXML
    private static Button LoginButton;
    @FXML
    private Label welcomeText;

    public static AzureResourceManager azure;

    @FXML
    protected void loginClick() {
        welcomeText.setText("You are being redirected to Azure for Login");
        AzureLogin.startLogin();
    }
    @FXML
    public static void changeScene() {
        ViewSwitcher.switchView(View.LABS);
    }

    public static void changeButtonTryAgain() {
        Platform.runLater(new Runnable() {
            public void run() {
                LoginButton.setText("Try again");
            }
        });
    }
}
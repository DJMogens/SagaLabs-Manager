package com.example.sagalabsmanager.controllers;

import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.resourcemanager.AzureResourceManager;
import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.AzureMethods;
import com.example.sagalabsmanager.View;
import com.example.sagalabsmanager.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class LoginController {
    @FXML
    private Label welcomeText;

    public static AzureResourceManager azure;

    @FXML
    protected void onHelloButtonClick() {
        AzureMethods azMethods = new AzureMethods();
        AzureLogin azLogin = new AzureLogin();

        welcomeText.setText("You are being redirected to Azure for Login");
        Thread azureLoginThread = new Thread(() -> {
            System.out.println("Getting token credential and profile...");
            azLogin.login();//husk at logge ind
            System.out.println("Authenticating...");


            azure = AzureResourceManager.configure() //få denne class til at authenticate med tokencredential og profile fra AzureLogin classen
                    .withLogLevel(HttpLogDetailLevel.BASIC)
                    .authenticate(azLogin.tokenCredential, azLogin.profile)
                    .withSubscription(azLogin.subscriptionId);
            System.out.println(azure);

        });
        azureLoginThread.start();

    }

    @FXML
    protected void changeScene() {
        ViewSwitcher.switchView(View.LABS);
    }
}
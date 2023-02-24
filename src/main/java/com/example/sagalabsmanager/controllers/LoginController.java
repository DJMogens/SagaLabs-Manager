package com.example.sagalabsmanager.controllers;

import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.resourcemanager.AzureResourceManager;
import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.View;
import com.example.sagalabsmanager.ViewSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static java.lang.Thread.sleep;


public class LoginController {
    @FXML
    private Label welcomeText;

    public static AzureResourceManager azure;

    @FXML
    protected void onHelloButtonClick() throws InterruptedException {

        welcomeText.setText("You are being redirected to Azure for Login");
        Thread azureLoginThread = new Thread(() -> {
            System.out.println("Getting token credential and profile...");
            AzureLogin.login();//husk at logge ind
            System.out.println("Authenticating...");


            azure = AzureResourceManager.configure() //få denne class til at authenticate med tokencredential og profile fra AzureLogin classen
                    .withLogLevel(HttpLogDetailLevel.BASIC)
                    .authenticate(AzureLogin.tokenCredential, AzureLogin.profile)
                    .withSubscription(AzureLogin.subscriptionId);
            System.out.println(azure);

        });
        azureLoginThread.start();
        for (int i = 0; i < 120; i++) {
            boolean waitLogin = azureLoginThread.isAlive();
            System.out.println(AzureLogin.loginStatus);
            if (!waitLogin) {
                if (AzureLogin.loginStatus) {
                    System.out.println("Login successfull");

                    changeScene();
                    break;
                }
            }
            sleep(1000);
        }
    }
    @FXML
    protected void changeScene() {
        ViewSwitcher.switchView(View.LABS);
    }
}
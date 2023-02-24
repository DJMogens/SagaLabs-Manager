package com.example.sagalabsmanager.controllers;

import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.resourcemanager.AzureResourceManager;
import com.example.sagalabsmanager.AzureLogin;
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
        //
        //tilføj kode der omskriver login til try again knap
        //
        //Kontroller om login er opnået på 120 sekunder
        Thread checkLogin = new Thread(() -> {
            long startTime = System.currentTimeMillis();
            long duration = 0;
            while (duration < 120_000 && azureLoginThread.isAlive()) {
                System.out.println(AzureLogin.loginStatus);
                if (AzureLogin.loginStatus) {
                    //skal printes til bruger i vindue
                    System.out.println("Login successful");
                    changeScene();
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                duration = System.currentTimeMillis() - startTime;
            }
            if (!AzureLogin.loginStatus){
                //skal printes til bruger i vindue
                System.out.println("Login not succeded. Try again");
            }
        });

        azureLoginThread.start();
        checkLogin.start();
    }
    @FXML
    protected void changeScene() {
        ViewSwitcher.switchView(View.LABS);
    }
}
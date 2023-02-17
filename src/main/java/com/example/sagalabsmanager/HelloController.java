package com.example.sagalabsmanager;

import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.resourcemanager.AzureResourceManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Scanner;


public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        AzureMethods azMethods = new AzureMethods();

        welcomeText.setText("You are being redirected to Azure for Login");

        System.out.println("Getting token credential and profile...");
        AzureLogin azureLogin = new AzureLogin();
        azureLogin.login();//husk at logge ind

        System.out.println("Authenticating...");

        AzureResourceManager azure = AzureResourceManager.configure() //få denne class til at authenticate med tokencredential og profile fra AzureLogin classen
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(azureLogin.tokenCredential, azureLogin.profile)
                .withSubscription(azureLogin.subscriptionId);
        System.out.println(azure);

        azMethods.listAllResourceGroups(azure);



        azMethods.getLabDetails(azure);
    }
}
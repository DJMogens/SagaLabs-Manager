package com.example.sagalabsmanager;

import com.azure.resourcemanager.AzureResourceManager;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("You are being redirected to Azure for Login");
        try {
            AzureAuthenticator authenticator = new AzureAuthenticator();
            IAuthenticationResult authResult = AzureAuthenticator.authenticate();
            // Use the authResult to make requests to Azure APIs
        } catch (Exception ex) {
            // Handle any exceptions that occur during authentication
            System.out.println(ex);
        }
        AzureResourceManager manager = null;
        try {
            ResourceManager listLab = new ResourceManager();
            ResourceManager.listLabs(manager);
            // Use the authResult to make requests to Azure APIs
        } catch (Exception ex) {
            // Handle any exceptions that occur during authentication
            System.out.println(ex);
        }
        ;
        System.out.println("her1");

        return;
    }
}
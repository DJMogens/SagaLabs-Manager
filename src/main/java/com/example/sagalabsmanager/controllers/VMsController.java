package com.example.sagalabsmanager.controllers;

import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.AzureMethods;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

public class VMsController extends MenuController {

    @FXML public Tab Lab1;
    @FXML public Tab Lab2;

    @FXML
    public void initialize() {
        ResourceGroup[] allLabs = AzureMethods.getAllLabs(AzureLogin.azure);
        Lab1.setText(allLabs[0].name().substring(0,10));
        Lab2.setText(allLabs[1].name().substring(0,10));
    }
}

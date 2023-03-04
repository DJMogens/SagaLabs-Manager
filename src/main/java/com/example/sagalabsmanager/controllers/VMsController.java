package com.example.sagalabsmanager.controllers;

import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.AzureMethods;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import java.util.ArrayList;

public class VMsController extends MenuController {

    @FXML public Tab Lab1;
    @FXML public Tab Lab2;

    @FXML
    public void initialize() {
        ArrayList<ResourceGroup> allLabs = AzureMethods.getAllLabs(AzureLogin.azure);
        for(ResourceGroup lab: allLabs) {
            Lab1.setText(lab.name().substring(0, 10));
            Lab2.setText(lab.name().substring(0, 10));
        }
    }
}

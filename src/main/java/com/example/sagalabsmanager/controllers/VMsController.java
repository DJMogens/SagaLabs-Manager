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

    public void initialize() {
        initializeTabNames();
    }
    private void initializeTabNames() {
        ArrayList<ResourceGroup> allLabs = AzureMethods.getAllLabs(AzureLogin.azure);
        Tab[] tabs = {Lab1, Lab2};
        for(int i = 0; i < tabs.length; i++) {
            tabs[i].setText(allLabs.get(i).name().substring(0, 10)); // Sets text of tabs to first 10 characters of lab names
        }
    }
}

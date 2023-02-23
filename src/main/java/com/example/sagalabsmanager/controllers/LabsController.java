package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.AzureMethods;

public class LabsController {

    public void listResourceGroups() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listAllResourceGroups(LoginController.azure);
    }
    public void getLabDetails() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.getLabDetails(LoginController.azure);
    }
}

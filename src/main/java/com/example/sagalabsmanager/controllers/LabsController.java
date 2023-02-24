package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.AzureMethods;

public class LabsController {

    public void listResourceGroupsWithLabTag() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listResourceGroupsWithLabTag(LoginController.azure);
    }
    public void getLabDetails() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listResourceGroupsWithLabTag(LoginController.azure);
    }
}

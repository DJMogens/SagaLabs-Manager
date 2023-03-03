package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.AzureMethods;

public class LabsController extends MenuController {

    public void listResourceGroupsWithLabTag() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listResourceGroupsWithLabTag(AzureLogin.azure);
    }
    public void getLabDetails() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listResourceGroupsWithLabTag(AzureLogin.azure);
    }
}


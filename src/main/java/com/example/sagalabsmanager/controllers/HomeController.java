package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.AzureMethods;

public class HomeController extends MenuController {
    public void listResourceGroupsWithLabTag() {
        AzureMethods.listResourceGroupsWithLabTag(AzureLogin.azure);
    }
    public void getLabDetails() {
        AzureMethods.listResourceGroupsWithLabTag(AzureLogin.azure);
    }
}


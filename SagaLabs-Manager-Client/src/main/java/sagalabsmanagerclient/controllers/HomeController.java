package sagalabsmanagerclient.controllers;

import sagalabsmanagerclient.AzureLogin;
import sagalabsmanagerclient.AzureMethods;

public class HomeController extends MenuController {
    public void listResourceGroupsWithLabTag() {
        AzureMethods.listResourceGroupsWithLabTag(AzureLogin.azure);
    }
    public void getLabDetails() {
        AzureMethods.listResourceGroupsWithLabTag(AzureLogin.azure);
    }
}


package com.example.sagalabsmanager;

import com.azure.resourcemanager.AzureResourceManager;

public class AzureMethods {

    public void listAllResourceGroups(AzureResourceManager azure) {
        System.out.println("Listing resource groups...");
        for (com.azure.resourcemanager.resources.models.ResourceGroup resourceGroup : azure.resourceGroups().list()) {//printer alle resource groups
            System.out.printf("Resource group name: %s, location: %s%n",
                    resourceGroup.name(), resourceGroup.regionName());
        }
    }

}

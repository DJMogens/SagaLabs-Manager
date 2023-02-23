package com.example.sagalabsmanager;

import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.models.ResourceGroup;

import java.util.Scanner;

public class AzureMethods {

    public void listAllResourceGroups(AzureResourceManager azure) {
        System.out.println("Listing resource groups...");
        for (com.azure.resourcemanager.resources.models.ResourceGroup resourceGroup : azure.resourceGroups().list()) {//printer alle resource groups
            System.out.printf("Resource group name: %s, location: %s%n",
                    resourceGroup.name(), resourceGroup.regionName());
        }
    }

    public void getLabDetails(AzureResourceManager azure) {
        System.out.println("choose resource group to get details: ");
        Scanner input = new Scanner(System.in);
        String rgName = input.next();

        ResourceGroup resourceGroup = azure.resourceGroups().getByName(rgName);
        System.out.println("Name:    " + resourceGroup.name());
        System.out.println("Id:      " + resourceGroup.id());
        System.out.println("Type:    " + resourceGroup.type());
        System.out.println("State:   " + resourceGroup.provisioningState());
        System.out.println("Region:  " + resourceGroup.regionName());
    }

    public void listVMProperties(AzureResourceManager azure) {
        System.out.println("choose resource group: ");
        Scanner input = new Scanner(System.in);
        String rgName = input.next();

    }

}

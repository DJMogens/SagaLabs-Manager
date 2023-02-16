package com.example.sagalabsmanager;

// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.models.ResourceGroup;

/**
 * Azure Resource sample for managing resource groups -
 * - List resource groups
 */

public final class ResourceManager {
    /**
     * Main function which runs the actual sample.
     *
     * @param azureResourceManager instance of the azure client
     * @return true if sample runs successfully
     */
    public static boolean listLabs(AzureResourceManager azureResourceManager) {
        final String rgName = Utils.randomResourceName(azureResourceManager, "rgRSMA", 24);
        final String rgName2 = Utils.randomResourceName(azureResourceManager, "rgRSMA", 24);
        final String resourceTagName = Utils.randomResourceName(azureResourceManager, "rgRSTN", 24);
        final String resourceTagValue = Utils.randomResourceName(azureResourceManager, "rgRSTV", 24);
        try {

            //=============================================================
            // List resource groups.

            System.out.println("Listing all resource groups");

            //choose subscription


            for (ResourceGroup rGroup : azureResourceManager.resourceGroups().list()) {
                System.out.println("Resource group: " + rGroup.name());
            }

        } finally {

            try {
                System.out.println("RG name: " + rgName);
            } catch (Exception g) {
                g.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Main entry point.
     *
     * @param args the parameters
     */
    public static void main(String[] args) {
        try {
            //=================================================================
            // Authenticate

            final AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
            final TokenCredential credential = new DefaultAzureCredentialBuilder()
                    .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                    .build();

            AzureResourceManager azureResourceManager = AzureResourceManager
                    .configure()
                    .withLogLevel(HttpLogDetailLevel.BASIC)
                    .authenticate(credential, profile)
                    .withSubscription("06d0a3df-f3c0-4336-927d-db8891937870"); //Variable for subscription. This should be configurable for the end user at some point

            listLabs(azureResourceManager);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}

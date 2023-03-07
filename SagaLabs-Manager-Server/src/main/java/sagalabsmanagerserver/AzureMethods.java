package sagalabsmanagerserver;
import com.azure.core.http.rest.PagedIterable;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class AzureMethods {

    public static List<String[]> labResourceGroups = new ArrayList<>(); //string array with names and ids of the resource groups that are labs (base on tag in azure)

    /*DEPRECATED, new is listResourceGroupsWithLabTag
    public void listAllResourceGroups(AzureResourceManager azure) {
        System.out.println("Listing resource groups...");
        for (com.azure.resourcemanager.resources.models.ResourceGroup resourceGroup : azure.resourceGroups().list()) {//printer alle resource groups
            System.out.printf("Resource group name: %s, location: %s%n",
                    resourceGroup.name(), resourceGroup.regionName());
        }
    }
    */

    public static void listResourceGroupsWithLabTag(AzureResourceManager azure) {
        System.out.println("Listing resource groups with the tag 'lab:true'...");
        for (ResourceGroup resourceGroup : azure.resourceGroups().list()) {
            if (resourceGroup.tags() != null && resourceGroup.tags().containsKey("lab") && resourceGroup.tags().get("lab").equalsIgnoreCase("true")) {
                String[] labResourceGroup = new String[]{resourceGroup.name(), resourceGroup.id()};
                labResourceGroups.add(labResourceGroup);
                System.out.printf("Lab (resource group) name: %s, id: %s%n", resourceGroup.name(), resourceGroup.id());
            }
        }
    }

    public static ArrayList<ResourceGroup> getAllLabs(AzureResourceManager azure) {
        ArrayList<ResourceGroup> resourceGroups = new ArrayList<ResourceGroup>();
        System.out.println("Listing resource groups with the tag 'lab:true'...");
        for (ResourceGroup resourceGroup : azure.resourceGroups().list()) {
            if (resourceGroup.tags() != null && resourceGroup.tags().containsKey("lab") && resourceGroup.tags().get("lab").equalsIgnoreCase("true")) {
                String[] labResourceGroup = new String[]{resourceGroup.name(), resourceGroup.id()};
                labResourceGroups.add(labResourceGroup);
                System.out.printf("Lab (resource group) name: %s, id: %s%n", resourceGroup.name(), resourceGroup.id());
                resourceGroups.add(resourceGroup);
            }
        }
        return resourceGroups;
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

    public static String getKeyVaultSecret(String secretName){
        // Get the password from Azure Key Vault Secret
        String keyVaultName = "sagalabskeyvault";
        String keyVaultUrl = "https://" + keyVaultName + ".vault.azure.net";
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(AzureLogin.tokenCredential)
                .buildClient();
        KeyVaultSecret secret = secretClient.getSecret(secretName);
        return secret.getValue();
    }


    public void listVMProperties(AzureResourceManager azure) {
        System.out.println("choose resource group: ");
        Scanner input = new Scanner(System.in);
        String rgName = input.next();

    }

    public static PagedIterable<VirtualMachine> getVMsInLab(ResourceGroup resourceGroup) {
        PagedIterable<VirtualMachine> vms = AzureLogin.azure.virtualMachines().listByResourceGroup(String.valueOf(resourceGroup.name()));
        return vms;
    }

}

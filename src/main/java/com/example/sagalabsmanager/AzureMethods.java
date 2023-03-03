package com.example.sagalabsmanager;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.models.ResourceGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class AzureMethods {

    public static List<String[]> labResourceGroups = new ArrayList<>(); //string array with names and ids of the resource groups that are labs (base on tag in azure)


    public void listAllResourceGroups(AzureResourceManager azure) {
        System.out.println("Listing resource groups...");
        for (com.azure.resourcemanager.resources.models.ResourceGroup resourceGroup : azure.resourceGroups().list()) {//printer alle resource groups
            System.out.printf("Resource group name: %s, location: %s%n",
                    resourceGroup.name(), resourceGroup.regionName());
        }
    }



    public static void listResourceGroupsWithLabTag(AzureResourceManager azure) {
        System.out.println("Listing resource groups with the tag 'lab:true'...");
        for (ResourceGroup resourceGroup : azure.resourceGroups().list()) {
            if (resourceGroup.tags() != null && resourceGroup.tags().containsKey("lab") && resourceGroup.tags().get("lab").equalsIgnoreCase("true")) {
                String[] labResourceGroup = new String[]{resourceGroup.name(), resourceGroup.id()};
                labResourceGroups.add(labResourceGroup);
                System.out.printf("Lab (resource group) name: %s, id: %s%n", resourceGroup.name(), resourceGroup.id());
            }
        }
        insertLabResourceGroupsIntoMySQL();
    }

    private static void insertLabResourceGroupsIntoMySQL() {
        // MySQL database connection details
        String url = "jdbc:mysql://localhost:3306/lab";
        String username = "root";
        String password = "IOU!H¤9pijoqwe890u19!=)";// very insecure to store in plaintext

        // SQL statement to insert data into the lab table
        String sql = "INSERT INTO lab (lab_navn, id) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Iterate through the lab resource groups list and insert each string and ID into the lab table
            for (String[] labResourceGroup : labResourceGroups) {
                pstmt.setString(1, labResourceGroup[0]);
                pstmt.setString(2, labResourceGroup[1]);
                pstmt.executeUpdate();
            }
            System.out.println("Lab resource groups inserted into MySQL database successfully!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
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

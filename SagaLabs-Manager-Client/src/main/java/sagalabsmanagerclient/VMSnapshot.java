package sagalabsmanagerclient;

import com.azure.core.management.Region;
import com.azure.core.util.Context;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.fluent.models.GalleryImageVersionInner;
import com.azure.resourcemanager.compute.models.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VMSnapshot extends AzureMethods {
    // Get the gallery resource ID from the Azure Key Vault secret
    static String vmImageGalleryResourceGroup = "SL-vmImages";
    static String vmImageGalleryNameDefault = "SagalabsVM";

    public static void takeSnapshots(ArrayList<MachinesVM> vms, String version, String galleryName) {
        // Get the Azure resource manager instance
        AzureResourceManager azure = AzureLogin.getAzure();

        // Loop over all VMs in the list
        for (MachinesVM vm : vms) {
            VirtualMachine azureVm = azure.virtualMachines().getById(vm.getAzureId());

            // Retrieve the VM instance view
            if (azureVm != null) {
                azureVm.refreshInstanceView();
                List<InstanceViewStatus> statuses = azureVm.instanceView().statuses();

                // Check if the VM is generalized
                boolean isSpecialized = false;
                for (InstanceViewStatus status : statuses) {
                    System.out.println("VM Name: " + vm.getVmName() + ", Status: " + status.code());
                    if (status.code().equalsIgnoreCase("OSState/specialized")) {
                        isSpecialized = true;
                        break;
                    }
                }
                if (isSpecialized){
                    takeSpecializedSnapshot(vm.getResourceGroup(), vm.getVmName(), version, vmImageGalleryResourceGroup, galleryName);}
            }
        }
    }




    private static String incrementVersion(String version) {
        Pattern pattern = Pattern.compile("(\\d+)\\.(\\d+)\\.(\\d+)");
        Matcher matcher = pattern.matcher(version);

        if (matcher.matches()) {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));

            patch++;

            return major + "." + minor + "." + patch;
        }

        return "0.0.1";
    }
    public static void takeSpecializedSnapshot(String resourceGroupName, String vmName, String version, String galleryResourceGroup, String galleryName) {
        // Get the Azure resource manager instance
        AzureResourceManager azure = AzureLogin.getAzure();

        // Find the VM
        VirtualMachine vm = azure.virtualMachines().getByResourceGroup(resourceGroupName, vmName);

        if (vm == null) {
            System.out.println("VM not found");
            return;
        }

        // Get or create the image gallery
        Gallery gallery = getOrCreateImageGallery(azure, galleryResourceGroup, galleryName);

        if (gallery == null) {
            System.out.println("Gallery not found and couldn't be created");
            return;
        }

        // Create a new gallery image version using the VM as the source
        GalleryImageVersionInner newImageVersion = azure.virtualMachines().manager().serviceClient().getGalleryImageVersions()
                .createOrUpdate(
                        galleryResourceGroup,
                        galleryName,
                        vmName,
                        version,
                        new GalleryImageVersionInner()
                                .withLocation(vm.region().toString())
                                .withStorageProfile(
                                        new GalleryImageVersionStorageProfile()
                                                .withSource(
                                                        new GalleryArtifactVersionFullSource()
                                                                .withId(vm.id())))
                                .withPublishingProfile(
                                        new GalleryImageVersionPublishingProfile()
                                                .withTargetRegions(Collections.singletonList(
                                                        new TargetRegion()
                                                                .withName(vm.region().toString())
                                                                .withRegionalReplicaCount(1)
                                                                .withExcludeFromLatest(false)))
                                ), Context.NONE);

        System.out.println("Image version created: " + vmName + " - " + newImageVersion.name());
    }





    private static Gallery getOrCreateImageGallery(AzureResourceManager azure, String galleryResourceGroup, String galleryName) {
        Gallery gallery = null;
        try {
            gallery = azure.galleries().getByResourceGroup(galleryResourceGroup, galleryName);
        } catch (Exception e) {
            System.out.println("Error while trying to get image gallery: " + e.getMessage());
        }
        if (gallery == null) {
            try {
                gallery = azure.galleries().define(galleryName)
                        .withRegion(Region.EUROPE_WEST) // Replace with the desired region
                        .withExistingResourceGroup(galleryResourceGroup)
                        .create();
            } catch (Exception e) {
                System.out.println("Error while trying to create image gallery: " + e.getMessage());
            }
        }
        return gallery;
    }




}

package sagalabsmanagerclient;

import com.azure.core.management.Region;
import com.azure.core.util.Context;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.fluent.models.GalleryImageInner;
import com.azure.resourcemanager.compute.fluent.models.GalleryImageVersionInner;
import com.azure.resourcemanager.compute.models.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VMSnapshot extends AzureUtils {
    // Get the gallery resource ID from the Azure Key Vault secret
    static String vmImageGalleryResourceGroup = "SL-vmImages";
    static String vmImageGalleryNameDefault = "SagalabsVM";

    public static void takeSnapshots(ArrayList<MachinesVM> vms, String version, String galleryName, Runnable onStartCallback) {
        // Get the Azure resource manager instance
        AzureResourceManager azure = AzureLogin.getAzure();

        // Create a thread pool with a fixed number of threads
        int numThreads = Math.min(10, vms.size()); // Limit the number of threads to a reasonable value
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

        // Loop over all VMs in the list
        List<Future<Void>> futures = new ArrayList<>();
        AtomicInteger machinesStarted = new AtomicInteger(0);
        int totalMachines = vms.size();

        for (MachinesVM vm : vms) {
            // Submit a task for each VM to be processed concurrently
            Future<Void> future = executorService.submit(() -> {
                VirtualMachine azureVm = azure.virtualMachines().getById(vm.getAzureId());

                // Retrieve the VM instance view
                if (azureVm != null) {
                    azureVm.refreshInstanceView();
                    List<InstanceViewStatus> statuses = azureVm.instanceView().statuses();

                        takeSpecializedSnapshot(vm.getResourceGroup(), vm.getVmName(), version, vmImageGalleryResourceGroup, galleryName, totalMachines, machinesStarted, onStartCallback);

                }



                return null;
            });
            futures.add(future);
        }

        // Wait for all tasks to complete
        boolean onStartCallbackTriggered = false;
        for (Future<Void> future : futures) {
            try {
                future.get();
                if (!onStartCallbackTriggered && machinesStarted.get() == totalMachines) {
                    onStartCallback.run();
                    onStartCallbackTriggered = true;
                }
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Error processing VM: " + e.getMessage());
            }
        }

        // Shutdown the executor service
        executorService.shutdown();
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
    public static boolean takeSpecializedSnapshot(String resourceGroupName, String vmName, String version, String galleryResourceGroup, String galleryName, int totalMachines, AtomicInteger machinesStarted, Runnable onStartCallback) {
        // Get the Azure resource manager instance
        AzureResourceManager azure = AzureLogin.getAzure();

        // Find the VM
        VirtualMachine vm = azure.virtualMachines().getByResourceGroup(resourceGroupName, vmName);

        if (vm == null) {
            System.out.println("VM not found");
            return false;
        }

        // Get or create the image gallery
        Gallery gallery = getOrCreateImageGallery(azure, galleryResourceGroup, galleryName);

        if (gallery == null) {
            System.out.println("Gallery not found and couldn't be created");
            return false;
        }

        // Create or update the gallery image
        GalleryImageInner galleryImageInner = createOrUpdateGalleryImage(azure, galleryResourceGroup, galleryName, vmName, vm, version);

        if (galleryImageInner == null) {
            System.out.println("Gallery image not found and couldn't be created");
            return false;
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
                                                                .withExcludeFromLatest(false)))), Context.NONE);

        System.out.println("Image version created: " + vmName + " - " + newImageVersion.name());
        return true;
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
    public static GalleryImageInner createOrUpdateGalleryImage(AzureResourceManager azure, String galleryResourceGroup, String galleryName, String galleryImageName, VirtualMachine vm, String version) {
        // Get the operating system, region, Hyper-V generation, and state from the VirtualMachine object
        OperatingSystemTypes osType = vm.osType();
        String location = vm.region().toString();
        String hyperVGeneration = vm.instanceView().innerModel().hyperVGeneration().toString();
        OperatingSystemStateTypes osState = OperatingSystemStateTypes.SPECIALIZED;

        // Create or update the gallery image
        GalleryImageInner galleryImageInner = azure.virtualMachines().manager().serviceClient().getGalleryImages()
                .createOrUpdate(
                        galleryResourceGroup,
                        galleryName,
                        galleryImageName,
                        new GalleryImageInner()
                                .withLocation(location)
                                .withOsType(osType)
                                .withOsState(osState)
                                .withHyperVGeneration(HyperVGeneration.fromString(hyperVGeneration))
                                .withPurchasePlan(
                                        vm.plan() != null ? // Check if the plan is null
                                                new ImagePurchasePlan()
                                                        .withName(vm.plan().name())
                                                        .withProduct(vm.plan().product())
                                                        .withPublisher(vm.plan().publisher())
                                                : null) // If the plan is null, set the value to null
                                .withIdentifier(
                                        new GalleryImageIdentifier()
                                                .withPublisher("SagaLabs") // Replace with your publisher name
                                                .withOffer("SagaLabs") // Replace with your offer name
                                                .withSku("SagaLabs-" + vm.name())), // Replace with your SKU name
                        Context.NONE);

        return galleryImageInner;
    }





}

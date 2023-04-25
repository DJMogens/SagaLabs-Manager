package sagalabsmanagerclient;
import com.azure.core.http.rest.PagedIterable;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.PowerState;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class AzureUtils {

    private void getLabDetails(AzureResourceManager azure) {
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

    public String getKeyVaultSecret(String secretName) {
        // Get the password from Azure Key Vault Secret
        String keyVaultName = "sagalabskeyvault";
        String keyVaultUrl = "https://" + keyVaultName + ".vault.azure.net";
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(AzureLogin.getTokenCredentialKeyVault())
                .buildClient();
        KeyVaultSecret secret = secretClient.getSecret(secretName);
        return secret.getValue();
    }


    public void listVMProperties(AzureResourceManager azure) {
        System.out.println("choose resource group: ");
        Scanner input = new Scanner(System.in);
        String rgName = input.next();

    }

    public PagedIterable<VirtualMachine> getVMsInLab(ResourceGroup resourceGroup) {
        return AzureLogin.getAzure().virtualMachines().listByResourceGroup(String.valueOf(resourceGroup.name()));
    }

    public String turnOnInLab(String resourceGroup) {
        try {
            // Get all the virtual machines in the resource group
            List<VirtualMachine> vms = AzureLogin.getAzure().virtualMachines().listByResourceGroup(resourceGroup).stream().toList();

            // Create a thread pool with one thread for each virtual machine
            ExecutorService executorService = Executors.newFixedThreadPool(vms.size());

            // Start all the virtual machines in parallel
            List<CompletableFuture<Void>> futures = vms.stream()
                    .filter(vm -> vm.powerState().equals(PowerState.DEALLOCATED))
                    .map(vm -> CompletableFuture.runAsync(vm::start, executorService))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Shutdown the thread pool
            executorService.shutdown();

            // Return success message
            return "All virtual machines in resource group " + resourceGroup + " have been turned on.";

        } catch (Exception e) {
            // Return error message
            return "An error occurred while turning on virtual machines in resource group " + resourceGroup + ": " + e.getMessage();
        }
    }

    public String turnOffVMsInLab(String resourceGroup) {
        try {
            // Get all the virtual machines in the resource group
            List<VirtualMachine> vms = AzureLogin.getAzure().virtualMachines().listByResourceGroup(resourceGroup).stream().toList();

            // Create a thread pool with one thread for each virtual machine
            ExecutorService executorService = Executors.newFixedThreadPool(vms.size());

            // Deallocate all the virtual machines in parallel
            List<CompletableFuture<Void>> futures = vms.stream()
                    .filter(vm -> vm.powerState().equals(PowerState.RUNNING))
                    .map(vm -> CompletableFuture.runAsync(vm::deallocate, executorService))
                    .toList();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Shutdown the thread pool
            executorService.shutdown();

            // Return success message
            return "All virtual machines in resource group " + resourceGroup + " have been deallocated.";

        } catch (Exception e) {
            // Return error message
            return "An error occurred while deallocating virtual machines in resource group " + resourceGroup + ": " + e.getMessage();
        }
    }


    public void turnOnVMs(ArrayList<MachinesVM> vms) {
        ExecutorService executor = Executors.newFixedThreadPool(vms.size());
        vms.stream()
                .filter(vm -> vm.getState().equals("deallocated"))
                .forEach(vm -> executor.submit(() -> {
                    VirtualMachine virtualMachine = AzureLogin.getAzure().virtualMachines().getById(vm.getAzureId());
                    virtualMachine.start();
                }));
        executor.shutdown();
    }

    public void deallocateVMs(ArrayList<MachinesVM> vms) {
        ExecutorService executor = Executors.newFixedThreadPool(vms.size());
        vms.stream()
                .filter(vm -> vm.getState().equals("running"))
                .forEach(vm -> executor.submit(() -> {
                    VirtualMachine virtualMachine = AzureLogin.getAzure().virtualMachines().getById(vm.getAzureId());
                    virtualMachine.deallocate();
                }));
        executor.shutdown();
    }

    public String runScript(ArrayList<MachinesVM> selectedVMs, String script) {

    ArrayList<String> outputOfRunScript = RunCommand.runScriptOnVms(selectedVMs, script);

    StringBuilder outputStringBuilder = new StringBuilder();
        assert outputOfRunScript != null;
        for (String output : outputOfRunScript) {
        outputStringBuilder.append(output).append(System.lineSeparator());
    }
        return outputStringBuilder.toString();
    }
}


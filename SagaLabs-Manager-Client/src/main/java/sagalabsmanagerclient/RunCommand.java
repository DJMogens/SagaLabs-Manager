package sagalabsmanagerclient;


import com.azure.resourcemanager.compute.models.RunCommandInput;
import com.azure.resourcemanager.compute.models.RunCommandResult;
import com.azure.resourcemanager.compute.models.VirtualMachine;

import java.util.ArrayList;
import java.util.Arrays;


public class RunCommand extends AzureMethods {
    public static String runCommandOnVM(MachinesVM vm, String script) {
        VirtualMachine virtualMachine = AzureLogin.getAzure().virtualMachines().getById(vm.getAzureId());
        String osType = virtualMachine.osType().toString();

        RunCommandInput runCommandInput;
        //check if it is windows or linux
        if ("Windows".equalsIgnoreCase(osType)) {
            runCommandInput = new RunCommandInput().withCommandId("RunPowerShellScript").withScript(Arrays.asList(script));
        } else {
            runCommandInput = new RunCommandInput().withCommandId("RunShellScript").withScript(Arrays.asList(script));
        }

        RunCommandResult runCommandResult = virtualMachine.runCommand(runCommandInput);

        System.out.println(runCommandResult.value().get(0).message());

        // Set output into the vm object
        vm.setLastScriptOutput(runCommandResult.value().get(0).message());
        return runCommandResult.value().get(0).message();
    }



        //The users shouldn't run code meant for bash on both linux and windows machines, and the other way around
        public static boolean checkOSIsTheSame(ArrayList<MachinesVM> vms) {
            if (vms.size() > 0) {
                VirtualMachine firstVM = AzureLogin.getAzure().virtualMachines().getById(vms.get(0).getAzureId());
                String firstOS = firstVM.osType().toString();
                for (int i = 1; i < vms.size(); i++) {
                    VirtualMachine currentVM = AzureLogin.getAzure().virtualMachines().getById(vms.get(i).getAzureId());
                    String currentOS = currentVM.osType().toString();
                    if (!currentOS.equals(firstOS)) {
                        return false;
                    }
                }
            }
            return true;
        }

    public static ArrayList<String> runScriptOnVms(ArrayList<MachinesVM> vms, String script) {
        ArrayList<String> scriptOutputs = new ArrayList<>();
        ArrayList<Thread> threads = new ArrayList<>();
        if (!checkOSIsTheSame(vms)) {
            // Return an error or throw an exception, as the VMs have different OSes.
            return null;
        }
        for (MachinesVM vm : vms) {
            Runnable runnable = () -> {
                String output = runCommandOnVM(vm, script);
                synchronized (scriptOutputs) {
                    scriptOutputs.add("output from: " + vm.getVmName() + "\n" + output);
                }
            };
            Thread thread = new Thread(runnable);
            thread.start();
            threads.add(thread);
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                // Handle the InterruptedException.
                e.printStackTrace();
            }
        }
        return scriptOutputs;
    }

}

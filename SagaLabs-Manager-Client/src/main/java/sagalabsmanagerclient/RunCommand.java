package sagalabsmanagerclient;


import com.azure.resourcemanager.compute.models.RunCommandInput;
import com.azure.resourcemanager.compute.models.RunCommandResult;
import com.azure.resourcemanager.compute.models.VirtualMachine;

import java.util.ArrayList;
import java.util.Arrays;


public class RunCommand extends AzureMethods {
    public static String runCommandOnVM(MachinesVM vm, String script) {
        VirtualMachine virtualMachine = AzureLogin.getAzure().virtualMachines().getById(vm.azureId);
        RunCommandInput runCommandInput = new RunCommandInput().withCommandId("RunShellScript").withScript(Arrays.asList(script));
        RunCommandResult runCommandResult = virtualMachine.runCommand(runCommandInput);

        System.out.println(runCommandResult.value().get(0).message());
        return runCommandResult.value().get(0).message();
    }



    //The users shouldnt run code meant for bash on both linux and windows machines.
        public static boolean checkOSIsTheSame(ArrayList<MachinesVM> vms) {
            if (vms.size() > 0) {
                VirtualMachine firstVM = AzureLogin.getAzure().virtualMachines().getById(vms.get(0).azureId);
                String firstOS = firstVM.osType().toString();
                for (int i = 1; i < vms.size(); i++) {
                    VirtualMachine currentVM = AzureLogin.getAzure().virtualMachines().getById(vms.get(i).azureId);
                    String currentOS = currentVM.osType().toString();
                    if (!currentOS.equals(firstOS)) {
                        return false;
                    }
                }
            }
            return true;
        }

    public static ArrayList<String> runScriptOnVms(ArrayList<MachinesVM> vms, String script) {
        ArrayList<String> scriptOutputs = new ArrayList<String>();
        ArrayList<Thread> threads = new ArrayList<Thread>();
        if (!checkOSIsTheSame(vms)) {
            // Return an error or throw an exception, as the VMs have different OSes.
            return null;
        }
        for (MachinesVM vm : vms) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    String output = runCommandOnVM(vm, script);
                    synchronized (scriptOutputs) {
                        scriptOutputs.add("output from: " + vm.getVmName() + "\n" + output);
                    }
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

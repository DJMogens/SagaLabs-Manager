package sagalabsmanagerclient;


import com.azure.resourcemanager.compute.models.RunCommandInput;
import com.azure.resourcemanager.compute.models.RunCommandResult;
import com.azure.resourcemanager.compute.models.VirtualMachine;

import java.util.Arrays;


public class RunCommand extends AzureMethods {
    public static String runCommandOnVM(MachinesVM vm, String script) {
        VirtualMachine virtualMachine = AzureLogin.getAzure().virtualMachines().getById(vm.azureId);
        RunCommandInput runCommandInput = new RunCommandInput().withCommandId("RunShellScript").withScript(Arrays.asList(script));
        RunCommandResult runCommandResult = virtualMachine.runCommand(runCommandInput);

        System.out.println(runCommandResult.value().get(0).message());
        return runCommandResult.value().get(0).message();
    }

}

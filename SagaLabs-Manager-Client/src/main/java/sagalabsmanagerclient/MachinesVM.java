package sagalabsmanagerclient;

import javafx.scene.control.CheckBox;

public class MachinesVM {
    private final CheckBox select;
    private String id; //local id for view and identifying.
    private final String vmName;
    private final String os;
    private final String state;
    private final String ip;
    private final String resourceGroup;
    private final String azureId; //the azure specific ID
    private String lastScriptOutput; //the azure specific ID

    public MachinesVM(String id, String vmName, String os, String state, String ip, String resourceGroup, String azureId) {
        this.select = new CheckBox("");
        this.id = id;
        this.vmName = vmName;
        this.os = os;
        this.state = state;
        this.ip = ip;
        this.resourceGroup = resourceGroup;
        this.azureId = azureId;
    }
    public String getId() {
        return id;
    }
    public String getVmName() {
        return vmName;
    }
    public String getOs() {
        return os;
    }
    public String getState() {return state;}
    public CheckBox getSelect() { return select;}
    public boolean getSelected() { return select.isSelected();}
    public String getIp() { return ip; }
    public String getResourceGroup() { return resourceGroup;}
    public String getAzureId() { return azureId;}
    public String getLastScriptOutput() { return lastScriptOutput;}
    public void setLastScriptOutput(String lastScriptOutput) {
        this.id = lastScriptOutput;
    }
}

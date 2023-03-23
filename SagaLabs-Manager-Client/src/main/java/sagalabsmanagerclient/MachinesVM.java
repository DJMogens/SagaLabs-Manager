package sagalabsmanagerclient;

import javafx.scene.control.CheckBox;

public class MachinesVM {
    private CheckBox select;
    private String id; //local id for view and identifying.
    private String vmName;
    private String os;
    private String state;
    private String ip;
    private String resourceGroup;
    public String azureId; //the azure specific ID

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
}

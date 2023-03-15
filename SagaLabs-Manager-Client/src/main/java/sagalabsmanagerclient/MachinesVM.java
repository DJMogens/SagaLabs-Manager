package sagalabsmanagerclient;

import javafx.scene.control.CheckBox;

public class MachinesVM {
    private CheckBox select;
    private String id;
    private String vmName;
    private String os;
    private String state;
    private String resourceGroup;

    public MachinesVM(String id, String vmName, String os, String state, String resourceGroup) {
        this.select = new CheckBox("");
        this.id = id;
        this.vmName = vmName;
        this.os = os;
        this.state = state;
        this.resourceGroup = resourceGroup;
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
    public String getResourceGroup() { return resourceGroup;}
}

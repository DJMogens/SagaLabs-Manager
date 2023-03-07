package com.example.sagalabsmanager;

import com.azure.resourcemanager.compute.models.OperatingSystemTypes;
import javafx.scene.control.CheckBox;

public class MachinesVM {
    private CheckBox select;
    private String id;
    private String vmName;
    private String os;
    private String state;

    public MachinesVM(String id, String vmName, OperatingSystemTypes os, String state) {
        this.select = new CheckBox("");
        this.id = id;
        this.vmName = vmName;
        this.os = os.toString();
        this.state = state;
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

}

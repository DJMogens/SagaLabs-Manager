package com.example.sagalabsmanager;

import com.azure.resourcemanager.compute.models.OperatingSystemTypes;

public class MachinesVM {
    private String id;
    private String vmName;
    private String os;
    private String state;

    public MachinesVM(String id, String vmName, OperatingSystemTypes os, String state) {
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
}

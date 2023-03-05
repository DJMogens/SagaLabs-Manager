package com.example.sagalabsmanager;

import com.azure.resourcemanager.compute.models.OperatingSystemTypes;

public class MachinesVM {
    private String id;
    private String vmName;
    private String os;

    public MachinesVM(String id, String vmName, OperatingSystemTypes os) {
        this.id = id;
        this.vmName = vmName;
        this.os = os.toString();
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
}

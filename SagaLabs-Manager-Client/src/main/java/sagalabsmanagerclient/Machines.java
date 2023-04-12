package sagalabsmanagerclient;

import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;

public class Machines {
    public static ArrayList<MachinesVM> machines = new ArrayList<>();
    public static Thread refreshing = new Thread(() -> {
        while(true) {
            System.out.println("Retrieving machines from database");
            setMachines();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    });
    public static void InitMachines() {
        if(!refreshing.isAlive()) {
            refreshing.start();
        }
    }
    public static void setMachines() {
        if(!machines.isEmpty()) {
            machines.clear();
        }
        try {
            machines.addAll(Database.getMachines());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static ArrayList<MachinesVM> getMachines() {
        return machines;
    }
    public static ArrayList<String> getResourceGroups() {
        ArrayList<String> resourceGroups = new ArrayList<>();
        for(MachinesVM machine: machines) {
            if(!resourceGroups.contains(machine.getResourceGroup())) {
                resourceGroups.add(machine.getResourceGroup());
            }
        }
        return resourceGroups;
    }
}

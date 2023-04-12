package sagalabsmanagerclient;

import java.sql.Array;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Machines {
    public static ArrayList<MachinesVM> machines = new ArrayList<>();
    public static Thread refreshing = new Thread(() -> {
        while(true) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            System.out.println("Retrieving machines from database at " + dtf.format(now));

            setMachines();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}
        }
    });
    public static void InitMachines() {
        if(!refreshing.isAlive()) {
            try {
                refreshing.start();
            }
            catch (RuntimeException ignored) {}
        }
    }
    public static void setMachines() {
        if(!machines.isEmpty()) {
            machines.clear();
        }
        try {
            machines.addAll(Database.getMachines());
        } catch (SQLException ignored) {}
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
    public static void stopRefreshing() {
        try {
            refreshing.interrupt();
        }
        catch(NullPointerException ignored) {}
    }
}

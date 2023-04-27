package sagalabsmanagerserver;

import com.azure.resourcemanager.compute.models.PowerState;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.azure.resourcemanager.resources.models.ResourceGroup;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.*;

public class Database {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://sagadb.sagalabs.dk:42069/sagadb";
    private static final String dbUsername = "sagalabs-manager";
    static String dbPassword = AzureMethods.getKeyVaultSecret("sagalabs-manager-SQL-pw");
    static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void syncLabs() throws SQLException {
        System.out.println("Starting syncLabs...");
        long startTime = System.currentTimeMillis();

        String tableName = "Labs";

        List<ResourceGroup> labs = AzureLogin.azure.resourceGroups().listByTag("lab", "true").stream().toList();

        // Get all labs in the database
        List<String> dbLabs = getAllResourceIds(tableName);

        int port = 80; // port of the vpn service

        // Prepare batch update
        String sql = "INSERT INTO Labs (LabName, LabID, VmCount, LabVPN, vpnRunning, azureID) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE LabName=VALUES(LabName), LabID=VALUES(LabID), VmCount=VALUES(VmCount), LabVPN=VALUES(LabVPN), vpnRunning=VALUES(vpnRunning), azureID=VALUES(azureID)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            labs.parallelStream().forEach(lab -> {
                if (lab == null) {
                    return;
                }
                String labName = lab.name() != null ? lab.name() : "";
                String labID = lab.id() != null ? lab.id() : "";

                int vmCount = 0;
                if (labName != null && !labName.isEmpty()) {
                    vmCount = AzureLogin.azure.virtualMachines().listByResourceGroup(labName).stream().toList().size();
                }
                List<PublicIpAddress> publicIps = new ArrayList<>();
                if (labName != null && !labName.isEmpty()) {
                    publicIps = AzureLogin.azure.publicIpAddresses().listByResourceGroup(labName).stream().toList();
                }
                //iterate over all public ip's in each lab, and find the ip that is contains VPN in the name
                String vpnPublicIp = "No public IP";
                boolean vpnRunning = false;
                for (PublicIpAddress publicIp : publicIps) {
                    if (publicIp == null) {
                        continue;
                    }
                    String publicIpName = publicIp.name();
                    if (publicIpName != null && publicIpName.contains("VPN")) {
                        vpnPublicIp = publicIp.ipAddress() != null ? publicIp.ipAddress() : "No public IP";
                        vpnRunning = isVpnRunning(vpnPublicIp, port, 2000, 3); // 3 retries with 2000ms timeout
                        break;
                    }
                }
                try {
                    stmt.setString(1, labName);
                    stmt.setString(2, labID);
                    stmt.setInt(3, vmCount);
                    stmt.setString(4, vpnPublicIp);
                    stmt.setBoolean(5, vpnRunning);
                    stmt.setString(6, labID);
                    stmt.addBatch();

                    if (labID != null && !labID.isEmpty()) {
                        dbLabs.remove(labID);
                    }
                    System.out.println("Lab synced: " + labName);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    public static void syncVM() throws SQLException {
        System.out.println("Starting syncVM...");

        long startTime = System.currentTimeMillis();

        String tableName = "vm";

        // Get all VMs in the database
        List<String> dbVMs = getAllResourceIds(tableName);

        // Get all labs in Azure
        List<ResourceGroup> labs = AzureLogin.azure.resourceGroups().listByTag("lab", "true").stream().toList();

        // Process virtual machines in parallel
        labs.parallelStream().forEach(rg -> {
            // Get all virtual machines in the resource group
            List<VirtualMachine> virtualMachines = AzureLogin.azure.virtualMachines().listByResourceGroup(rg.name()).stream().toList();

            System.out.println("Processing resource group: " + rg.name());

            // Prepare batch update
            String sql = "INSERT INTO vm (vm_name, powerstate, internal_ip, ostype, resource_group, azureID) VALUES (?,?,?,?,?,?) " +
                    "ON DUPLICATE KEY UPDATE powerstate=VALUES(powerstate), internal_ip=VALUES(internal_ip), ostype=VALUES(ostype), resource_group=VALUES(resource_group),azureID=VALUES(azureID)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (VirtualMachine vm : virtualMachines) {
                    try {
                        if (vm == null) {
                            continue;
                        }
                        stmt.setString(1, vm.name());
                        PowerState powerState = vm.powerState();
                        if (powerState != null) {
                            stmt.setString(2, powerState.toString());
                        } else {
                            stmt.setString(2, "");
                        }
                        if (vm.getPrimaryNetworkInterface() != null) {
                            String internalIp = vm.getPrimaryNetworkInterface().primaryPrivateIP();
                            stmt.setString(3, internalIp != null ? internalIp : "");
                        } else {
                            stmt.setString(3, "");
                        }
                        stmt.setString(4, vm.osType() != null ? vm.osType().toString() : "");
                        stmt.setString(5, vm.resourceGroupName() != null ? vm.resourceGroupName() : "");
                        stmt.setString(6, vm.id() != null ? vm.id() : "");
                        stmt.addBatch();
                        if (vm.id() != null && !vm.id().isEmpty()) {
                            dbVMs.remove(vm.id());
                        }
                        System.out.println("VM synced: " + vm.name());
                    } catch (Exception e) {
                        System.out.println("Error syncing VM: " + vm.name() + " | " + e.getMessage());
                    }
                }
                stmt.executeBatch();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        // Delete VMs from the database that are no longer in Azure
        for (String vmId : dbVMs) {
            String sql = "DELETE FROM " + tableName + " WHERE azureID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, vmId);
            System.out.println("Deleting VM from database: " + vmId);

            stmt.executeUpdate();
        }

        updateLastUpdate(tableName);

        long elapsedTime = System.currentTimeMillis() - startTime;
        if (elapsedTime > 30_000) {
            System.out.printf("syncLabs took longer than 30 seconds to execute: %d ms%n", elapsedTime);
        }

        System.out.println("syncVM complete.");
    }




    public static void updateLastUpdate(String tableName) throws SQLException {
        String sql = "INSERT INTO last_updated (table_name, last_update) VALUES (?, NOW()) " +
                "ON DUPLICATE KEY UPDATE last_update=NOW()";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, tableName);
        stmt.executeUpdate();
    }

    public static List<String> getAllResourceIds(String tableName) throws SQLException {
        List<String> resourceIds = new ArrayList<>();
        String sql = "SELECT azureID FROM " + tableName;
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);

        while (resultSet.next()) {
            resourceIds.add(resultSet.getString("azureID"));
        }
        return resourceIds;
    }

    public static boolean isVpnRunning(String vpnPublicIp, int port, int timeout, int retries) {
        for (int i = 0; i < retries; i++) {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(vpnPublicIp, port), timeout);
                if (socket.isConnected()) {
                    socket.close();
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }



}

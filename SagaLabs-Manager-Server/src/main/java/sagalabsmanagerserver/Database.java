package sagalabsmanagerserver;

import com.azure.resourcemanager.compute.models.PowerState;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.net.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    // Database credentials
    // Database credentials
    private static final String DB_URL = "jdbc:mysql://sagadb.sagalabs.dk:42069/sagadb";
    private static final String dbUsername = "sagalabs-manager";
    static String dbPassword = AzureMethods.getKeyVaultSecret("sagalabs-manager-SQL-pw");
    static HikariDataSource dataSource; // used for connection pooling
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());


    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setMaximumPoolSize(50);

        dataSource = new HikariDataSource(config);
    }


    public static void syncLabs() throws SQLException {
        LOGGER.info("Starting syncLabs...");
        long startTime = System.currentTimeMillis();

        String tableName = "Labs";
        List<ResourceGroup> labs = AzureLogin.azure.resourceGroups().listByTag("lab", "true").stream().toList();

        // Add a timestamp before getting all labs in the database
        long startDbLabs = System.currentTimeMillis();
        List<String> dbLabs = getAllResourceIds(tableName);
        LOGGER.info(String.format("getAllResourceIds time: %d ms", System.currentTimeMillis() - startDbLabs));

        int port = 80; // port of the vpn service

        // Prepare batch update
        String sql = "INSERT INTO Labs (LabName, LabID, VmCount, LabVPN, vpnRunning, azureID) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE LabName=VALUES(LabName), LabID=VALUES(LabID), VmCount=VALUES(VmCount), LabVPN=VALUES(LabVPN), vpnRunning=VALUES(vpnRunning), azureID=VALUES(azureID)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            labs.parallelStream().forEach(lab -> {
                if (lab == null) {
                    return;
                }
                String labName = lab.name() != null ? lab.name() : "";
                String labID = lab.id() != null ? lab.id() : "";

                // Add a timestamp before getting VM count
                long startVmCount = System.currentTimeMillis();
                int vmCount = 0;
                if (!labName.isEmpty()) {
                    vmCount = AzureLogin.azure.virtualMachines().listByResourceGroup(labName).stream().toList().size();
                }
                LOGGER.info(String.format("VM count time for %s: %d ms", labName, System.currentTimeMillis() - startVmCount));

                // Add a timestamp before getting public IPs
                long startPublicIps = System.currentTimeMillis();
                List<PublicIpAddress> publicIps = new ArrayList<>();
                if (!labName.isEmpty()) {
                    publicIps = AzureLogin.azure.publicIpAddresses().listByResourceGroup(labName).stream().toList();
                }
                LOGGER.info(String.format("Public IPs time for %s: %d ms", labName, System.currentTimeMillis() - startPublicIps));

                // Iterate over all public IPs in each lab and find the IP that contains VPN in the name
                String vpnPublicIp = "No public IP";
                boolean vpnRunning = false;
                for (PublicIpAddress publicIp : publicIps) {
                    if (publicIp == null) {
                        continue;
                    }
                    String publicIpName = publicIp.name();
                    if (publicIpName != null && publicIpName.contains("VPN")) {
                        vpnPublicIp = publicIp.ipAddress() != null ? publicIp.ipAddress() : "No public IP";
                        long startPortScan = System.currentTimeMillis();
                        try {
                            Socket socket = new Socket();
                            socket.connect(new InetSocketAddress(vpnPublicIp, port), 2000);
                            vpnRunning = socket.isConnected();
                            socket.close();
                        } catch (Exception ignored) {
                        }
                        LOGGER.info(String.format("Port scan time for %s: %d ms", vpnPublicIp, System.currentTimeMillis() - startPortScan));

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

                    if (!labID.isEmpty()) {
                        dbLabs.remove(labID);
                    }
                    LOGGER.info("Lab synced: " + labName);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        LOGGER.info(String.format("syncLabs complete. Total time: %d ms", elapsedTime));
    }






    public static void syncVM() throws SQLException {
        LOGGER.info("Starting syncVM...");

        long startTime = System.currentTimeMillis();

        String tableName = "vm";

        // Get all VMs in the database
        List<String> dbVMs = getAllResourceIds(tableName);

        // Get all labs in Azure
        List<ResourceGroup> labs = AzureLogin.azure.resourceGroups().listByTag("lab", "true").stream().toList();

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Process virtual machines in parallel
        List<Future<?>> futures = new ArrayList<>();
        for (ResourceGroup rg : labs) {
            futures.add(executor.submit(() -> {
                // Get all virtual machines in the resource group
                List<VirtualMachine> virtualMachines = AzureLogin.azure.virtualMachines().listByResourceGroup(rg.name()).stream().toList();

                LOGGER.info("Processing resource group: " + rg.name());

                // Prepare batch update
                String sql = "INSERT INTO vm (vm_name, powerstate, internal_ip, ostype, resource_group, azureID) VALUES (?,?,?,?,?,?) " +
                        "ON DUPLICATE KEY UPDATE powerstate=VALUES(powerstate), internal_ip=VALUES(internal_ip), ostype=VALUES(ostype), resource_group=VALUES(resource_group),azureID=VALUES(azureID)";
                try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                            LOGGER.info("VM synced: " + vm.name());
                        } catch (Exception e) {
                            LOGGER.warning("Error syncing VM: " + vm.name() + " | " + e.getMessage());
                        }
                    }
                    stmt.executeBatch();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }));
        }

        futures.forEach(future -> {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.log(Level.SEVERE, "Error in futures.get()", e);
            }
        });

        executor.shutdown();

        // Delete VMs from the database that are no longer in Azure
        for (String vmId : dbVMs) {
            String sql = "DELETE FROM " + tableName + " WHERE azureID = ?";
            try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, vmId);
                LOGGER.info("Deleting VM from database: " + vmId);

                stmt.executeUpdate();
            }
        }

        updateLastUpdate(tableName);

        long elapsedTime = System.currentTimeMillis() - startTime;

        if (elapsedTime > 30_000) {
            LOGGER.warning(String.format("syncVM took longer than 30 seconds to execute: %d ms", elapsedTime));
        }

        LOGGER.info("syncVM complete.");
    }






    public static void updateLastUpdate(String tableName) throws SQLException {
        String sql = "INSERT INTO last_updated (table_name, last_update) VALUES (?, NOW()) " +
                "ON DUPLICATE KEY UPDATE last_update=NOW()";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tableName);
            stmt.executeUpdate();
        }
        LOGGER.info(String.format("Last update timestamp for table '%s' has been updated.", tableName));
    }

    public static List<String> getAllResourceIds(String tableName) throws SQLException {
        List<String> resourceIds = new ArrayList<>();
        String sql = "SELECT azureID FROM " + tableName;
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            ResultSet resultSet = stmt.executeQuery(sql);

            while (resultSet.next()) {
                resourceIds.add(resultSet.getString("azureID"));
            }
        }
        LOGGER.info(String.format("Retrieved %d resource IDs from table '%s'.", resourceIds.size(), tableName));
        return resourceIds;
    }


}
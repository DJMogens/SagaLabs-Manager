package sagalabsmanagerserver;

import com.azure.resourcemanager.compute.models.PowerState;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

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

        // Fetch information for each lab and store it in a data structure
        List<LabInfo> labInfos = new ArrayList<>();
        for (ResourceGroup lab : labs) {
            LabInfo labInfo = new LabInfo();
            labInfo.fetchLabInfo(lab);
            labInfos.add(labInfo);
        }

        // Add a timestamp before getting all labs in the database
        long startDbLabs = System.currentTimeMillis();
        List<String> dbLabs = getAllResourceIds(tableName);
        LOGGER.info(String.format("getAllResourceIds time: %d ms", System.currentTimeMillis() - startDbLabs));

        int port = 80; // port of the vpn service

        // Prepare batch update
        String sql = "INSERT INTO Labs (LabName, LabID, VmCount, LabVPN, vpnRunning, azureID) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE LabName=VALUES(LabName), LabID=VALUES(LabID), VmCount=VALUES(VmCount), LabVPN=VALUES(LabVPN), vpnRunning=VALUES(vpnRunning), azureID=VALUES(azureID)";

        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (LabInfo labInfo : labInfos) {
                if (labInfo == null) {
                    continue;
                }
                stmt.setString(1, labInfo.getLabName());
                stmt.setString(2, labInfo.getLabID());
                stmt.setInt(3, labInfo.getVmCount());
                stmt.setString(4, labInfo.getVpnPublicIp());
                stmt.setBoolean(5, labInfo.isVpnRunning());
                stmt.setString(6, labInfo.getLabID());
                stmt.addBatch();

                if (!labInfo.getLabID().isEmpty()) {
                    dbLabs.remove(labInfo.getLabID());
                }
                LOGGER.info("Lab synced: " + labInfo.getLabName());
            }
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

        // Fetch VM information for each lab and store it in a data structure
        List<LabInfo> labInfos = new ArrayList<>();
        for (ResourceGroup lab : labs) {
            LabInfo labInfo = new LabInfo();
            labInfo.fetchLabInfo(lab);
            labInfo.fetchVMs();
            labInfos.add(labInfo);
        }

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Process virtual machines in parallel
        List<Future<?>> futures = new ArrayList<>();
        for (LabInfo labInfo : labInfos) {
            futures.add(executor.submit(() -> {
                List<VirtualMachine> virtualMachines = labInfo.getVirtualMachines();

                LOGGER.info("Processing resource group: " + labInfo.getLabName());

                // Prepare batch update
                String sql = "INSERT INTO vm (vm_name, powerstate, internal_ip, ostype, resource_group, azureID) VALUES (?,?,?,?,?,?) " +
                        "ON DUPLICATE KEY UPDATE powerstate=VALUES(powerstate), internal_ip=VALUES(internal_ip), ostype=VALUES(ostype), resource_group=VALUES(resource_group),azureID=VALUES(azureID)";
                try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
                    for (VirtualMachine vm : virtualMachines) {
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

class LabInfo {
    private List<VirtualMachine> virtualMachines;

    private String labName;
    private String labID;
    private int vmCount;
    private String vpnPublicIp;
    private boolean vpnRunning;

    public void fetchLabInfo(ResourceGroup lab) {
        labName = lab.name() != null ? lab.name() : "";
        labID = lab.id() != null ? lab.id() : "";

        if (!labName.isEmpty()) {
            vmCount = AzureLogin.azure.virtualMachines().listByResourceGroup(labName).stream().toList().size();
        }

        List<PublicIpAddress> publicIps = new ArrayList<>();
        if (!labName.isEmpty()) {
            publicIps = AzureLogin.azure.publicIpAddresses().listByResourceGroup(labName).stream().toList();
        }

        vpnPublicIp = "No public IP";
        vpnRunning = false;
        for (PublicIpAddress publicIp : publicIps) {
            if (publicIp == null) {
                continue;
            }
            String publicIpName = publicIp.name();
            if (publicIpName != null && publicIpName.contains("VPN")) {
                vpnPublicIp = publicIp.ipAddress() != null ? publicIp.ipAddress() : "No public IP";

                int port = 80; // port of the vpn service
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(vpnPublicIp, port), 2000);
                    vpnRunning = socket.isConnected();
                    socket.close();
                } catch (Exception ignored) {
                }

                break;
            }
        }
    }

    public void fetchVMs() {
        if (!labName.isEmpty()) {
            virtualMachines = AzureLogin.azure.virtualMachines().listByResourceGroup(labName).stream().toList();
        }
    }

    public List<VirtualMachine> getVirtualMachines() {
        return virtualMachines;
    }

    public String getLabName() {
        return labName;
    }

    public String getLabID() {
        return labID;
    }

    public int getVmCount() {
        return vmCount;
    }

    public String getVpnPublicIp() {
        return vpnPublicIp;
    }

    public boolean isVpnRunning() {
        return vpnRunning;
    }
}

package sagalabsmanagerserver;

import com.azure.resourcemanager.compute.models.PowerState;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.azure.resourcemanager.resources.models.ResourceGroup;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        String tableName = "Labs";

        List<ResourceGroup> labs = AzureLogin.azure.resourceGroups().listByTag("lab", "true").stream().toList();

        int port = 80; // port of the vpn service

        for (ResourceGroup lab : labs) {
            String labName = lab.name();
            String labID = lab.id();

            int vmCount = AzureLogin.azure.virtualMachines().listByResourceGroup(labName).stream().toList().size();
            List<PublicIpAddress> publicIps = AzureLogin.azure.publicIpAddresses().listByResourceGroup(labName).stream().toList();
            //iterate over all public ip's in each lab, and find the ip that is contains VPN in the name
            String vpnPublicIp = "No public IP";
            boolean vpnRunning = false;
            for (PublicIpAddress publicIp : publicIps) {
                String publicIpName = publicIp.name();
                if (publicIpName.contains("VPN")) {
                    vpnPublicIp = publicIp.ipAddress();
                    try {
                        Socket socket = new Socket();
                        socket.connect(new InetSocketAddress(vpnPublicIp, port), 2000);
                        if (socket.isConnected()){
                            vpnRunning = true;
                        }
                        socket.close();
                    }catch (Exception ignored){

                    }

                    break;
                }
            }
            //insert vars into database, or update if a key is duplicated
            String sql = "INSERT INTO Labs (LabName,LabID,VmCount,LabVPN,vpnRunning) VALUES (?,?,?,?,?)"+
                    "ON DUPLICATE KEY UPDATE LabName=VALUES(LabName), LabVPN=VALUES(LabVPN), VmCount=VALUES(VmCount), vpnRunning=VALUES(vpnRunning)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, labName);
            stmt.setString(2, labID);
            stmt.setInt(3, vmCount);
            stmt.setString(4, vpnPublicIp);
            stmt.setBoolean(5, vpnRunning);
            stmt.executeUpdate();
        }
        updateLastUpdate(tableName);
    }

    public static void syncVM() throws SQLException {
        String tableName = "vm"; // replace with the actual table name
        //for each resourcegroup that it a lab; for each virtual machine
        for (ResourceGroup rg : AzureLogin.azure.resourceGroups().listByTag("lab", "true")) {

            // Get all virtual machines in the resource group
            for (VirtualMachine vm : AzureLogin.azure.virtualMachines().listByResourceGroup(rg.name())) {
                //insert vars into database, or update if a key is duplicated
                String sql = "INSERT INTO vm (vm_name, powerstate, internal_ip, ostype, resource_group) VALUES (?,?,?,?,?) " +
                        "ON DUPLICATE KEY UPDATE powerstate=VALUES(powerstate), internal_ip=VALUES(internal_ip), ostype=VALUES(ostype), resource_group=VALUES(resource_group)";

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, vm.name());
                PowerState powerState = vm.powerState();
                if (powerState != null) {
                    stmt.setString(2, powerState.toString());
                } else {
                    stmt.setString(2, "");
                }
                stmt.setString(2, vm.powerState().toString());
                stmt.setString(3, vm.getPrimaryNetworkInterface().primaryPrivateIP());
                stmt.setString(4, vm.osType().toString());
                stmt.setString(5, vm.resourceGroupName());
                stmt.executeUpdate();
            }
            updateLastUpdate(tableName);
        }

    }
    public static void updateLastUpdate(String tableName) throws SQLException {
        String sql = "INSERT INTO last_updated (table_name, last_update) VALUES (?, NOW()) " +
                "ON DUPLICATE KEY UPDATE last_update=NOW()";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, tableName);
        stmt.executeUpdate();
    }


}

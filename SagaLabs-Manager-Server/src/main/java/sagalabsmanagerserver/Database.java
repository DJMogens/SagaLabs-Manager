package sagalabsmanagerserver;

import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.azure.resourcemanager.resources.models.ResourceGroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Database {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://sagadb.sagalabs.dk:42069/sagadb";
    private static final String dbUsername = "sagalabs-manager";
    static String dbPassword = AzureMethods.getKeyVaultSecret("sagalabs-manager-SQL-pw");
    public static boolean sqlLogin() throws SQLException {
        Connection conn = null;
        // Open a connection
        System.out.println("Connecting to database...");
        conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
        {
            try {
                if (conn != null) {
                    conn.close();
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static void syncLabs() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
        List<ResourceGroup> labs = AzureLogin.azure.resourceGroups().listByTag("lab", "true").stream().toList();

        String truncateSql = "TRUNCATE TABLE Labs";
        PreparedStatement truncateStmt = conn.prepareStatement(truncateSql);
        truncateStmt.executeUpdate();
        int id = 0;
        for (ResourceGroup lab : labs) {
            String labName = lab.name();
            String labID = lab.id();
            int vmCount = AzureLogin.azure.virtualMachines().listByResourceGroup(labName).stream().toList().size();
            List<PublicIpAddress> publicIps = AzureLogin.azure.publicIpAddresses().listByResourceGroup(labName).stream().toList();
            //iterate over all public ip's in each lab, and find the ip that is contains VPN in the name
            String vpnPublicIp = "No public IP";
            for (PublicIpAddress publicIp : publicIps) {
                String publicIpName = publicIp.name();
                if (publicIpName.contains("VPN")) {
                    vpnPublicIp = publicIp.ipAddress();
                    break;
                }
            }

            String sql = "INSERT INTO Labs (LabName,LabID,id,VmCount,LabVPN) VALUES (?,?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, labName);
            stmt.setString(2, labID);
            stmt.setInt(3, id);
            stmt.setInt(4, vmCount);
            stmt.setString(5, vpnPublicIp);
            stmt.executeUpdate();
            id++;
        }
        syncVM();

    }

    public static void syncVM() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);

        List<ResourceGroup> labs = AzureLogin.azure.resourceGroups().listByTag("lab", "true").stream().toList();

        //refresh database
        String truncateSql = "TRUNCATE TABLE vm";
        PreparedStatement truncateStmt = conn.prepareStatement(truncateSql);
        truncateStmt.executeUpdate();

        //for each resourcegroup that it a lab; for each virtual machine
        for (ResourceGroup rg : AzureLogin.azure.resourceGroups().listByTag("lab", "true")) {

            // Get all virtual machines in the resource group
            for (VirtualMachine vm : AzureLogin.azure.virtualMachines().listByResourceGroup(rg.name())) {
                String sql = "INSERT INTO vm (vm_name, powerstate, internal_ip, ostype) VALUES (?,?,?,?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, vm.name());
                stmt.setString(2, vm.powerState().toString());
                stmt.setString(3, vm.osType().toString());
                stmt.setString(4, vm.getPrimaryNetworkInterface().primaryPrivateIP());
                stmt.executeUpdate();
            }
        }
    }
}

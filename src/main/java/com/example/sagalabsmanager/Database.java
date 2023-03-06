package com.example.sagalabsmanager;

import com.azure.resourcemanager.resources.models.ResourceGroup;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static com.example.sagalabsmanager.AzureLogin.azure;

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
        List<ResourceGroup> labs = azure.resourceGroups().listByTag("lab", "true").stream().toList();

        String truncateSql = "TRUNCATE TABLE Labs";
        PreparedStatement truncateStmt = conn.prepareStatement(truncateSql);
        truncateStmt.executeUpdate();
        int id = 0;
        for (ResourceGroup lab : labs) {
            String labName = lab.name();
            String labID = lab.id();
            String sql = "INSERT INTO Labs (LabName,LabID,id) VALUES (?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, labName);
            stmt.setString(2, labID);
            stmt.setInt(3, id);
            stmt.executeUpdate();
            id++;
        }

        System.out.println(labs);
    }
}

package sagalabsmanagerclient;

import java.sql.*;
import java.util.ArrayList;

public class Database {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://sagadb.sagalabs.dk:42069/sagadb";
    private static final String dbUsername = "sagalabs-manager";
    private final static AzureMethods azureMethods = new AzureMethods();
    static final String dbPassword = azureMethods.getKeyVaultSecret("sagalabs-manager-SQL-pw");
    public static Connection conn;

    static {
        try {
            conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean login() throws SQLException {
            conn.close();
            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
        return conn != null;
    }
    public static ArrayList<MachinesVM> getMachines() throws SQLException {
        if(conn.isClosed()) {
            login();
        }
        ArrayList<MachinesVM> machinesVMs = new ArrayList<>();
        String sql;
        sql = "SELECT * FROM sagadb.vm";
        ResultSet resultSet = executeSql(sql);

        while (resultSet.next()) {
            machinesVMs.add(new MachinesVM(
                    resultSet.getObject("id").toString(),
                    resultSet.getObject("vm_name").toString(),
                    resultSet.getObject("ostype").toString(),
                    resultSet.getObject("powerstate").toString().substring(11),
                    resultSet.getObject("internal_ip").toString(),
                    resultSet.getObject("resource_group").toString(),
                    resultSet.getObject("azureID").toString()));
        }
        return machinesVMs;
    }

    public static ArrayList<String> getResourceGroups() throws SQLException {
        if(conn.isClosed()) {
            login();
        }
        ArrayList<String> resourceGroups = new ArrayList<>();
        ResultSet resultSet = executeSql("select distinct resource_group from vm");
        while(resultSet.next()) {
            resourceGroups.add(resultSet.getObject("resource_group").toString());
        }
        return resourceGroups;
    }
    public static ResultSet executeSql(String sql) throws SQLException {
        if(conn.isClosed()) {
            login();
        }
        Statement statement = conn.createStatement();
        return statement.executeQuery(sql);
    }
}

package sagalabsmanagerclient;

import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.network.models.PublicIpAddress;
import com.azure.resourcemanager.resources.models.ResourceGroup;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.List;

public class Database {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://sagadb.sagalabs.dk:42069/sagadb";
    private static final String dbUsername = "sagalabs-manager";
    static String dbPassword = AzureMethods.getKeyVaultSecret("sagalabs-manager-SQL-pw");
    private static Connection conn = null;

    public static boolean login() throws SQLException {
            // Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, dbUsername, dbPassword);
            if (conn != null) {
                return true;
            }
            return false;
    }
    public static void getMachines() throws SQLException {
        System.out.println("GETTING MACHINES FROM DATABASE");
        Statement statement = conn.createStatement();
        String sql = "SELECT * FROM sagadb.vm";
        ResultSet resultSet = statement.executeQuery(sql);

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columns = metaData.getColumnCount();
        System.out.println(columns + " columns");
        for(int i = 1; i < columns; i++) {
            String columnName = metaData.getColumnName(i);
            System.out.print(columnName+ "   ");
        }
        System.out.print("\n");
        while (resultSet.next()) {
            for (int i = 1; i < columns + 1; i++) {
                System.out.print(resultSet.getObject(i) + "  ");
            }
            System.out.print("\n");
        }
        conn.close();
    }
}

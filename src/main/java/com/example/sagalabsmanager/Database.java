package com.example.sagalabsmanager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://sagadb.sagalabs.dk:42069/sagadb";
    private static final String dbUsername = "sagalabs-manager";

    static String dbPassword = AzureMethods.getKeyVaultSecret("sagalabs-manager-SQL-pw");
    public static boolean sqlLoginCheck() throws SQLException {
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
}

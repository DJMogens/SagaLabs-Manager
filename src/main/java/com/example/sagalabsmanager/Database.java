package com.example.sagalabsmanager;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://sagadb.sagalabs.dk:42069/sagadb";
    private static final String dbUsername = "sagalabs-manager";

    // Get the password from Azure Key Vault Secret
    static String keyVaultName = "sagalabskeyvault";
    static String secretName = "sagalabs-manager-SQL-pw";
    static String keyVaultUrl = "https://" + keyVaultName + ".vault.azure.net";
    static SecretClient secretClient = new SecretClientBuilder()
            .vaultUrl(keyVaultUrl)
            .credential(AzureLogin.tokenCredentialKeyVault)
            .buildClient();
    static KeyVaultSecret secret = secretClient.getSecret(secretName);
    static String dbPassword = secret.getValue();
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

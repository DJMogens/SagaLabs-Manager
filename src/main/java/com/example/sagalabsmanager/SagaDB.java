package com.example.sagalabsmanager;

import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SagaDB {

    // Database credentials
    private static final String DB_URL = "jdbc:mysql://130.225.39.157:42069/sagadb";
    private static final String dbUsername = "sagalabs-manager";

    public static boolean sqlLoginCheck() throws SQLException {
        Connection conn = null;
            // Get the password from Azure Key Vault Secret
            String keyVaultName = "sagalabskeyvault";
            String secretName = "sagalabs-manager-SQL-pw";
            String keyVaultUrl = "https://" + keyVaultName + ".vault.azure.net";
            SecretClient secretClient = new SecretClientBuilder()
                    .vaultUrl(keyVaultUrl)
                    .credential(AzureLogin.tokenCredentialKeyVault)
                    .buildClient();
            KeyVaultSecret secret = secretClient.getSecret(secretName);
            String dbPassword = secret.getValue();

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

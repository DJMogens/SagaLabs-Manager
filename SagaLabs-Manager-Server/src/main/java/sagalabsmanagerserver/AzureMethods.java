package sagalabsmanagerserver;
import com.azure.core.http.rest.PagedIterable;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import com.azure.security.keyvault.secrets.models.KeyVaultSecret;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.sql.*;

public class AzureMethods {

    public static String getKeyVaultSecret(String secretName){
        // Get the password from Azure Key Vault Secret
        String keyVaultName = "sagalabskeyvault";
        String keyVaultUrl = "https://" + keyVaultName + ".vault.azure.net";
        SecretClient secretClient = new SecretClientBuilder()
                .vaultUrl(keyVaultUrl)
                .credential(AzureLogin.tokenCredential)
                .buildClient();
        KeyVaultSecret secret = secretClient.getSecret(secretName);
        return secret.getValue();
    }

}

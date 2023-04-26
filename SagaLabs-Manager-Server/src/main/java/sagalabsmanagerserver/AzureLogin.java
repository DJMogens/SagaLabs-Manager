package sagalabsmanagerserver;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpClientProvider;
import com.azure.core.http.netty.NettyAsyncHttpClientBuilder;
import com.azure.core.http.netty.NettyAsyncHttpClientProvider;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.HttpClientOptions;
import com.azure.identity.*;
import com.azure.resourcemanager.resources.ResourceManager;
import io.github.cdimascio.dotenv.Dotenv;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import reactor.core.publisher.Mono;
import com.azure.core.http.policy.TimeoutPolicy;
import java.time.Duration;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;


public class AzureLogin {
        public static AzureProfile profile;
        public static TokenCredential tokenCredential;
        final public static String tenantId = "43f3cd6e-9092-45ae-b8c1-990bd8b3cdca"; //needs to be a environment variable
        final public static String clientId = "4ca9b980-3658-4847-9e7c-33d75a4ea510";//needs to be a environment variable
        final public static String subscriptionId = "06d0a3df-f3c0-4336-927d-db8891937870";
        public static AzureResourceManager azure;


        public static void buildCredentialsFromEnvironment() {
            System.out.println("Authenticating...");
            //makes sure that non-secret environment variables is set
            System.setProperty("AZURE_CLIENT_ID", clientId);
            System.setProperty("AZURE_TENANT_ID", tenantId);
            String azureClientSecret = System.getenv("AZURE_CLIENT_SECRET");



            if (azureClientSecret == null) {
                System.err.println("ERROR: AZURE_CLIENT_SECRET environment variable is not set.\n Retreive it with: az keyvault secret show --name sagalabs-manager-client-secret --vault-name sagalabskeyvault\n");
                System.exit(1);
            }
            tokenCredential = new EnvironmentCredentialBuilder().build();

            profile = new AzureProfile(AzureEnvironment.AZURE);

            ResourceManager azure = ResourceManager
                    .authenticate(tokenCredential, profile)
                    .withSubscription(subscriptionId);
            System.out.println("Authenticated successfully");
        }


    static void startLogin() {
        System.out.println("Getting token credential and profile...");
        buildCredentialsFromEnvironment();

        // Set the timeout duration
        Duration timeoutDuration = Duration.ofSeconds(10);

        azure = AzureResourceManager.configure()
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .withPolicy(new TimeoutPolicy(timeoutDuration)) // Add this line to set the timeout
                .authenticate(tokenCredential, profile)
                .withSubscription(subscriptionId);
    }
}

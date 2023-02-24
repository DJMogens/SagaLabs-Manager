package com.example.sagalabsmanager;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;

public class AzureLogin {
    public static AzureProfile profile;
    public static TokenCredential tokenCredential;
    public static String tenantId = "43f3cd6e-9092-45ae-b8c1-990bd8b3cdca";
    public static String clientId = "4ca9b980-3658-4847-9e7c-33d75a4ea510";
    public static String subscriptionId = "06d0a3df-f3c0-4336-927d-db8891937870";
    public static boolean loginStatus = false;

    public static void login() {
        // Set the Azure tenant ID and client ID
        loginStatus = false;
        // Build the interactive browser credential
        InteractiveBrowserCredential credential = new InteractiveBrowserCredentialBuilder()
                .tenantId(tenantId)
                .clientId(clientId)
                .build();

        // Set the scopes for which the access token is requested
        TokenRequestContext tokenRequestContext = new TokenRequestContext();

        //set the scope for the credential
        tokenRequestContext.setScopes(Collections.singletonList("https://management.azure.com/user_impersonation"));//dette scope siger at API må logge ind og bruge rettigheder på vegne af den indloggede bruger!!!

        // Use the credential to get an access token
        String accessToken = Objects.requireNonNull(credential.getToken(tokenRequestContext).block()).getToken();

        tokenCredential = tokenRequestContext1 -> Mono.just(new AccessToken(accessToken, OffsetDateTime.now().plusHours(1)));

        profile = new AzureProfile(tenantId, clientId, AzureEnvironment.AZURE);
        loginStatus = true;
    }

}

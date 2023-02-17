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
import java.util.*;

public class AzureLogin {
    public AzureProfile profile;
    public TokenCredential tokenCredential;
    public String tenantId = "43f3cd6e-9092-45ae-b8c1-990bd8b3cdca";
    public String clientId = "4ca9b980-3658-4847-9e7c-33d75a4ea510";
    public String subscriptionId = "06d0a3df-f3c0-4336-927d-db8891937870";

    public void login() {
        // Set the Azure tenant ID and client ID


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
        String accessToken = credential.getToken(tokenRequestContext).block().getToken();

        this.tokenCredential = new TokenCredential() {
            @Override
            public Mono<AccessToken> getToken(TokenRequestContext tokenRequestContext) {
                return Mono.just(new AccessToken(accessToken, OffsetDateTime.now().plusHours(1)));
            }
        };

        this.profile = new AzureProfile(tenantId, clientId, AzureEnvironment.AZURE);

    }
    public AzureProfile getProfile(){
        return profile;
    }
    public TokenCredential getTokenCredential(){
        return tokenCredential;
    }

}

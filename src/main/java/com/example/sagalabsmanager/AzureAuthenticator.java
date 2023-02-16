package com.example.sagalabsmanager;

import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.InteractiveRequestParameters;
import com.microsoft.aad.msal4j.PublicClientApplication;

import java.util.Collections;
import java.util.Set;

public class AzureAuthenticator {

    private static String clientId = "4ca9b980-3658-4847-9e7c-33d75a4ea510";
    private static String tenantId = "43f3cd6e-9092-45ae-b8c1-990bd8b3cdca";
    private static String redirectUri = "http://localhost";
    private static Set<String> scopes = Collections.singleton("user.read");
    private static String subscriptionName = "SagaLabs Subscription";
    private static String subscriptionId = "06d0a3df-f3c0-4336-927d-db8891937870";

    public static <AuthenticationResult> IAuthenticationResult authenticate() throws Exception {

        PublicClientApplication pca = PublicClientApplication.builder(clientId)
                .authority("https://login.microsoftonline.com/" + tenantId + "/")
                .build();

        InteractiveRequestParameters parameters = InteractiveRequestParameters.builder(new java.net.URI(redirectUri))
                .scopes(scopes)
                .build();

        AuthenticationResult authResult = (AuthenticationResult) pca.acquireToken(parameters).get();

        System.out.println(authResult);
        return (IAuthenticationResult) authResult;
    }
}
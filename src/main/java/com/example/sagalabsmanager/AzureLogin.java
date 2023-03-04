package com.example.sagalabsmanager;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenCredential;
import com.azure.core.credential.TokenRequestContext;
import com.azure.core.http.policy.HttpLogDetailLevel;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.InteractiveBrowserCredential;
import com.azure.identity.InteractiveBrowserCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.example.sagalabsmanager.controllers.LoginController;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Objects;

public class AzureLogin {
    public static AzureProfile profile;
    public static TokenCredential tokenCredential;
    public static TokenCredential tokenCredentialKeyVault;
    final public static String tenantId = "43f3cd6e-9092-45ae-b8c1-990bd8b3cdca";
    final public static String clientId = "4ca9b980-3658-4847-9e7c-33d75a4ea510";
    final public static String subscriptionId = "06d0a3df-f3c0-4336-927d-db8891937870";
    public static boolean loginStatus = false;

    public static AzureResourceManager azure;

    private static void buildCredentials() {
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
        String accessTokenManagement = Objects.requireNonNull(credential.getToken(tokenRequestContext).block()).getToken();

        tokenCredential = tokenRequestContext1 -> Mono.just(new AccessToken(accessTokenManagement, OffsetDateTime.now().plusHours(1)));

        profile = new AzureProfile(tenantId, clientId, AzureEnvironment.AZURE);


        //do the same to obtain rights for key vault
        InteractiveBrowserCredential credentialKeyVault = new InteractiveBrowserCredentialBuilder()
                .tenantId(tenantId)
                .clientId(clientId)
                .build();

        // Set the scopes for which the access token is requested
        TokenRequestContext tokenRequestContextKeyVault = new TokenRequestContext();

        //set the scope for the credential
        tokenRequestContextKeyVault.setScopes(Collections.singletonList("https://vault.azure.net/user_impersonation"));//dette scope siger at API må logge ind og bruge rettigheder på vegne af den indloggede bruger!!!

        // Use the credential to get an access token
        String accessTokenKeyVault = Objects.requireNonNull(credentialKeyVault.getToken(tokenRequestContextKeyVault).block()).getToken();
        tokenCredentialKeyVault = tokenRequestContext2 -> Mono.just(new AccessToken(accessTokenKeyVault, OffsetDateTime.now().plusHours(1)));

        loginStatus = true;

    }

    public static void login() {
        Thread azureLoginThread = new Thread(AzureLogin::startLogin);
        //
        //tilføj kode der omskriver login til try again knap
        //
        //Kontroller om login er opnået på 120 sekunder
        Thread checkLoginThread = new Thread(() -> {
            checkLogin(azureLoginThread);
        });

        azureLoginThread.start();
        System.out.println("Starting check login thread");
        checkLoginThread.start();
    }

    private static void startLogin() {
        System.out.println("Getting token credential and profile...");
        buildCredentials();//husk at logge ind
        System.out.println("Authenticating...");
        azure = AzureResourceManager.configure() //få denne class til at authenticate med tokencredential og profile fra AzureLogin classen
                .withLogLevel(HttpLogDetailLevel.BASIC)
                .authenticate(tokenCredential, profile)
                .withSubscription(subscriptionId);
        System.out.println(azure);
    }

    private static void checkLogin(Thread azureLoginThread) {
        long startTime = System.currentTimeMillis();
        long duration = 0;
        while (duration < 120_000 && azureLoginThread.isAlive()) {
            System.out.println(AzureLogin.loginStatus);
            if (loginStatus) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            duration = System.currentTimeMillis() - startTime;
        }
        if (loginStatus) {
            //skal printes til bruger i vindue
            System.out.println("Login successful");
            LoginController.changeScene();
        }
        else {
            //skal printes til bruger i vindue
            System.out.println("Login not succeded. Try again");
            LoginController.changeButtonTryAgain();
        }
    }

}

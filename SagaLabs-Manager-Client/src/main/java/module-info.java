module com.example.sagalabsmanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.azure.resourcemanager.authorization;
    requires com.azure.resourcemanager.compute;
    requires com.microsoft.aad.msal4j;
    requires com.azure.security.keyvault.secrets;
    requires com.azure.core;
    requires java.desktop;
    requires org.apache.logging.log4j.slf4j;
    requires org.slf4j;
    requires jdk.unsupported;
    requires jdk.httpserver;
    requires com.azure.resourcemanager.resources;
    requires com.azure.core.management;
    requires com.azure.identity;
    requires org.apache.commons.net;
    requires com.azure.resourcemanager.appplatform;
    requires com.azure.resourcemanager.appservice;
    requires com.azure.resourcemanager;
    requires java.sql;
    requires com.google.gson;

    opens sagalabsmanagerclient to javafx.fxml;
    exports sagalabsmanagerclient;
    exports sagalabsmanagerclient.controllers;
    opens sagalabsmanagerclient.controllers to javafx.fxml;
}
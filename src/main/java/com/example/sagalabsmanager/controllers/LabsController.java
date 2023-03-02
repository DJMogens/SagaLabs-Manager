package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LabsController {

    public Label sqlsuccess, sqlfailed;
    public TextField sqlUsername, sqlURL;
    public PasswordField sqlPassword;

    public void listResourceGroupsWithLabTag() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listResourceGroupsWithLabTag(AzureLogin.azure);
    }
    public void getLabDetails() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listResourceGroupsWithLabTag(AzureLogin.azure);
    }

    public void logout(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.LOGIN);
    }
    public void home(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.LABS);
    }
    public void switchToMachine(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.MACHINES);
    }
    public void switchToSQL(ActionEvent event) throws IOException {
        ViewSwitcher.switchView(View.SQLSCENE);
    }

    public void ConnectToSQL(ActionEvent event) throws IOException, SQLException {
        SagaDB.sqlLoginCheck();
    }
}


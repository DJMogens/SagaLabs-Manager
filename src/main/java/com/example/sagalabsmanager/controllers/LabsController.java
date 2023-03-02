package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;

public class LabsController {
    @FXML
    public Label sqlstatus;
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
        if (SagaDB.sqlLoginCheck()){
            sqlstatus.setText("SQL connection: Working");
            sqlstatus.setTextFill(Color.GREEN);
        }
        else {
            sqlstatus.setText("SQL connection: Failed");
            sqlstatus.setTextFill(Color.RED);
        }
    }
}


package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.AzureMethods;
import com.example.sagalabsmanager.View;
import com.example.sagalabsmanager.ViewSwitcher;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LabsController {

    public Label sqlsuccess, sqlfailed;
    public TextField sqlUsername, sqlURL;
    public PasswordField sqlPassword;
    public String name, password, url;

    public void listResourceGroupsWithLabTag() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listResourceGroupsWithLabTag(LoginController.azure);
    }
    public void getLabDetails() {
        AzureMethods azureMethods = new AzureMethods();
        azureMethods.listResourceGroupsWithLabTag(LoginController.azure);
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

    public void ConnectToSQL(ActionEvent event) throws IOException {

        name = sqlUsername.getText();
        password = sqlPassword.getText();
        url = sqlURL.getText();

        if (sqlUsername.getText().isEmpty() || sqlPassword.getText().isEmpty() || sqlURL.getText().isEmpty()) {
            sqlfailed.setVisible(true);
            sqlsuccess.setVisible(false);
        } else {
            sqlfailed.setVisible(false);
            sqlsuccess.setVisible(true);
            sqlsuccess.setText("Successfully connected to " + name + "'s Database");
        }
    }
}

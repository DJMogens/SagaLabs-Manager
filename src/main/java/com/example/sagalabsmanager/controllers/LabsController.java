package com.example.sagalabsmanager.controllers;

import com.example.sagalabsmanager.AzureMethods;
import com.example.sagalabsmanager.View;
import com.example.sagalabsmanager.ViewSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class LabsController {

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
}

package com.example.sagalabsmanager.controllers;

import com.azure.resourcemanager.network.models.VM;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.AzureMethods;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;

public class VMsController extends MenuController {
    @FXML public TabPane tabPane;
    static ArrayList<ResourceGroup> allLabs; // Dynamically expanding list of tabs
    static ArrayList<Tab> tabs = new ArrayList<Tab>(); // Dynamically expanding list of tabs

    public void initialize() {
        initializeTabs();
    }

    private void initializeTabs() {
        allLabs = AzureMethods.getAllLabs(AzureLogin.azure);
        for(ResourceGroup lab: allLabs) {
            // Creates tab
            Tab tab = new Tab();
            tab.setText(lab.name().substring(0, 10));
            // Creates tableview under tab
            TableView<VM> tableView = new TableView<VM>();
            tab.setContent(tableView);
            // Creates columns in tableview
            initializeColumns(tableView);
            // Adds tab to pane and tabs array
            tabPane.getTabs().add(tab);
            tabs.add(tab);
        }
    }
    private void initializeColumns(TableView tableView) {
        TableColumn idColumn = new TableColumn("ID");
        idColumn.setPrefWidth(450.0);
        TableColumn vmColumn = new TableColumn("Machine Name");
        vmColumn.setPrefWidth(450.0);
        TableColumn osColumn = new TableColumn("OS");
        osColumn.setPrefWidth(199.0);
        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(vmColumn);
        tableView.getColumns().add(osColumn);
    }
}

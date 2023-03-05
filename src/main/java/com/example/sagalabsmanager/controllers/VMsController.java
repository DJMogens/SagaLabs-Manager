package com.example.sagalabsmanager.controllers;

import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.containerservice.models.OSType;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.AzureMethods;
import com.example.sagalabsmanager.MachinesLabTab;
import com.example.sagalabsmanager.MachinesVM;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;

public class VMsController extends MenuController {
    @FXML protected TabPane tabPane;
    @FXML protected Tab allTab;
    @FXML protected TableView<MachinesVM> allTableView;

    public static ArrayList<MachinesLabTab> labTabs = new ArrayList<MachinesLabTab>();

    public void initialize() {
        initializeTabs();
    }

    private void initializeTabs() {
        ArrayList<ResourceGroup> allLabs = AzureMethods.getAllLabs(AzureLogin.azure);
        MachinesLabTab allLabsTab = new MachinesLabTab(allTab, allTableView);
        labTabs.add(allLabsTab);
        for(ResourceGroup lab: allLabs) {
            // Creates tab
            Tab tab = new Tab();
            tab.setText(lab.name().substring(0, 10));
            // Creates tableview under tab
            TableView<MachinesVM> tableView = new TableView<MachinesVM>();
            tab.setContent(tableView);

            // Creates columns in tableview
            initializeColumns(tableView);
            // Adds tab to pane and tabs array
            tabPane.getTabs().add(tab);
            MachinesLabTab labTab = new MachinesLabTab(lab, tab, tableView);
            labTabs.add(labTab);
        }
        setTabSelectionAction();
    }

    private void setTabSelectionAction() {
        for(MachinesLabTab labTab: labTabs) {
           labTab.getTab().setOnSelectionChanged(e -> selectTab());
        }
    }

    private void initializeColumns(TableView<MachinesVM> tableView) {
        TableColumn<MachinesVM, String> idColumn = new TableColumn<MachinesVM, String>("ID");
        idColumn.setPrefWidth(450.0);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<MachinesVM, String> vmColumn = new TableColumn<MachinesVM, String>("Machine Name");
        vmColumn.setPrefWidth(450.0);
        vmColumn.setCellValueFactory(new PropertyValueFactory<>("vmName"));
        TableColumn<MachinesVM, OSType> osColumn = new TableColumn<MachinesVM, OSType>("OS");
        osColumn.setPrefWidth(199.0);
        osColumn.setCellValueFactory(new PropertyValueFactory<>("os"));
        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(vmColumn);
        tableView.getColumns().add(osColumn);
    }

    private void selectTab() {
        for(MachinesLabTab labTab: labTabs) {
            if(labTab.getTab().isSelected()) {
                // just for testing
                if(labTab.getResourceGroup() != null) {
                    for(VirtualMachine vm: AzureMethods.getVMsInLab(labTab.resourceGroup)) {
                        labTab.getTableView().getItems().add(new MachinesVM(vm.vmId(), vm.name(), vm.osType()));
                    }
                }
            }
        }
    }

}

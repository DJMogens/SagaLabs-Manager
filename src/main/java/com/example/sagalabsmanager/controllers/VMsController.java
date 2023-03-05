package com.example.sagalabsmanager.controllers;

import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.containerservice.models.OSType;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import com.example.sagalabsmanager.AzureLogin;
import com.example.sagalabsmanager.AzureMethods;
import com.example.sagalabsmanager.MachinesTab;
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

    public static ArrayList<MachinesTab> machinesTabs = new ArrayList<MachinesTab>();

    public void initialize() {
        System.out.println("Initializing tabs");
        initializeTabs();
    }

    private void initializeTabs() {
        // Creates tab for all
        machinesTabs.add(new MachinesTab(allTab, allTableView));
        showAllMachines();

        for(ResourceGroup lab: AzureMethods.getAllLabs(AzureLogin.azure)) {
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
            MachinesTab machinesTab = new MachinesTab(lab, tab, tableView);
            machinesTabs.add(machinesTab);
        }
        setTabSelectionAction();
    }

    private void setTabSelectionAction() {
        for(MachinesTab tab: machinesTabs) {
           tab.getTab().setOnSelectionChanged(e -> selectTab());
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
        for(MachinesTab machinesTab: machinesTabs) {
            if(machinesTab.getTab().isSelected()) {
                // For lab, when selected
                if(machinesTab.getResourceGroup() != null && machinesTab.getTableView().getItems().isEmpty()) {
                    for(VirtualMachine vm: AzureMethods.getVMsInLab(machinesTab.resourceGroup)) {
                        machinesTab.getTableView().getItems().add(new MachinesVM(vm.vmId(), vm.name(), vm.osType()));
                    }
                }
                // For 'ALL' tab
                else if (allTableView.getItems().isEmpty()) {
                    showAllMachines();
                }
            }
        }
    }
    private void showAllMachines() {
        for(ResourceGroup resourceGroup: AzureMethods.getAllLabs(AzureLogin.azure)) {
            for(VirtualMachine vm: AzureMethods.getVMsInLab(resourceGroup)) {
                System.out.println(vm.name());
                allTableView.getItems().add(new MachinesVM(vm.vmId(), vm.name(), vm.osType()));
            }
        }
    }
}

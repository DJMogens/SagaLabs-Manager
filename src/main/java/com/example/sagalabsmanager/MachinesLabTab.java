package com.example.sagalabsmanager;

import com.azure.resourcemanager.resources.models.ResourceGroup;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;

public class MachinesLabTab {

    public ResourceGroup resourceGroup;
    public Tab tab;
    public TableView<MachinesVM> tableView;

    public Tab getTab() {
        return this.tab;
    }
    public ResourceGroup getResourceGroup() {
        return this.resourceGroup;
    }
    public TableView<MachinesVM> getTableView() {
        return this.tableView;
    }
    public MachinesLabTab(ResourceGroup resourceGroup, Tab tab, TableView<MachinesVM> tableView) {
        this.resourceGroup = resourceGroup;
        this.tab = tab;
        this.tableView = tableView;
    }
    public MachinesLabTab(Tab tab, TableView<MachinesVM> tableView) {
        this(null, tab, tableView);
    }

}

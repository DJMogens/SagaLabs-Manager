package sagalabsmanagerclient;

import com.azure.resourcemanager.resources.models.ResourceGroup;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;

public class MachinesTab {

    public String resourceGroup;
    public Tab tab;
    public TableView<MachinesVM> tableView;

    public Tab getTab() {
        return this.tab;
    }
    public String getResourceGroup() {
        return this.resourceGroup;
    }
    public TableView<MachinesVM> getTableView() {
        return this.tableView;
    }
    public MachinesTab(String resourceGroup, Tab tab, TableView<MachinesVM> tableView) {
        this.resourceGroup = resourceGroup;
        this.tab = tab;
        this.tableView = tableView;
    }
    public MachinesTab(Tab tab, TableView<MachinesVM> tableView) {
        this(null, tab, tableView);
    }
}

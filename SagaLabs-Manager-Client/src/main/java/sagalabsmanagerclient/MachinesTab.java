package sagalabsmanagerclient;

import com.azure.resourcemanager.containerservice.models.OSType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MachinesTab {

    private final String resourceGroup;
    private final Tab tab;
    private final TableView<MachinesVM> tableView;

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
    public void initializeColumns() {
        TableColumn<MachinesVM, CheckBox> selectColumn = new TableColumn<>();
        selectColumn.setPrefWidth(50.0);
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));

        TableColumn<MachinesVM, String> idColumn = new TableColumn<>("ID");
        idColumn.setPrefWidth(20.0);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<MachinesVM, String> vmColumn = new TableColumn<>("Machine Name");
        vmColumn.setPrefWidth(250.0);
        vmColumn.setCellValueFactory(new PropertyValueFactory<>("vmName"));

        TableColumn<MachinesVM, OSType> osColumn = new TableColumn<>("OS");
        osColumn.setPrefWidth(100.0);
        osColumn.setCellValueFactory(new PropertyValueFactory<>("os"));

        TableColumn<MachinesVM, String> stateColumn = new TableColumn<>("State");
        stateColumn.setPrefWidth(99.0);
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));

        TableColumn<MachinesVM, String> rgColumn = new TableColumn<>("Resource Group");
        rgColumn.setPrefWidth(200.0);
        rgColumn.setCellValueFactory(new PropertyValueFactory<>("resourceGroup"));

        TableColumn<MachinesVM, String> ipColumn = new TableColumn<>("IP Address");
        ipColumn.setPrefWidth(200.0);
        ipColumn.setCellValueFactory(new PropertyValueFactory<>("ip"));

        tableView.getColumns().add(selectColumn);
        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(vmColumn);
        tableView.getColumns().add(osColumn);
        tableView.getColumns().add(stateColumn);
        tableView.getColumns().add(ipColumn);
        tableView.getColumns().add(rgColumn);
    }

}

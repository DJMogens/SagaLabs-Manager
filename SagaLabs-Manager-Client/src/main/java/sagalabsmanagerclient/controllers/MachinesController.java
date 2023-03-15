package sagalabsmanagerclient.controllers;

import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.containerservice.models.OSType;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import org.w3c.dom.Text;
import sagalabsmanagerclient.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.stream.Stream;

public class MachinesController extends MenuController {
    @FXML protected TabPane tabPane;
    @FXML protected Tab allTab;
    @FXML protected TableView<MachinesVM> allTableView;
    @FXML protected TextField osFilterText;
    @FXML protected TextField stateFilterText;
    @FXML protected Button filterButton;

    public static ArrayList<MachinesTab> machinesTabs = new ArrayList<MachinesTab>();

    public void initialize() throws SQLException {
        initializeTabs();
    }

    private void initializeTabs() throws SQLException {
        // Creates tab for all
        if(machinesTabs.isEmpty()) {
            MachinesTab machinesAllTab = new MachinesTab(allTab, allTableView);
            machinesTabs.add(machinesAllTab);
            selectTab(machinesAllTab);
            for(String resourceGroup: Database.getResourceGroups()) {
                Tab tab = new Tab();
                tab.setText(resourceGroup.substring(0, 10));
                // Creates tableview under tab
                TableView<MachinesVM> tableView = new TableView<MachinesVM>();
                tab.setContent(tableView);

                // Creates columns in tableview
                initializeColumns(tableView);
                // Adds tab to pane and tabs array
                tabPane.getTabs().add(tab);
                MachinesTab machinesTab = new MachinesTab(resourceGroup, tab, tableView);
                machinesTabs.add(machinesTab);
            }
            setTabSelectionAction();
        }
        else {
            machinesTabs.set(0, new MachinesTab(allTab, allTableView));
            selectTab(machinesTabs.get(0));
            System.out.println(machinesTabs.get(0).getTableView().getItems());
            for(MachinesTab tab: machinesTabs.subList(1, machinesTabs.size())) {
                tab.getTab().setContent(tab.getTableView());
                initializeColumns(tab.getTableView());
                tabPane.getTabs().add(tab.getTab());
                setTabSelectionAction();
            }
        }
    }

    private void setTabSelectionAction() {
        for(MachinesTab tab: machinesTabs) {
            tab.getTab().setOnSelectionChanged(e -> {
                try {
                    selectTab(tab);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    private void initializeColumns(TableView<MachinesVM> tableView) {
        TableColumn<MachinesVM, CheckBox> selectColumn = new TableColumn<MachinesVM, CheckBox>();
        selectColumn.setPrefWidth(50.0);
        selectColumn.setCellValueFactory(new PropertyValueFactory<>("select"));

        TableColumn<MachinesVM, String> idColumn = new TableColumn<MachinesVM, String>("ID");
        idColumn.setPrefWidth(20.0);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<MachinesVM, String> vmColumn = new TableColumn<MachinesVM, String>("Machine Name");
        vmColumn.setPrefWidth(250.0);
        vmColumn.setCellValueFactory(new PropertyValueFactory<>("vmName"));

        TableColumn<MachinesVM, OSType> osColumn = new TableColumn<MachinesVM, OSType>("OS");
        osColumn.setPrefWidth(100.0);
        osColumn.setCellValueFactory(new PropertyValueFactory<>("os"));

        TableColumn<MachinesVM, String> stateColumn = new TableColumn<MachinesVM, String>("State");
        stateColumn.setPrefWidth(99.0);
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));

        TableColumn<MachinesVM, String> rgColumn = new TableColumn<MachinesVM, String>("Resource Group");
        rgColumn.setPrefWidth(200.0);
        rgColumn.setCellValueFactory(new PropertyValueFactory<>("resourceGroup"));

        tableView.getColumns().add(selectColumn);
        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(vmColumn);
        tableView.getColumns().add(rgColumn);
        tableView.getColumns().add(osColumn);
        tableView.getColumns().add(stateColumn);
    }

    private void selectTab(MachinesTab machinesTab) throws SQLException {
        if(machinesTab.getTab().getText().equals("All")) {
            if(machinesTab.getTableView().getItems().isEmpty()) {
                String resourceGroupName;
                resourceGroupName = machinesTab.resourceGroup;
                FilteredList<MachinesVM> list = new FilteredList<MachinesVM>(
                        FXCollections.observableArrayList(Database.getMachines(resourceGroupName)),
                        null);
                machinesTab.getTableView().setItems(list);
            }
            else {
                FilteredList<MachinesVM> list = (FilteredList<MachinesVM>) machinesTabs.get(0).getTableView().getItems();
                list.setPredicate(null);
            }
        } else {
            FilteredList<MachinesVM> list = (FilteredList<MachinesVM>) machinesTabs.get(0).getTableView().getItems();
            list.setPredicate(e -> e.getResourceGroup().equals(machinesTab.getResourceGroup()));
            machinesTab.getTableView().setItems(list);
        }
    }

    private MachinesTab getCurrentTab() {
        MachinesTab machinesTab = new MachinesTab(null, null, null);
        for(MachinesTab tab: machinesTabs) {
            if(tab.getTab().isSelected()) {
                machinesTab = tab;
                break;
            }
        }
        return machinesTab;
    }

    // METHOD TO RETURN VMs WITH CHECKMARK. Currently returns MachinesVM objects.
    private ArrayList<MachinesVM> getSelectedVMs() {
        MachinesTab machinesTab = machinesTabs.get(0); // By default all tab
        ArrayList<MachinesVM> machineList = new ArrayList<MachinesVM>();
        machinesTab = getCurrentTab();
        for (MachinesVM vm :machinesTab.getTableView().getItems()) {
            if(vm.getSelected()) {
                machineList.add(vm);
            }
        }
        return machineList;
    }

    public void applyFilter(ActionEvent actionEvent) throws SQLException {
        MachinesTab tab = getCurrentTab();

        String osFilter = osFilterText.getText();
        String finalOsFilter = osFilter.replaceAll("\\s", "");
        String stateFilter = stateFilterText.getText();
        String finalStateFilter = stateFilter.replaceAll("\\s", "");

        Predicate<MachinesVM> test = null;
        FilteredList<MachinesVM> VMs = null;
        if(!finalOsFilter.equals("")) {
            test = e -> e.getOs().equalsIgnoreCase(finalOsFilter);
        }
        VMs = new FilteredList<MachinesVM>(tab.getTableView().getItems(), test);
        tab.getTableView().setItems(VMs);

        if(!finalStateFilter.equals("")) {
            test = e -> e.getState().equalsIgnoreCase(finalStateFilter);
        }
        VMs = new FilteredList<MachinesVM>(tab.getTableView().getItems(), test);
        tab.getTableView().setItems(VMs);

        System.out.println("after filtering " + tab.getTableView().getItems().size());
    }

    public void resetFilters(ActionEvent actionEvent) throws SQLException {
        System.out.println("Resetting filters for tab");

        stateFilterText.setText("");
        osFilterText.setText("");

        MachinesTab tab = machinesTabs.get(0);

        // Complicated way of emptying tableview. Will filter on empty OS -> delete all.
        ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(e -> e.getOs().isEmpty());

        selectTab(tab);
        selectTab(getCurrentTab());
    }
}

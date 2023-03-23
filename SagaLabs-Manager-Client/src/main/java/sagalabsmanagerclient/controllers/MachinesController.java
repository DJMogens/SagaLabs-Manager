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
    public Button machinesTurnOn;
    public Button machinesTurnOff;
    @FXML protected TabPane tabPane;
    @FXML protected Tab allTab;
    @FXML protected TableView<MachinesVM> allTableView;
    @FXML public TextField osFilterText;
    @FXML public TextField stateFilterText;
    @FXML public TextField nameFilterText;
    @FXML public TextField ipFilterText;
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
        else { // If tabs already exist
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

        TableColumn<MachinesVM, String> ipColumn = new TableColumn<MachinesVM, String>("IP Address");
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

    private void selectTab(MachinesTab machinesTab) throws SQLException {
        // For tab "All"
        if(machinesTab.getTab().getText().equals("All") && machinesTab.getTableView().getItems().isEmpty()) {
            String resourceGroupName;
            resourceGroupName = machinesTab.resourceGroup;
            FilteredList<MachinesVM> list = new FilteredList<MachinesVM>(
                    FXCollections.observableArrayList(Database.getMachines(resourceGroupName)),
                    null);
            machinesTab.getTableView().setItems(list);
        } else { // For any other tab (specific lab)
            FilteredList<MachinesVM> newList = (FilteredList<MachinesVM>) machinesTabs.get(0).getTableView().getItems();
            machinesTab.getTableView().setItems(newList);
        }
        applyFilter(new ActionEvent());
    }

    public MachinesTab getCurrentTab() {
        return machinesTabs.get(tabPane.getSelectionModel().getSelectedIndex());
    }

    // METHOD TO RETURN VMs WITH CHECKMARK. Currently returns MachinesVM objects.
    private ArrayList<MachinesVM> getSelectedVMs() {
        MachinesTab machinesTab = machinesTabs.get(0); // By default all tab
        ArrayList<MachinesVM> machineList = new ArrayList<MachinesVM>();
        machinesTab = getCurrentTab();
        for (MachinesVM vm : machinesTab.getTableView().getItems()) {
            if (vm.getSelected()) {
                System.out.println("Selected VM ID: " + vm.getId() + ", State: " + vm.getState());
                machineList.add(vm);
            }
        }
        return machineList;
    }

    public void applyFilter(ActionEvent actionEvent) throws SQLException {
        MachinesTab tab = getCurrentTab();

        String osFilter = osFilterText.getText().replaceAll("\\s", "");;
        String stateFilter = stateFilterText.getText().replaceAll("\\s", "");;
        String nameFilter = nameFilterText.getText().replaceAll("\\s", "");;
        String ipFilter = ipFilterText.getText().replaceAll("\\s", "");;

        Predicate<MachinesVM> osPredicate = vm -> (osFilter.isEmpty() || vm.getOs().equalsIgnoreCase(osFilter));
        Predicate<MachinesVM> statePredicate = vm -> (stateFilter.isEmpty() || vm.getState().equalsIgnoreCase(stateFilter));
        Predicate<MachinesVM> namePredicate = vm -> (nameFilter.isEmpty() || vm.getVmName().contains(nameFilter));
        Predicate<MachinesVM> ipPredicate = vm -> (ipFilter.isEmpty() || vm.getIp().startsWith(ipFilter));
        Predicate<MachinesVM> rgPredicate = vm -> (tab.getTab().getText().equals("All") || vm.getResourceGroup().equals(tab.getResourceGroup()));

        ((FilteredList<MachinesVM>) tab.getTableView().getItems()).setPredicate(osPredicate.and(statePredicate).and(namePredicate).and(ipPredicate).and(rgPredicate));
    }

    public void resetFilters(ActionEvent e) throws SQLException {
        stateFilterText.setText("");
        osFilterText.setText("");
        nameFilterText.setText("");
        ipFilterText.setText("");
        applyFilter(e);
    }

    @FXML
    public void handleTurnOn(ActionEvent e) {
        ArrayList<MachinesVM> selectedVMs = getSelectedVMs();
        // Call the method to turn on the selected VMs
        Runnable turnOnTask = () -> AzureMethods.turnOnVMs(selectedVMs);
        new Thread(turnOnTask).start();

        System.out.println("Selected VMs: " + selectedVMs);
    }

    @FXML
    public void handleTurnOff(ActionEvent e) {
        ArrayList<MachinesVM> selectedVMs = getSelectedVMs();
        // Call the method to turn off the selected VMs
        Runnable turnOffTask = () -> AzureMethods.deallocateVMs(selectedVMs);
        new Thread(turnOffTask).start();
        System.out.println("Selected VMs: " + selectedVMs);
    }
}

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
    public Button runScriptButton;
    public TextArea scriptField;
    @FXML protected TabPane tabPane;
    @FXML protected Tab allTab;
    @FXML protected TableView<MachinesVM> allTableView;
    @FXML private TextField osFilterText;
    @FXML private TextField stateFilterText;
    @FXML private TextField nameFilterText;
    @FXML private TextField ipFilterText;

    private static ArrayList<MachinesTab> machinesTabs = new ArrayList<MachinesTab>();

    public void initialize() throws SQLException {
        initializeTabs();
    }

    private void initializeTabs() throws SQLException {
        // Creates tab for all
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
            // Adds tab to pane and tabs array
            tabPane.getTabs().add(tab);
            MachinesTab machinesTab = new MachinesTab(resourceGroup, tab, tableView);
            machinesTab.initializeColumns();
            machinesTabs.add(machinesTab);
        }
        setTabSelectionAction();
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

    private void selectTab(MachinesTab machinesTab) throws SQLException {
        String resourceGroupName = machinesTab.getResourceGroup();
        FilteredList<MachinesVM> list = new FilteredList<MachinesVM>(
                FXCollections.observableArrayList(Database.getMachines(resourceGroupName)),
                null);
        machinesTab.getTableView().setItems(list);
        applyFilter(new ActionEvent());
    }

    public MachinesTab getCurrentTab() {
        return machinesTabs.get(tabPane.getSelectionModel().getSelectedIndex());
    }

    // METHOD TO RETURN VMs WITH CHECKMARK. Currently returns MachinesVM objects.
    private ArrayList<MachinesVM> getSelectedVMs() {
        ArrayList<MachinesVM> machineList = new ArrayList<MachinesVM>();
        MachinesTab machinesTab = getCurrentTab();
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

        FilteredList<MachinesVM> list = new FilteredList<MachinesVM>(FXCollections.observableArrayList(tab.getTableView().getItems()), osPredicate.and(statePredicate).and(namePredicate).and(ipPredicate).and(rgPredicate));
        tab.getTableView().setItems(list);

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

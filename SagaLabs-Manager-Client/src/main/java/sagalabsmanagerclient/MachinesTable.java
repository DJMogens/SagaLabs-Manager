package sagalabsmanagerclient;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.function.Predicate;

public class MachinesTable {
    private final TabPane tabPane;
    private final ArrayList<MachinesTab> machinesTabs = new ArrayList<>();
    private final ArrayList<MachinesVM> previousMachines = new ArrayList<>();

    public MachinesTable(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public ArrayList<MachinesTab> getMachinesTabs() {
        return machinesTabs;
    }
    public void createTab(String resourceGroup) {
        Tab tab = new Tab();
        tab.setText(resourceGroup.substring(0, 10));
        // Creates tableview under tab
        TableView<MachinesVM> tableView = new TableView<>();
        tab.setContent(tableView);
        // Adds tab to pane and tabs array
        tabPane.getTabs().add(tab);
        MachinesTab machinesTab = new MachinesTab(resourceGroup, tab, tableView);
        machinesTab.initializeColumns();
        machinesTabs.add(machinesTab);
    }

    public void initializeTabs(Tab allTab, TableView<MachinesVM> allTableView) throws SQLException {
        // Creates tab for all
        MachinesTab machinesAllTab = new MachinesTab(allTab, allTableView);
        machinesTabs.add(machinesAllTab);
        selectTab(machinesAllTab);
        for(String resourceGroup: Database.getResourceGroups()) {
            createTab(resourceGroup);
        }
    }

    public void addNewLabs() throws SQLException {
        labLoop:
        for(String resourceGroup: Database.getResourceGroups()) {
            tabLoop:
            for(MachinesTab tab: machinesTabs) {
                try {
                    if (tab.getResourceGroup().equals(resourceGroup)) {
                        continue labLoop;
                    }
                }
                catch(NullPointerException e) {
                }
            }
            createTab(resourceGroup);
        }
    }
    public void selectTab(MachinesTab machinesTab) throws SQLException {
        String resourceGroupName = machinesTab.getResourceGroup();
        FilteredList<MachinesVM> list = new FilteredList<>(
                FXCollections.observableArrayList(Database.getMachines(resourceGroupName)),
                null);
        machinesTab.getTableView().setItems(list);
    }
    public MachinesTab getCurrentTab() {
        return machinesTabs.get(tabPane.getSelectionModel().getSelectedIndex());
    }

    public ArrayList<MachinesVM> getSelectedVMs() {
        ArrayList<MachinesVM> machineList = new ArrayList<>();
        MachinesTab machinesTab = getCurrentTab();
        for (MachinesVM vm : machinesTab.getTableView().getItems()) {
            if (vm.getSelected()) {
                System.out.println("Selected VM ID: " + vm.getId() + ", State: " + vm.getState());
                machineList.add(vm);
            }
        }
        return machineList;
    }
    public void applyFilter(String osFilter, String stateFilter, String nameFilter, String ipFilter) {
        MachinesTab tab = getCurrentTab();

        Predicate<MachinesVM> osPredicate = vm -> (osFilter.isEmpty() || vm.getOs().equalsIgnoreCase(osFilter));
        Predicate<MachinesVM> statePredicate = vm -> (stateFilter.isEmpty() || vm.getState().equalsIgnoreCase(stateFilter));
        Predicate<MachinesVM> namePredicate = vm -> (nameFilter.isEmpty() || vm.getVmName().toLowerCase().contains(nameFilter.toLowerCase())); //set both to lowercase to ignore the casing
        Predicate<MachinesVM> ipPredicate = vm -> (ipFilter.isEmpty() || vm.getIp().startsWith(ipFilter));
        Predicate<MachinesVM> rgPredicate = vm -> (tab.getTab().getText().equals("All") || vm.getResourceGroup().equals(tab.getResourceGroup()));

        FilteredList<MachinesVM> list = new FilteredList<>(FXCollections.observableArrayList(tab.getTableView().getItems()), osPredicate.and(statePredicate).and(namePredicate).and(ipPredicate).and(rgPredicate));
        tab.getTableView().setItems(list);
    }
    public void setPreviousMachines() {
        previousMachines.addAll(getCurrentTab().getTableView().getItems());
    }
    public void setSelectedMachines() {
        for(MachinesVM newVM: getCurrentTab().getTableView().getItems()) {
            for(MachinesVM previousVM: previousMachines) {
                if(newVM.getVmName().equals(previousVM.getVmName())) {
                    newVM.getSelect().setSelected(previousVM.getSelected());
                }
            }
        }
        previousMachines.clear();
    }
}

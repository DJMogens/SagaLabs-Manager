package sagalabsmanagerclient.controllers;

import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.containerservice.models.OSType;
import com.azure.resourcemanager.resources.models.ResourceGroup;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import org.w3c.dom.Text;
import sagalabsmanagerclient.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.stream.Stream;

public class MachinesController extends MenuController {
    @FXML protected Button runScriptButton;
    @FXML protected TextArea scriptField, scriptOutputField;
    @FXML protected TabPane tabPane;
    @FXML protected Tab allTab;
    @FXML protected TableView<MachinesVM> allTableView;
    @FXML protected TextField osFilterText, stateFilterText, nameFilterText, ipFilterText;
    private final AzureMethods azureMethods = new AzureMethods();
    private MachinesTable machinesTable;

    public void initialize() throws SQLException {
        machinesTable = new MachinesTable(tabPane);
        machinesTable.initializeTabs(allTab, allTableView);
        applyFilter(new ActionEvent());
        setTabSelectionAction();

        super.initialize();
    }
    public void refresh() throws SQLException {
        // Makes copy of current machines to set checkmarks again
        machinesTable.setPreviousMachines();
        // Refreshes machines
        machinesTable.selectTab(machinesTable.getCurrentTab());
        applyFilter(new ActionEvent());
        // Resets checkmarks on "new" machines
        machinesTable.setSelectedMachines();
        // Checks if new labs exist and adds to tableview
        machinesTable.addNewLabs();
    }
    public void setTabSelectionAction() {
        for(MachinesTab tab: machinesTable.getMachinesTabs()) {
            tab.getTab().setOnSelectionChanged(e -> {
                try {
                    machinesTable.selectTab(tab);
                    applyFilter(new ActionEvent());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }
    public void applyFilter(ActionEvent e) throws SQLException {
        String osFilter = osFilterText.getText().replaceAll("\\s", "");;
        String stateFilter = stateFilterText.getText().replaceAll("\\s", "");;
        String nameFilter = nameFilterText.getText().replaceAll("\\s", "");;
        String ipFilter = ipFilterText.getText().replaceAll("\\s", "");;
        machinesTable.applyFilter(osFilter, stateFilter, nameFilter, ipFilter);
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
        ArrayList<MachinesVM> selectedVMs = machinesTable.getSelectedVMs();
        // Call the method to turn on the selected VMs
        Runnable turnOnTask = () -> azureMethods.turnOnVMs(selectedVMs);
        new Thread(turnOnTask).start();

        System.out.println("Selected VMs: " + selectedVMs);
    }

    @FXML
    public void handleTurnOff(ActionEvent e) {
        ArrayList<MachinesVM> selectedVMs = machinesTable.getSelectedVMs();
        // Call the method to turn off the selected VMs
        Runnable turnOffTask = () -> azureMethods.deallocateVMs(selectedVMs);
        new Thread(turnOffTask).start();
        System.out.println("Selected VMs: " + selectedVMs);
    }

    @FXML
    public void handleRunScript(ActionEvent e) {
        String output = azureMethods.runScript(machinesTable.getSelectedVMs(), scriptField.getText());
        scriptOutputField.setText("");
        scriptOutputField.appendText(output);
    }


}

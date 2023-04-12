package sagalabsmanagerclient.controllers;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import sagalabsmanagerclient.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class MachinesController extends MenuController {
    @FXML protected Button runScriptButton;
    @FXML protected TextArea scriptField, scriptOutputField;
    @FXML protected TabPane tabPane;
    @FXML protected Tab allTab;
    @FXML protected TableView<MachinesVM> allTableView;
    @FXML protected TextField osFilterText, stateFilterText, nameFilterText, ipFilterText;
    private final AzureMethods azureMethods = new AzureMethods();
    private MachinesTable machinesTable;
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);


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
        String osFilter = osFilterText.getText().replaceAll("\\s", "");
        String stateFilter = stateFilterText.getText().replaceAll("\\s", "");
        String nameFilter = nameFilterText.getText().replaceAll("\\s", "");
        String ipFilter = ipFilterText.getText().replaceAll("\\s", "");
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
    public void handleTurnOn() {
        ArrayList<MachinesVM> selectedVMs = machinesTable.getSelectedVMs();
        // Call the method to turn on the selected VMs
        Runnable turnOnTask = () -> azureMethods.turnOnVMs(selectedVMs);
        new Thread(turnOnTask).start();

        System.out.println("Selected VMs: " + selectedVMs);
    }

    @FXML
    public void handleTurnOff() {
        ArrayList<MachinesVM> selectedVMs = machinesTable.getSelectedVMs();
        // Call the method to turn off the selected VMs
        Runnable turnOffTask = () -> azureMethods.deallocateVMs(selectedVMs);
        new Thread(turnOffTask).start();
        System.out.println("Selected VMs: " + selectedVMs);
    }

    private boolean validateOperatingSystems(ArrayList<MachinesVM> selectedVMs) {
        if (selectedVMs.isEmpty()) {
            return false;
        }

        String firstOS = selectedVMs.get(0).getOs();
        for (MachinesVM vm : selectedVMs) {
            if (!firstOS.equalsIgnoreCase(vm.getOs())) {
                return false;
            }
        }
        return true;
    }

    private boolean validateMachineStates(ArrayList<MachinesVM> selectedVMs) {
        for (MachinesVM vm : selectedVMs) {
            if (!"running".equalsIgnoreCase(vm.getState())) {
                return false;
            }
        }
        return true;
    }

    @FXML
    public void handleRunScript() {
        ArrayList<MachinesVM> selectedVMs = machinesTable.getSelectedVMs();

        if (!validateOperatingSystems(selectedVMs)) {
            scriptOutputField.clear();
            scriptOutputField.setStyle("-fx-text-fill: red;"); // Set the text color to red
            scriptOutputField.setText("Error: All highlighted machines must have the same operating system (Windows or Linux).");
            return;
        }

        if (!validateMachineStates(selectedVMs)) {
            scriptOutputField.clear();
            scriptOutputField.setStyle("-fx-text-fill: red;"); // Set the text color to red
            scriptOutputField.setText("Error: All highlighted machines must be in the 'running' state.");
            return;
        }

        scriptOutputField.setStyle("-fx-text-fill: white;"); // Set the text color back to default

        isLoading.set(true);
        startLoadingAnimation();

        Runnable runScriptRunnable = () -> {
            String output = azureMethods.runScript(selectedVMs, scriptField.getText());
            Platform.runLater(() -> {
                isLoading.set(false);
                scriptOutputField.setText("");
                scriptOutputField.appendText(output);
            });
        };
        new Thread(runScriptRunnable).start();
    }

    private void startLoadingAnimation() {
        Task<Void> loadingTask = new Task<Void>() {
            @Override
            protected Void call() {
                int dotCount = 0;
                while (isLoading.get()) {
                    final int currentDotCount = dotCount;
                    Platform.runLater(() -> {
                        StringBuilder loadingText = new StringBuilder("Loading");
                        for (int i = 0; i < currentDotCount; i++) {
                            loadingText.append(".");
                        }
                        scriptOutputField.setText(loadingText.toString());
                    });

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        if (isCancelled()) {
                            break;
                        }
                    }
                    dotCount = (dotCount + 1) % 4;
                }
                return null;
            }
        };

        isLoading.set(true);
        new Thread(loadingTask).start();
    }


}

package sagalabsmanagerclient.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sagalabsmanagerclient.MachinesTab;
import sagalabsmanagerclient.MachinesTable;
import sagalabsmanagerclient.MachinesVM;
import sagalabsmanagerclient.VMSnapshot;

import java.sql.SQLException;
import java.util.ArrayList;

public class CaptureLabController{
    public Button Capture_Confirm;
    public TextField TextFieldVmGalleryNew;
    public Button filterButton;
    public Button resetFilterButton;
    public TextField TextFieldVmImageVersion;
    public ProgressIndicator progressIndicator;
    private String labName;
    private MachinesTable machinesTable;
    @FXML protected TabPane tabPane;
    @FXML protected TextField nameFilterText, ipFilterText;
    @FXML protected ChoiceBox<String> osFilterChoice, stateFilterChoice;

    @FXML
    public void initialize() {
        try {
            machinesTable = new MachinesTable(tabPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectAllCheckboxes() {
        TableView<MachinesVM> currentTableView = machinesTable.getCurrentTab().getTableView();
        for (MachinesVM machine : currentTableView.getItems()) {
            CheckBox checkBox = (CheckBox) currentTableView.getColumns().get(0).getCellObservableValue(machine).getValue();
            checkBox.setSelected(true);
        }
    }



    public void setLabData(String labName) {
        try {
            this.labName = labName;
            machinesTable.createTab(labName);
            machinesTable.selectTab(machinesTable.getCurrentTab());
            applyFilter("", "", "", "");
            selectAllCheckboxes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void applyFilter(String osFilter, String stateFilter, String nameFilter, String ipFilter) {
        machinesTable.applyFilter(osFilter, stateFilter, nameFilter, ipFilter);
    }

    public void applyFilter(ActionEvent e) throws SQLException {
        String osFilter = osFilterChoice.getValue() != null ? osFilterChoice.getValue().toString().replaceAll("\\s", "") : "";
        String stateFilter = stateFilterChoice.getValue() != null ? stateFilterChoice.getValue().toString().replaceAll("\\s", "") : "";
        String nameFilter = nameFilterText.getText().replaceAll("\\s", "");
        String ipFilter = ipFilterText.getText().replaceAll("\\s", "");
        machinesTable.applyFilter(osFilter, stateFilter, nameFilter, ipFilter);
    }

    public void resetFilters(ActionEvent e) throws SQLException {
        stateFilterChoice.setValue("");
        osFilterChoice.setValue("");
        nameFilterText.setText("");
        ipFilterText.setText("");
        applyFilter(e);
    }

    public void captureCheckedVMImages(ActionEvent event) {
        progressIndicator.setVisible(true);
        String galleryName = TextFieldVmGalleryNew.getText().trim();
        ArrayList<MachinesVM> selectedVMs = getSelectedVMs();
        String imageVersion = TextFieldVmImageVersion.getText();

        Task<Void> captureTask = new Task<Void>() {
            @Override
            protected Void call() {
                VMSnapshot.takeSnapshots(selectedVMs, imageVersion, galleryName, () -> {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Capture Started");
                        alert.setHeaderText(null);
                        alert.setContentText("Capture process has started for all machines. You may now close the window. The capture may take up to 15 minutes to complete.");
                        alert.showAndWait();
                    });
                });
                return null;
            }
        };


        captureTask.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                // Show feedback to the user, e.g., using an Alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Capture Completed");
                alert.setHeaderText(null);
                alert.setContentText("The lab has been captured successfully.");
                alert.showAndWait();
            });
        });

        captureTask.setOnFailed(e -> {
            Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                // Show error message to the user, e.g., using an Alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Capture Failed");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while capturing the lab.");
                alert.showAndWait();
            });
        });

        new Thread(captureTask).start();
    }



    public ArrayList<MachinesVM> getSelectedVMs() {
        ArrayList<MachinesVM> machineList = new ArrayList<>();
        MachinesTab machinesTab = machinesTable.getCurrentTab();
        for (MachinesVM vm : machinesTab.getTableView().getItems()) {
            if (vm.getSelect().isSelected()) {
                machineList.add(vm);
            }
        }
        return machineList;
    }


    public MachinesTable getMachinesTable() {
        return machinesTable;
    }

    public String getLabName() {
        return labName;
    }


}

package sagalabsmanagerclient.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sagalabsmanagerclient.MachinesTable;
import sagalabsmanagerclient.MachinesVM;

import java.sql.SQLException;

public class CaptureLabController {
    public Button Capture_Confirm;
    public TextField TextFieldVmGalleryNew;
    public ChoiceBox choiceBoxVmGallery;
    public Button filterButton;
    public Button resetFilterButton;
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
}

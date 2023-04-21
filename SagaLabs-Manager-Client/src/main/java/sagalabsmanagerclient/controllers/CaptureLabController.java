package sagalabsmanagerclient.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import sagalabsmanagerclient.MachinesTable;

import java.sql.SQLException;

public class CaptureLabController {
    private String labName;
    private MachinesTable machinesTable;

    @FXML
    private TabPane tabPane;

    @FXML
    public void initialize() {
        try {
            machinesTable = new MachinesTable(tabPane);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLabData(String labName) {
        try {
            this.labName = labName;
            machinesTable.createTab(labName);
            machinesTable.selectTab(machinesTable.getCurrentTab());
            applyFilter("", "", "", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void applyFilter(String osFilter, String stateFilter, String nameFilter, String ipFilter) {
        machinesTable.applyFilter(osFilter, stateFilter, nameFilter, ipFilter);
    }
}

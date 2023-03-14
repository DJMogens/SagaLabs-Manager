package sagalabsmanagerclient.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sagalabsmanagerclient.VPNServiceConnection;
import sagalabsmanagerclient.View;
import sagalabsmanagerclient.ViewSwitcher;

import java.sql.SQLException;
import java.util.ArrayList;

public class VPNController extends MenuController {
    @FXML
    public static void changeScene() {
        ViewSwitcher.switchView(View.VPN);
    }

    @FXML
    private TableView<JsonObject> userVpnTableView;

    public void listVpn(ActionEvent actionEvent) throws SQLException {
        VPNServiceConnection.getVPNUserInformation();
        listVpn();
    }

    public void listVpn() throws SQLException {
        VPNServiceConnection.getVPNUserInformation();
        // Get the list of JSON arrays
        ArrayList<JsonObject> jsonArrayList = VPNServiceConnection.vpnUserJsonList;

        // Create an ObservableList to hold the JSON objects
        ObservableList<JsonObject> jsonObservableList = FXCollections.observableArrayList();

        // Add each JSON object to the ObservableList
        jsonObservableList.addAll(jsonArrayList);

        // Set the items for the TableView
        userVpnTableView.setItems(jsonObservableList);

        // Define the columns for the TableView
        TableColumn<JsonObject, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getAsJsonArray("vpnUsers")
                .get(0)
                .getAsJsonObject()
                .get("Identity")
                .getAsString()));

        TableColumn<JsonObject, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getAsJsonArray("vpnUsers")
                .get(0)
                .getAsJsonObject()
                .get("AccountStatus")
                .getAsString()));

        TableColumn<JsonObject, String> onlineColumn = new TableColumn<>("online?");
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .getAsJsonArray("vpnUsers")
                .get(0)
                .getAsJsonObject()
                .get("Connections")
                .getAsString()));

        // Add the columns to the TableView
        userVpnTableView.getColumns().addAll(nameColumn, statusColumn, onlineColumn);
    }

}

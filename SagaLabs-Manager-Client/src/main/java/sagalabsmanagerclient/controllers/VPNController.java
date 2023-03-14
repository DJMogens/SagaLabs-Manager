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

    @FXML
    private TableColumn<JsonObject, String> userVPNName;

    @FXML
    private TableColumn<JsonObject, String> userVPNStatus;

    @FXML
    private TableColumn<JsonObject, String> userVPNOnline;

    public void initialize() {
        // Initialize the columns for the TableView
        userVPNName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .get("Identity")
                .getAsString()));

        userVPNStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .get("AccountStatus")
                .getAsString()));

        userVPNOnline.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .get("Connections")
                .getAsString()));
    }

    public void listVpn(ActionEvent actionEvent) throws SQLException {
        VPNServiceConnection.getVPNUserInformation();
        listVpn();
    }

    public void listVpn() throws SQLException {
        VPNServiceConnection.getVPNUserInformation();
        // Get the list of JSON arrays
        ArrayList<JsonObject> jsonArrayList = VPNServiceConnection.vpnUserJsonList;

        // Create an ObservableList to hold the VPN users
        ObservableList<JsonObject> vpnUsers = FXCollections.observableArrayList();

        // Iterate over each JSON object
        jsonArrayList.forEach(jsonObject -> {
            // Get the jsonArray for vpnUsers
            JsonArray vpnUsersArray = jsonObject.getAsJsonArray("vpnUsers");

            // Iterate over each object in vpnUsers and add it to the ObservableList
            vpnUsersArray.forEach(jsonElement -> {
                vpnUsers.add(jsonElement.getAsJsonObject());
            });
        });

        // Set the items for the TableView
        userVpnTableView.setItems(vpnUsers);
    }
}

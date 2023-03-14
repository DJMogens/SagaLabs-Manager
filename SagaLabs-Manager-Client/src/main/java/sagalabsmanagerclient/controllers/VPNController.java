package sagalabsmanagerclient.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
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

    @FXML
    private TableColumn<JsonObject, String> userVPNButtons;




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

        // Create the CellFactory for the userVPNButtons column
        userVPNButtons.setCellFactory(col -> {
            return new TableCell<JsonObject, String>() {
                final Button revokeBtn = new Button("Revoke");
                final Button downloadBtn = new Button("Download");
                final Button deleteBtn = new Button("Delete");
                final Button rotateBtn = new Button("Rotate");
                final Button unrevokeBtn = new Button("Unrevoke");

                final HBox hbox = new HBox(10);

                {
                    revokeBtn.setOnAction(e -> {
                        // Handle revoke button action here
                    });

                    downloadBtn.setOnAction(e -> {
                        // Handle download button action here
                    });

                    deleteBtn.setOnAction(e -> {
                        // Handle delete button action here
                    });

                    rotateBtn.setOnAction(e -> {
                        // Handle rotate button action here
                    });

                    unrevokeBtn.setOnAction(e -> {
                        // Handle unrevoke button action here
                    });
                }

                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (!empty) {
                        String status = getTableView().getItems().get(getIndex()).get("AccountStatus").getAsString();

                        hbox.getChildren().clear();

                        if (status.equalsIgnoreCase("Active")) {
                            hbox.getChildren().addAll(revokeBtn, downloadBtn);
                        } else if (status.equalsIgnoreCase("Revoked")) {
                            hbox.getChildren().addAll(deleteBtn, rotateBtn, unrevokeBtn);
                        } else {
                            hbox.getChildren().addAll(new Button()); //empty cell
                        }
                        setGraphic(hbox);
                    } else {
                        setGraphic(null);
                    }
                }
            };
        });
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

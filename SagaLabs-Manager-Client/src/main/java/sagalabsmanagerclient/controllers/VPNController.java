package sagalabsmanagerclient.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import sagalabsmanagerclient.VPNServiceConnection;
import sagalabsmanagerclient.View;
import sagalabsmanagerclient.ViewSwitcher;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


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
        initializeColumns();
        // Create the CellFactory for the userVPNButtons column
        userVPNButtons.setCellFactory(new Callback<TableColumn<JsonObject, String>, TableCell<JsonObject, String>>() {
            @Override
            public TableCell<JsonObject, String> call(TableColumn<JsonObject, String> param) {
                return createButtonCellFactory();
            }
        });
        try {
            listVpn();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the cell value factories for the TableView columns.
     */
    private void initializeColumns() {
        // Set the cell value factory for the userVPNName column
        userVPNName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .get("Identity")
                .getAsString()));

        // Set the cell value factory for the userVPNStatus column
        userVPNStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .get("AccountStatus")
                .getAsString()));

        // Set the cell value factory for the userVPNOnline column
        userVPNOnline.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()
                .get("Connections")
                .getAsString()));
    }
    /**
     * Creates a CellFactory for the userVPNButtons column.
     * @return A TableCell instance for the userVPNButtons column.
     */
    private TableCell<JsonObject, String> createButtonCellFactory() {
        return new TableCell<JsonObject, String>() {
            final Button revokeBtn = new Button("Revoke");
            final Button downloadBtn = new Button("Download");
            final Button deleteBtn = new Button("Delete");
            final Button rotateBtn = new Button("Rotate");
            final Button unrevokeBtn = new Button("Unrevoke");
            final HBox hbox = new HBox(10);
            {
                // Set the action handlers for each button
                revokeBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        handleRevokeButton(getIndex());
                    }
                });
                downloadBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        handleDownloadButton(getIndex());
                    }
                });
                deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        handleDeleteButton(getIndex());
                    }
                });
                rotateBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        handleRotateButton(getIndex());
                    }
                });
                unrevokeBtn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                        handleUnrevokeButton(getIndex());
                    }
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
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
    }
    /**
     * Handles the action for the revoke button.
     * @param index The index of the selected item in the TableView.
     */
    private void handleRevokeButton(int index) {
        JsonObject vpnUser = userVpnTableView.getItems().get(index);
        String vpnIp = vpnUser.get("vpnIp").getAsString();
        String username = vpnUser.get("Identity").getAsString();
        try {
            VPNServiceConnection.revokeCertificate(vpnIp, username);
            listVpn();
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Handles the action for the download button.
     * @param index The index of the selected item in the TableView.
     */
    private void handleDownloadButton(int index) {
        JsonObject vpnUser = userVpnTableView.getItems().get(index);
        String vpnIp = vpnUser.get("vpnIp").getAsString();
        String username = vpnUser.get("Identity").getAsString();
        try {
            VPNServiceConnection.downloadConfig(vpnIp, username);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Handles the action for the delete button.
     * @param index The index of the selected item in the TableView.
     */
    private void handleDeleteButton(int index) {
        JsonObject vpnUser = userVpnTableView.getItems().get(index);
        System.out.println(userVpnTableView.getItems().get(index));
        String vpnIp = vpnUser.get("vpnIp").getAsString();
        String username = vpnUser.get("Identity").getAsString();
        try {
            VPNServiceConnection.deleteUser(vpnIp, username);
            // Remove the deleted user from the ObservableList
            userVpnTableView.getItems().remove(index);

            listVpn();

        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Handles the action for the rotate button.
     * @param index The index of the selected item in the TableView.
     */
    private void handleRotateButton(int index) {
        JsonObject vpnUser = userVpnTableView.getItems().get(index);
        System.out.println(userVpnTableView.getItems().get(index));
        String vpnIp = vpnUser.get("vpnIp").getAsString();
        String username = vpnUser.get("Identity").getAsString();
        try {
            VPNServiceConnection.rotateCertificate(vpnIp, username);
            listVpn();
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Handles the action for the unrevoke button.
     * @param index The index of the selected item in the TableView.
     */
    private void handleUnrevokeButton(int index) {
        JsonObject vpnUser = userVpnTableView.getItems().get(index);
        System.out.println(userVpnTableView.getItems().get(index));
        String vpnIp = vpnUser.get("vpnIp").getAsString();
        String username = vpnUser.get("Identity").getAsString();
        try {
            VPNServiceConnection.unrevokeCertificate(vpnIp, username);
            listVpn();
        } catch (IOException | SQLException ex) {
            ex.printStackTrace();
        }
    }
    @FXML
    public void loadData() {
        try {
            listVpn();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
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
        // Update the TableView on the JavaFX Application thread
        Platform.runLater(() -> {
            userVpnTableView.getItems().clear();
            userVpnTableView.getItems().addAll(vpnUsers);
        });
    }
}
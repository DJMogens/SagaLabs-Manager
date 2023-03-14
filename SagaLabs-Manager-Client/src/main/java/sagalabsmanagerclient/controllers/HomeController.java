package sagalabsmanagerclient.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sagalabsmanagerclient.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeController extends MenuController {

    @FXML
    private AnchorPane anchorHome;

    public void initialize() throws SQLException {
        ResultSet labs = Database.executeSql("SELECT LabName, vpnRunning, VmCount FROM Labs");

        VBox vbox = new VBox();
        vbox.setSpacing(10);

        while (labs.next()) {
            String labName = labs.getString("LabName");
            boolean vpnRunning = labs.getBoolean("vpnRunning");
            int vmCount = labs.getInt("VmCount");

            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setStyle("-fx-background-color: gray; -fx-border-color: #333333; -fx-border-width: 2px; -fx-border-style: solid;");

            Rectangle box = new Rectangle(100, 100, vpnRunning ? Color.GREEN : Color.BLUE);

            VBox labelBox = new VBox();
            labelBox.setSpacing(5);
            Label nameLabel = new Label(labName);
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            Label vmLabel = new Label("VM's: " + vmCount);
            vmLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            Label vpnStatusLabel = new Label(vpnRunning ? "VPN: Running" : "VPN: Down");
            vpnStatusLabel.setTextFill(vpnRunning ? Color.GREEN : Color.RED);

            labelBox.getChildren().addAll(nameLabel, vmLabel, vpnStatusLabel);

            Button turnOnButton = new Button("Turn On all machines");

            Button turnOffButton = new Button("Turn Off all machines");

            Label statusLabel = new Label("Starting up...");
            statusLabel.setVisible(false);

            turnOnButton.setOnAction(event -> {
                statusLabel.setVisible(true);
                turnOnButton.setDisable(true);

                // Code to start up the VMs
            });

            turnOffButton.setOnAction(event -> {
                // Code to turn off all the machines
            });

            hbox.getChildren().addAll(box, labelBox, turnOnButton, turnOffButton);

            vbox.getChildren().add(hbox);
        }

        anchorHome.getChildren().add(vbox);
    }


}
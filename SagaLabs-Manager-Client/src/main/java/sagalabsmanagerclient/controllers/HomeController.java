package sagalabsmanagerclient.controllers;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import sagalabsmanagerclient.AzureMethods;
import sagalabsmanagerclient.Database;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeController extends MenuController {

    @FXML
    private AnchorPane anchorHome;

    public void initialize() throws SQLException {
        // Execute a SQL query to retrieve information about labs
        ResultSet labs = Database.executeSql("SELECT LabName, vpnRunning, VmCount FROM Labs");

        // Create a VBox to hold all the lab information boxes
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        // Loop through the labs ResultSet and create an HBox for each lab
        while (labs.next()) {
            // Retrieve lab information from the ResultSet
            String labName = labs.getString("LabName");
            boolean vpnRunning = labs.getBoolean("vpnRunning");
            int vmCount = labs.getInt("VmCount");

            // Create an HBox to hold all the lab information elements
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setStyle("-fx-background-color: gray; -fx-border-color: #333333; -fx-border-width: 2px; -fx-border-style: solid;");

            // Create a colored Rectangle to represent the VPN status
            Rectangle box = new Rectangle(100, 100, vpnRunning ? Color.GREEN : Color.BLUE);

            // Create a VBox to hold all the lab labels
            VBox labelBox = new VBox();
            labelBox.setSpacing(5);
            Label nameLabel = new Label(labName);
            nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            Label vmLabel = new Label("VM's: " + vmCount);
            vmLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
            Label vpnStatusLabel = new Label(vpnRunning ? "VPN: Running" : "VPN: Down");
            vpnStatusLabel.setTextFill(vpnRunning ? Color.GREEN : Color.RED);

            labelBox.getChildren().addAll(nameLabel, vmLabel, vpnStatusLabel);

            // Create buttons to turn on/off all VMs for this lab
            Button turnOnButton = new Button("Turn On all machines");
            Button turnOffButton = new Button("Turn Off all machines");

            // Create a label to display startup status
            Label statusLabel = new Label("Starting up...");
            statusLabel.setVisible(false);

            // Set up the turn on button event handler
            turnOnButton.setOnAction(event -> {
                Runnable task = () -> {
                    AzureMethods.turnOnInLab(labName);
                    Platform.runLater(() -> {
                        // Update UI with success message
                    });
                };

                Thread thread = new Thread(task);
                thread.start();
            });

            // Set up the turn off button event handler
            turnOffButton.setOnAction(event -> {
                Runnable task = () -> {
                    AzureMethods.turnOffVMsInLab(labName);
                    Platform.runLater(() -> {
                        // Update UI with success message
                    });
                };

                Thread thread = new Thread(task);
                thread.start();
            });

            // Add all the elements to the HBox
            hbox.getChildren().addAll(box, labelBox, turnOnButton, turnOffButton);

            // Add the HBox to the VBox
            vbox.getChildren().add(hbox);
        }

        // Add the VBox to the anchor pane in the scene
        anchorHome.getChildren().add(vbox);
    }

}